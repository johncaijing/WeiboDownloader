/*
 * Created by johntsai on 2019-09-01
 */
interface IDownloader<T, I> {

    fun analyseUrls(type: T): List<I>

    fun download(type: T)

}
