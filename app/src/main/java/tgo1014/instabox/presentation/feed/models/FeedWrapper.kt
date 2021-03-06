package tgo1014.instabox.presentation.feed.models

data class FeedWrapper(
    val feedItems: List<FeedItem>,
    val nextPageMaxId: String? = null,
    val isArchive: Boolean,
    val moreResultAvailable: Boolean,
)