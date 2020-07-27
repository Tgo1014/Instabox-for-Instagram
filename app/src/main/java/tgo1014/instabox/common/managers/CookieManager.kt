package tgo1014.instabox.common.managers

interface CookieManager {
    fun getCookie(url: String?): String?
}