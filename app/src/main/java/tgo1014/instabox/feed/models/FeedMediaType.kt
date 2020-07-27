package tgo1014.instabox.feed.models

enum class FeedMediaType(val id: Int) {

    PHOTO(1),
    VIDEO(2),
    ALBUM(8),
    UNKNOWN(-1);

    companion object {
        @JvmStatic
        fun find(id: Long? = null): FeedMediaType {
            if (id == null) return UNKNOWN
            return values().firstOrNull { it.id.toLong() == id } ?: UNKNOWN
        }
    }

}