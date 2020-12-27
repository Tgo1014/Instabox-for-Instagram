package tgo1014.instabox.presentation.feed.models

import org.junit.Test

class FeedMediaTypeTest {

    @Test
    fun find_rightId() {
        // Given A List of Correct IDs
        val ids = FeedMediaType.values()
            .filter { it != FeedMediaType.UNKNOWN }
            .map { it.id }
        // When Searching for it
        val list = ids.map { FeedMediaType.find(it.toLong()) }
        // Then Should find all
        assert(!list.contains(FeedMediaType.UNKNOWN))
    }

    @Test
    fun find_wrongId() {
        // Given A Wrong Id
        val id: Long = -1
        // When Searching For It
        val enum = FeedMediaType.find(id)
        // Then Should Be Unknown
        assert(enum == FeedMediaType.UNKNOWN)
    }
}