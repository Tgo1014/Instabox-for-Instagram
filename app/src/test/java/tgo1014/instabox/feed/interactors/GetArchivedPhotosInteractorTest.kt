package tgo1014.instabox.feed.interactors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import tgo1014.instabox.TestHelper.generateRetrofit
import tgo1014.instabox.TestHelper.setResponse
import tgo1014.instabox.common.network.InstagramApi

@ExperimentalCoroutinesApi
class GetArchivedPhotosInteractorTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private val mockWebServer = MockWebServer()
    private val instagramApi by lazy {
        generateRetrofit(mockWebServer).create(InstagramApi::class.java)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockWebServer.start()
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun archiveFeed_hasResults() = runBlocking {
        // Given An Archived Feed API Fetch
        val interactor = GetArchivedPhotosInteractor(instagramApi)
        // When Response Has Results
        mockWebServer.setResponse("feed_response.json")
        val result = interactor.execute()
        // Then FeedItems Should Not Be Empty
        assert(result.feedItems.isNotEmpty())
        // Then Result Should Be Flagged As Archive
        assert(result.isArchive)
    }

    @Test
    fun feedNextPage_correctId() = runBlocking {
        // Given An Archived Feed API Fetch
        val interactor = GetArchivedPhotosInteractor(instagramApi).apply {
            input = GetArchivedPhotosInteractor.Input("test")
        }
        // When Using A Correct MaxId Param
        mockWebServer.setResponse("feed_response.json")
        val result = interactor.execute()
        // Then FeedItems Should Not Be Empty
        assert(result.feedItems.isNotEmpty())
    }

    @Test
    fun feedNextPage_incorrectId() = runBlocking {
        // Given An Archived Feed API Fetch
        val interactor = GetArchivedPhotosInteractor(instagramApi).apply {
            input = GetArchivedPhotosInteractor.Input("test")
        }
        // When Using A Wrong MaxId Param
        mockWebServer.setResponse("empty_response.json")
        val result = interactor.execute()
        // Then FeedItems Should Be Empty
        assert(result.feedItems.isEmpty())
    }
}
