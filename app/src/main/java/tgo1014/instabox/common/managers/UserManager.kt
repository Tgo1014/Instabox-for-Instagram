package tgo1014.instabox.common.managers

interface UserManager {

    val isUserLogged: Boolean
    var token: String
    var userId: String
    var sessionId: String

    fun getFormattedUserAgent() : String

}