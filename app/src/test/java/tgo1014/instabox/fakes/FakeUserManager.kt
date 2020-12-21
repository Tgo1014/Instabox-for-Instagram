package tgo1014.instabox.fakes

import tgo1014.instabox.common.managers.UserManager

class FakeUserManager(
    override val isUserLogged: Boolean,
    override var token: String = "",
    override var userId: String = "",
    override var sessionId: String = "",
) : UserManager {
    override fun getFormattedUserAgent() = ""
}