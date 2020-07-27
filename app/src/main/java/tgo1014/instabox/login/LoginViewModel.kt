package tgo1014.instabox.login

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import tgo1014.instabox.common.managers.CookieManager
import tgo1014.instabox.common.managers.UserManager
import tgo1014.instabox.common.utils.substringBetween

class LoginViewModel(
    private val userManager: UserManager,
    private val cookieManager: CookieManager
) : ViewModel() {

    private val _state = MutableLiveData<LoginState>()
    val state: LiveData<LoginState> get() = _state
    val instaLoginUrl = "https://www.instagram.com/accounts/login/"
    val webviewClient by lazy { getWebClient() }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun verifyIfUserIsLogged() {
        if (userManager.isUserLogged) {
            stateLoggedSuccessfully()
            return
        }
        stateHasToLogin()
    }

    @VisibleForTesting
    fun handleCookie(url: String?) {
        val cookie = cookieManager.getCookie(url)
        val token = cookie?.substringBetween("csrftoken=", ";")
        val userId = cookie?.substringBetween("ds_user_id=", ";")
        val sessionId = cookie?.substringBetween("sessionid=", ";")
        if (isValidLogin(token, userId, sessionId)) {
            userManager.userId = userId!!
            userManager.token = token!!
            userManager.sessionId = sessionId!!
            stateLoggedSuccessfully()
        }
    }

    @VisibleForTesting
    fun isValidLogin(token: String?, userId: String?, sessionId: String?): Boolean {
        if (token.isNullOrEmpty()) return false
        if (userId.isNullOrEmpty()) return false
        if (sessionId.isNullOrEmpty()) return false
        if (userId == "ig_cb=1") return false
        if (sessionId == "ig_cb=1") return false
        return true
    }

    private fun getWebClient() = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            handleCookie(url)
        }
    }

    private fun stateLoggedSuccessfully() {
        _state.value = LoginState.UserLoggedSuccessfully
    }

    private fun stateHasToLogin() {
        _state.value = LoginState.UserHasToLogin
    }

}