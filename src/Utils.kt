import okhttp3.Request
import java.util.regex.Pattern

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
        val client = NetworkManager.instance.getClient().newBuilder()
            .followRedirects(false)
            .followSslRedirects(false).build()
        val request = Request.Builder()
            .header("User-Agent", Constant.UserAgent)
            .url(path)
            .build()

        val response = client.newCall(request).execute()
        if (response.code == 302) {
            val location = response.header("Location", "")!!
            response.body?.close()
            return if (location.isNotEmpty() && location.length > 27) "107603${location.substring(27)}" else ""
        } else {
            response.body?.close()
        }
    }

    if (this.name.isNotEmpty()) {
        //FIXME 这个请求获取不到containerId
        val url = "https://weibo.cn/${this.name}"
        val client = NetworkManager.instance.getClient()

        val request = Request.Builder()
            .addHeader("User-Agent", Constant.UserAgent)
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        val html = response.body?.string()
        if (html?.isNotEmpty()!!) {
            val pattern = Pattern.compile("href=\"/([\\d]*?)/info\"")
            val matcher = pattern.matcher(html)
            while (matcher.find()) {
                return "107603${matcher.group(1)}"
            }
        }
        response.body?.close()
    }
    return ""
}


fun String.getSuffix(separator: String): String {
    val index = lastIndexOf(separator)
    return if(index == -1) "" else substring(index)
}
