package tgo1014.instabox.common.managers

class CookieManagerImpl(private val cookieManager: android.webkit.CookieManager) : CookieManager {
    override fun getCookie(url: String?): String? = cookieManager.getCookie(url)
}