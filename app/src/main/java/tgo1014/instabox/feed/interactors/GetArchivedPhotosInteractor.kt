package tgo1014.instabox.feed.interactors

import tgo1014.instabox.common.BaseInteractor
import tgo1014.instabox.common.network.InstagramApi
import tgo1014.instabox.common.network.responses.FeedResponse
import tgo1014.instabox.feed.models.FeedItem
import tgo1014.instabox.feed.models.FeedMediaType
import tgo1014.instabox.feed.models.FeedWrapper
import javax.inject.Inject

class GetArchivedPhotosInteractor @Inject constructor(
    private val instagramApi: InstagramApi,
) : BaseInteractor<FeedWrapper> {

    var input: Input? = null

    data class Input(val maxId: String? = null)

    override suspend fun execute(): FeedWrapper {
        val feedResponse = instagramApi.getArchivedFeed(input?.maxId)
        val feedItems = feedResponse.items?.filter { it?.id != null }?.map {
            FeedItem(
                id = it?.id ?: "",
                thumbUrl = getThumb(it),
                imageUrl = getImage(it),
                mediaType = FeedMediaType.find(it?.mediaType),
                isArchived = true
            )
        } ?: arrayListOf()

        return FeedWrapper(
            feedItems,
            feedResponse.nextMaxId,
            true,
            feedResponse.moreAvailable ?: false
        )
    }

    private fun getImage(item: FeedResponse.Item?): String {
        if (item == null) return ""
        return getImageFromAlbum(item) ?: getImageFromItem(item) ?: ""
    }

    private fun getImageFromItem(item: FeedResponse.Item): String? =
        item.imageVersions2?.candidates?.maxBy { it?.height ?: 0 }?.url

    private fun getImageFromAlbum(item: FeedResponse.Item): String? =
        item.carouselMedia?.firstOrNull()?.imageVersions2?.candidates?.maxBy {
            it?.height ?: 0
        }?.url

    private fun getThumb(item: FeedResponse.Item?): String {
        if (item == null) return ""
        return getThumbFromAlbum(item) ?: getThumbFromItem(item) ?: ""
    }

    private fun getThumbFromItem(item: FeedResponse.Item): String? =
        item.imageVersions2?.candidates?.minBy { it?.height ?: 0 }?.url

    private fun getThumbFromAlbum(item: FeedResponse.Item): String? =
        item.carouselMedia?.firstOrNull()?.imageVersions2?.candidates?.minBy {
            it?.height ?: 0
        }?.url
}