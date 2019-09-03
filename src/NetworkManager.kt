import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import java.io.ByteArrayOutputStream
import java.io.InputStream


/*
 * Created by johntsai on 2019-09-01
 */
class NetworkManager {

    private val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(2, TimeUnit.MINUTES)
        .readTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .build()

    companion object {
        val instance = HttpManagerHolder.managerHolder
    }

    private object HttpManagerHolder {
        val managerHolder = NetworkManager()
    }

    fun getClient(): OkHttpClient {
        return okHttpClient
    }

    fun downloadFile(url: String): ByteArray? {
        val request = Request.Builder().url(url).addHeader("User-Agent", Constant.UserAgent).build()
        var result: ByteArray? = null
        getClient().newCall(request).execute().body?.use { body ->
            val byteStream = body.byteStream()
            result = inputStreamToByteArray(byteStream)
            body.close()
        }
        return result
    }

    private fun inputStreamToByteArray(inputStream: InputStream): ByteArray {
        val baos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        while (inputStream.read(buffer).let {
                len = it
                it != -1
            }) {
            baos.write(buffer, 0, len)
        }
        val b = baos.toByteArray()
        inputStream.close()
        baos.close()
        return b
    }

}
