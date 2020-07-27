package tgo1014.instabox.feed.interactors


import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
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
import tgo1014.instabox.TestHelper.setResponse
import tgo1014.instabox.common.managers.UserManager
import tgo1014.instabox.common.network.InstagramApi

@ExperimentalCoroutinesApi
class GetSelfFeedInteractorTest {

    @get:Rule
    var thrown: ExpectedException = ExpectedException.none()

    private val testDispatcher = TestCoroutineDispatcher()
    private var userManager: UserManager = mock()
    private val mockWebServer = MockWebServer()
    private val instagramApi by lazy {
        TestHelper.generateRetrofit(mockWebServer).create(InstagramApi::class.java)
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
    fun selfFeed_loggedUser() = runBlocking {
        // Given A Self User Feed API Fetch
        val interactor = GetSelfFeedInteractor(instagramApi, userManager)
        // When Getting Feed With Logged User
        whenever(userManager.userId).thenReturn("123")
        mockWebServer.setResponse("feed_response.json")
        val result = interactor.execute()
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
        thrown.expect(HttpException::class.java)
        runBlocking { interactor.execute() }
    }

}