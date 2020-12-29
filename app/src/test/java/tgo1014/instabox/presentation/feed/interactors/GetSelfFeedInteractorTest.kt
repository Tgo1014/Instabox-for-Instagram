package tgo1014.instabox.presentation.feed.interactors

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import tgo1014.instabox.MainCoroutineRule
import tgo1014.instabox.TestHelper
import tgo1014.instabox.TestHelper.setResponse
import tgo1014.instabox.managers.UserManager
import tgo1014.instabox.network.InstagramApi

@ExperimentalCoroutinesApi
class GetSelfFeedInteractorTest {

    // Set the main coroutines dispatcher for unit testing
    @get:Rule
    var coroutinesRule = MainCoroutineRule()

    private var userManager: UserManager = mock()
    private val mockWebServer = MockWebServer()
    private val instagramApi by lazy {
        TestHelper.generateRetrofit(mockWebServer).create(InstagramApi::class.java)
    }

    @Before
    fun setup() {
        mockWebServer.start()
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun selfFeed_loggedUser() = runBlocking {
        // Given A Self User Feed API Fetch
        val interactor = GetSelfFeedInteractor(instagramApi, userManager)
        // When Getting Feed With Logged User
        whenever(userManager.userId).thenReturn("123")
        mockWebServer.setResponse("feed_response.json")
        val result = interactor.invoke()
        // Then FeedItems Should Not Be Empty
        assert(result.feedItems.isNotEmpty())
    }

    @Test
    fun selfFeed_withoutUser() {
        // Given A Self User Feed API Fetch
        val interactor = GetSelfFeedInteractor(instagramApi, userManager)
        // When Feed Without Logged User
        whenever(userManager.userId).thenReturn("")
        mockWebServer.setResponse("page_not_found.html", 404)
        // Then Should Throw HttpException
        Assert.assertThrows(HttpException::class.java) {
            runBlocking { interactor.invoke() }
        }
    }
}