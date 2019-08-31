import okhttp3.OkHttpClient
import okhttp3.Request

/*
 * Created by johntsai on 2019-08-31
 */
fun WeiboUser.transformToContainerId(): String {
    if (this.containerId.isNotEmpty()) {
        return this.containerId
    }

    if (this.id.isNotEmpty()) {
        return "107603${this.id}"
    }

    if (this.nickName.isNotEmpty()) {

        val path = "http://m.weibo.com/n/${this.nickName}"
        val client = OkHttpClient().newBuilder()
            .followRedirects(false)
            .followSslRedirects(false).build()
        val request = Request.Builder()
            .header("User-Agent", Constant.UserAgent)
            .url(path)
            .build()

        val response = client.newCall(request).execute()
        if (response.code == 302) {
            val location = response.header("Location", "")!!
            return if (location.isNotEmpty() && location.length > 27) location.substring(27) else ""
        }
    }



    return ""

}
