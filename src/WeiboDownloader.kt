import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/*
 * Created by johntsai on 2019-08-31
 */
class WeiboDownloader(val user: WeiboUser) : IWeiboDownloader {

    private var okhttpClient: OkHttpClient? = null
    init {
        okhttpClient = OkHttpClient().newBuilder()
            .connectTimeout(3000,TimeUnit.MILLISECONDS)
            .build()
    }

    override fun analyseWeiboUrls(type: WeiboType): List<String> {

        val result = ArrayList<String>()
        var page = 1
        while (true) {
            val urls = getUrls(page, type)
            if(urls.isEmpty()) {
                break
            } else {
                result.addAll(urls)
                page++
                Thread.sleep(1000)
            }
        }

        return result
    }

    private fun getUrls(page: Int, type: WeiboType):List<String> {

        val url = "https://m.weibo.cn/api/container/getIndex?count=25&page=$page&containerid=${this.user.containerId}"

        val request = Request.Builder().url(url).header("User-Agent",Constant.UserAgent).build()

        val response = okhttpClient?.newCall(request)?.execute()


        return emptyList()

    }

    override fun download(type: WeiboType) {
        if(this.user.containerId.isEmpty()) {
            return
        }
        val urls = analyseWeiboUrls(type)

    }


}
