package tgo1014.instabox.presentation.feed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import tgo1014.instabox.MainCoroutineRule
import tgo1014.instabox.TestHelper.getFakeFeedWrapper
import tgo1014.instabox.managers.UserManager
import tgo1014.instabox.presentation.feed.interactors.ActionOnFeedItemInteractor
import tgo1014.instabox.presentation.feed.interactors.GetArchivedPhotosInteractor
import tgo1014.instabox.presentation.feed.interactors.GetSelfFeedInteractor
import tgo1014.instabox.presentation.feed.models.FeedState

@ExperimentalCoroutinesApi
class FeedViewModelTest {

    // Set the main coroutines dispatcher for unit testing
    @get:Rule
    var coroutinesRule = MainCoroutineRule()

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var userManager: UserManager = mock()
    private var viewStateObserver: Observer<FeedState>? = null

    private var getArchivedPhotosInteractor: GetArchivedPhotosInteractor = mock()
    private var actionOnFeedItemInteractor: ActionOnFeedItemInteractor = mock()
    private var getSelfFeedInteractor: GetSelfFeedInteractor = mock()

    private val viewModel: FeedViewModel = FeedViewModel(
        userManager,
        getArchivedPhotosInteractor,
        actionOnFeedItemInteractor,
        getSelfFeedInteractor,
    )

    private val states = mutableListOf<FeedState>()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewStateObserver = Observer<FeedState> { states.add(it) }
        viewModel.state.observeForever(viewStateObserver!!)
    }

    @After
    fun tearDown() {
        viewModel.state.removeObserver(viewStateObserver!!)
        states.clear()
    }

    @Test
    fun init_userNotLogged() {
        // Given The User Is Not Logged
        whenever(userManager.isUserLogged).thenReturn(false)
        // When Initting the VM
        viewModel.init()
        // Then State Should be UserHasToLogin
        assert(states[0] is FeedState.UserHasToLogin)
    }

    @Test
    fun init_userLogged() {
        // Given The User Not Logged
        whenever(userManager.isUserLogged).thenReturn(true)
        // When Initting the VM
        viewModel.init()
        // Then State Should be UserHasToLogin
        assert(states[0] is FeedState.UserLoggedSuccesfully)
    }

    @Test
    fun userLogged_notArchive_feedSuccessful() = runBlocking {
        // Given The User Is Logged
        whenever(userManager.isUserLogged).thenReturn(true)
        val fakeFeedWrappper = getFakeFeedWrapper(false)
        whenever(getSelfFeedInteractor.invoke()).thenReturn(fakeFeedWrappper)
        // When Initting the VM as not archive
        viewModel.init(false)
        // Then State Should be UserLoggedSuccesfully
        assert(states[0] is FeedState.UserLoggedSuccesfully)
        // Then State Should be Loading
        assert(states[1] is FeedState.Loading)
        // Then State Should be Success
        assert(states[2] is FeedState.FeedSuccess)
        assert((states[2] as FeedState.FeedSuccess).feedItems == fakeFeedWrappper.feedItems)
    }
}