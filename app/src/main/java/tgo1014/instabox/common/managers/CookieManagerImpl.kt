package tgo1014.instabox.common.managers

import javax.inject.Inject

class CookieManagerImpl @Inject constructor(
    private val cookieManager: android.webkit.CookieManager,
) : CookieManager {
    override fun getCookie(url: String?): String? = cookieManager.getCookie(url)
}