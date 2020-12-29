package tgo1014.instabox.presentation.feed.models

sealed class FeedState {
    object Init : FeedState()
    object UserHasToLogin : FeedState()
    object UserLoggedSuccesfully : FeedState()
    object Loading : FeedState()
    class FeedSuccess(val feedItems: List<FeedItem>) : FeedState()
    class FeedActionRunning(
        val actionAlreadyDoneSize: Int,
        val itemsToPerfomeActionSize: Int,
        val lastRemovedItem: FeedItem? = null,
    ) : FeedState()
    class FeedItemActionSuccess(vararg val feedItem: FeedItem) : FeedState()
    class Error(val e: Exception) : FeedState()
}
