package tgo1014.instabox.feed.interactors

import tgo1014.instabox.common.network.InstagramApi
import tgo1014.instabox.feed.models.FeedItem
import javax.inject.Inject

class ActionOnFeedItemInteractor @Inject constructor(
    private val instagramApi: InstagramApi,
) {
    suspend operator fun invoke(feedItem: FeedItem) {
        instagramApi.changeMediaVisibility(
            mediaId = feedItem.id,
            action = if (feedItem.isArchived) "undo_only_me" else "only_me",
            mediaIdForm = feedItem.id
        )
    }
}