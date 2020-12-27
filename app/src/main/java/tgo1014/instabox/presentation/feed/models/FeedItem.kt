package tgo1014.instabox.presentation.feed.models

data class FeedItem(
    val id: String,
    val thumbUrl: String,
    val imageUrl: String,
    val mediaType: FeedMediaType,
    val isArchived: Boolean,
)