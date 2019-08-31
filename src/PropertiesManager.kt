import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

/*
 * Created by johntsai on 2019-08-31
 */
class PropertiesManager private constructor() {

    private var properties: Properties? = null
    private var inputStream: InputStream? = null

    init {
        inputStream = FileInputStream(Constant.DOWNLOAD_PROPERTIES_PATH)
        properties = Properties()
        properties!!.load(inputStream)
    }

    companion object {
        val instance = PropertiesManagerHolder.propertiesManager
    }

    private object PropertiesManagerHolder {
        val propertiesManager = PropertiesManager()
    }


    fun read(key: String): String {
        val value = properties?.get(key)
        return value?.toString() ?: ""
    }

    fun write(key: String, value: String) {
        properties?.set(key, value)
        properties?.store(FileOutputStream(Constant.DOWNLOAD_PROPERTIES_PATH),"")
    }

    fun release() {
        inputStream?.close()
    }


}
