import okio.buffer
import okio.sink
import java.io.File
import java.util.concurrent.CountDownLatch

/*
 * Created by johntsai on 2019-09-01
 */
class ImageDownloadTask(val url: String, val path: String, val name: String, val downLatch: CountDownLatch) : Runnable {

    override fun run() {
        try {
            val byteArray = NetworkManager.instance.downloadFile(url)
            val suffix = url.getSuffix(".")
            val fileFullName = if (suffix.isEmpty()) "$name.jpg" else "$name$suffix"
            val file = File(path, fileFullName)
            val sink = file.sink().buffer()
            if(byteArray != null) {
                sink.write(byteArray)
            }

        } catch (e: Exception) {
            println("$name download failed: ${e.message}")
        } finally {
            downLatch.countDown()
        }

    }

}
