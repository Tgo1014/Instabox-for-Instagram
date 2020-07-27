package tgo1014.instabox.common.managers

interface AdManager {
    fun preloadRewardAd()
    fun showRewardAd(onReward: () -> Unit, onFail: () -> Unit)
    suspend fun isEu(): Boolean
}