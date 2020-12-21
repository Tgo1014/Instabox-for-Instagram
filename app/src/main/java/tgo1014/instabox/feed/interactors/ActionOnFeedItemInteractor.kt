package tgo1014.instabox.feed.interactors

import tgo1014.instabox.common.BaseInteractor
import tgo1014.instabox.common.network.InstagramApi
import tgo1014.instabox.feed.models.FeedItem

class ActionOnFeedItemInteractor(
    private val instagramApi: InstagramApi,
) : BaseInteractor<Unit> {

    var input: Input? = null

    data class Input(val feedItem: FeedItem)

    override suspend fun execute() {
        require(input != null)
        instagramApi.changeMediaVisibility(
            input?.feedItem?.id ?: "",
            if (input?.feedItem?.isArchived == true) "undo_only_me" else "only_me",
            input?.feedItem?.id ?: ""
        )
    }
}