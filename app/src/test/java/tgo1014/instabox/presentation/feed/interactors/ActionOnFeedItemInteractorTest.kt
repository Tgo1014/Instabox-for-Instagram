package tgo1014.instabox.presentation.feed.interactors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import retrofit2.HttpException
import tgo1014.instabox.TestHelper
import tgo1014.instabox.TestHelper.fakeFeedItem
import tgo1014.instabox.TestHelper.generateRetrofit
import tgo1014.instabox.TestHelper.setResponse
import tgo1014.instabox.network.InstagramApi

@ExperimentalCoroutinesApi
class ActionOnFeedItemInteractorTest {

    @get:Rule
    var thrown: ExpectedException = ExpectedException.none()

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
    fun changeMedia_validFeedItem() {
        // Given A Change Media Visibility API Call
        val interactor = ActionOnFeedItemInteractor(instagramApi)
        // When Input Is Valid FeedItem
        val input = fakeFeedItem
        mockWebServer.setResponse("change_media_response.json")
        // Then Should Run
        runBlocking { interactor.invoke(input) }
    }

    @Test
    fun changeMedia_invalidFeedItem() {
        // Given A Change Media Visibility API Call
        val interactor = ActionOnFeedItemInteractor(instagramApi)
        // When Input Is Invalid FeedItem
        val input = fakeFeedItem
        mockWebServer.setResponse("change_media_response_error.json", 400)
        // Then Should Run
        thrown.expect(HttpException::class.java)
        runBlocking { interactor.invoke(input) }
    }
}