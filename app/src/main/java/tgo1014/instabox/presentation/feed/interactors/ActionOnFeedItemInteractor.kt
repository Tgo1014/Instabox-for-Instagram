package tgo1014.instabox.presentation.feed.interactors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tgo1014.instabox.network.InstagramApi
import tgo1014.instabox.presentation.feed.models.FeedItem
import javax.inject.Inject

class ActionOnFeedItemInteractor @Inject constructor(
    private val instagramApi: InstagramApi,
) {
    suspend operator fun invoke(feedItem: FeedItem) {
        withContext(Dispatchers.IO) {
            instagramApi.changeMediaVisibility(
                mediaId = feedItem.id,
                action = if (feedItem.isArchived) "undo_only_me" else "only_me",
                mediaIdForm = feedItem.id
            )
        }
    }
}