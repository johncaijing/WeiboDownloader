import okio.buffer
import okio.sink
import java.io.File
import java.util.concurrent.CountDownLatch

/*
 * Created by johntsai on 2019-09-01
 */
class ImageDownloadTask(val info: WeiboInfo, val path: String, val name: String, val downLatch: CountDownLatch) : Runnable {

    override fun run() {
        try {
            val byteArray = NetworkManager.instance.downloadFile(info.url)
            var fileFullName = ""
            if(info.type == WeiboType.IMAGE) {
                val suffix = info.url.getSuffix(".")
                fileFullName = if (suffix.isEmpty()) "$name.jpg" else "$name$suffix"
            } else if(info.type == WeiboType.VIDEO) {
                fileFullName = "$name.mp4"
            }
            val file = File(path, fileFullName)
            val sink = file.sink().buffer()
            if(byteArray != null) {
                sink.write(byteArray)
            }

        } catch (e: Exception) {
            println("$name download failed: $e")
        } finally {
            downLatch.countDown()
        }

    }

}
