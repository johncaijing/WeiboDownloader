/*
 * Created by johntsai on 2019-09-01
 */
data class WeiboInfo(var type: WeiboType, var url: String) {
    constructor() : this(WeiboType.IMAGE, "")
}
