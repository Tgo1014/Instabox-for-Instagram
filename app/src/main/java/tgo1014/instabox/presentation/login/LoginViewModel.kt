package tgo1014.instabox.presentation.login

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.VisibleForTesting
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tgo1014.instabox.managers.CookieManager
import tgo1014.instabox.managers.UserManager
import tgo1014.instabox.utils.substringBetween

class LoginViewModel @ViewModelInject constructor(
    private val userManager: UserManager,
    private val cookieManager: CookieManager,
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Init)
    val state: StateFlow<LoginState> get() = _state

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