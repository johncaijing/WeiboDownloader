import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/*
 * Created by johntsai on 2019-08-31
 */
class WeiboDownloader(val user: WeiboUser, val path: String) : IWeiboDownloader {

    override fun analyseWeiboUrls(type: WeiboType): List<String> {

        val result = ArrayList<String>()
        var page = 1
        println("analysing weibo")
        while (true) {
            val urls = getUrls(page, type)
            if (urls.isEmpty()) {
                break
            } else {
                Thread.sleep(1000)
                result.addAll(urls)
                Thread.sleep(1000)
                page++
                print("......")
            }
        }

        return result
    }

    private fun getUrls(page: Int, type: WeiboType): List<String> {

        val url = "https://m.weibo.cn/api/container/getIndex?count=25&page=$page&containerid=${this.user.containerId}"

        val request = Request.Builder().url(url).header("User-Agent", Constant.UserAgent).build()

        val response = NetworkManager.instance.getClient().newCall(request).execute()

        val mBlogs = ArrayList<JSONObject>()
        if (response.code == 200) {
            val responseStr = response.body?.string()
            val jsonObject = JSONObject(responseStr)
            if (jsonObject.has("data")) {
                val data = jsonObject.optJSONObject("data")
                val cards = data.optJSONArray("cards")
                cards?.forEach {
                    if (it is JSONObject) {
                        val mBlog = it.optJSONObject("mblog")
                        if (mBlog != null) {
                            mBlogs.add(mBlog)
                        }
                    }
                }

            }
        }
        response.close()
        return transformMBlogToUrl(type, mBlogs)
    }

    private fun transformMBlogToUrl(type: WeiboType, mBlogs: List<JSONObject>): List<String> {
        val result = ArrayList<String>()
        when (type) {
            WeiboType.IMAGE -> {
                mBlogs.forEach {
                    if (it.has("pics")) {
                        val pics = it.optJSONArray("pics")
                        pics?.forEach { pic ->
                            if (pic is JSONObject && pic.has("large")) {
                                val large = pic.optJSONObject("large")
                                if (large != null && large.has("url")) {
                                    result.add(large.getString("url"))
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                return emptyList()
            }
        }
        return result
    }

    override fun download(type: WeiboType) {
        if (this.user.containerId.isEmpty()) {
            return
        }
        val urls = analyseWeiboUrls(type)

        println("The size of files: ${urls.size}")

        val executorService = Executors.newFixedThreadPool(8)

        val downLatch = CountDownLatch(urls.size);

        urls.forEachIndexed { index, s ->
            executorService.submit(ImageDownloadTask(s, path, index.toString(), downLatch))
        }

        downLatch.await()
        executorService.shutdown()


    }


}
