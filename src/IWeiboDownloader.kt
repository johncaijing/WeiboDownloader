/*
 * Created by johntsai on 2019-09-01
 */
interface IWeiboDownloader {

    fun analyseWeiboUrls(type: WeiboType): List<WeiboInfo>

    fun download(type: WeiboType = WeiboType.IMAGE)

}
