import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/*
 * Created by johntsai on 2019-08-31
 */
class WeiboDownloader(val user: WeiboUser, val path: String) : IWeiboDownloader {

    override fun analyseWeiboUrls(type: WeiboType): List<WeiboInfo> {

        val result = ArrayList<WeiboInfo>()
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

    private fun getUrls(page: Int, type: WeiboType): List<WeiboInfo> {

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
        response.body?.close()
        return transformMBlogToUrl(mBlogs)
    }

    private fun transformMBlogToUrl(mBlogs: List<JSONObject>): List<WeiboInfo> {
        val result = ArrayList<WeiboInfo>()
        mBlogs.forEach {
            if (it.has("pics")) {
                val pics = it.optJSONArray("pics")
                pics?.forEach { pic ->
                    if (pic is JSONObject && pic.has("large")) {
                        val large = pic.optJSONObject("large")
                        if (large != null && large.has("url")) {
                            result.add(WeiboInfo(WeiboType.IMAGE, large.getString("url")))
                        }
                    }
                }
            } else if (it.has("page_info")) {
                val pageInfo = it.optJSONObject("page_info")
                val mediaInfo = pageInfo.optJSONObject("media_info")
                if (mediaInfo != null) {
                    val mp4Url = mediaInfo.optString("mp4_720p_mp4")
                    if (!mp4Url.isNullOrEmpty()) {
                        result.add(WeiboInfo(WeiboType.VIDEO, mp4Url))
                    }
                }
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
