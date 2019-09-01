import java.util.*

/*
 * Created by johntsai on 2019-08-31
 */
fun main() {

    val scanner = Scanner(System.`in`)

    var path = PropertiesManager.instance.read(Constant.KEY_DOWNLOAD_PATH)
    if (path.isEmpty()) {
        println("Please input the path of downloading files:")
        path = scanner.nextLine()
        PropertiesManager.instance.write(Constant.KEY_DOWNLOAD_PATH, path)
    }

    PropertiesManager.instance.release()

    println("Please input the weibo user's info:")
    println("1:User Id")
    println("2:User name")
    println("3:User nickname")

    val input = scanner.nextInt()

    if (input < 0 || input > 3) {
        scanner.close()
        return
    }

    val user = WeiboUser()

    when (input) {
        1 -> {
            println("User Id:")
            user.id = scanner.next().trim()
        }
        2 -> {
            println("User name:")
            user.name = scanner.next().trim()
        }
        3 -> {
            println("User nickname:")
            user.nickName = scanner.next().trim()
        }
    }

    scanner.close()

    val containerId = user.transformToContainerId()
    user.containerId = containerId

    val downloader = WeiboDownloader(user)
    downloader.download()

}
