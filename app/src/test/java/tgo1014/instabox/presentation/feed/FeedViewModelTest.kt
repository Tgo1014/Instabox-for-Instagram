package tgo1014.instabox.presentation.feed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
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
import timber.log.Timber

@ExperimentalCoroutinesApi
class FeedViewModelTest {

    // Set the main coroutines dispatcher for unit testing
    @get:Rule
    var coroutinesRule = MainCoroutineRule()

    private var userManager: UserManager = mock()
    private var getArchivedPhotosInteractor: GetArchivedPhotosInteractor = mock()
    private var actionOnFeedItemInteractor: ActionOnFeedItemInteractor = mock()
    private var getSelfFeedInteractor: GetSelfFeedInteractor = mock()

    private val viewModel: FeedViewModel = FeedViewModel(
        userManager,
        getArchivedPhotosInteractor,
        actionOnFeedItemInteractor,
        getSelfFeedInteractor,
    )

    private lateinit var mockito: AutoCloseable

    @Before
    fun setup() {
        mockito = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        mockito.close()
    }

    @Test
    fun init_userNotLogged() = runBlockingTest {
        viewModel.state.test {
            // Given The User Is Not Logged
            whenever(userManager.isUserLogged).thenReturn(false)
            // When Initting the VM
            viewModel.init()
            // Then State Should be Init
            assert(expectItem() is FeedState.Init)
            // Then State Should be UserHasToLogin
            assert(expectItem() is FeedState.UserHasToLogin)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_userLogged() = runBlockingTest {
        viewModel.state.test {
            // Given The User Not Logged
            whenever(userManager.isUserLogged).thenReturn(true)
            // When Initting the VM
            viewModel.init()
            // Then State Should be Init
            assert(expectItem() is FeedState.Init)
            // Then State Should be UserHasToLogin
            assert(expectItem() is FeedState.UserLoggedSuccesfully)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun userLogged_notArchive_feedSuccessful() = runBlockingTest {
        viewModel.state.test {
            // Given The User Is Logged
            whenever(userManager.isUserLogged).thenReturn(true)
            val fakeFeedWrappper = getFakeFeedWrapper(false)
            whenever(getSelfFeedInteractor.invoke()).thenReturn(fakeFeedWrappper)
            // When Initting the VM as not archive
            viewModel.init(false)
            // Then State Should be Init
            assert(expectItem() is FeedState.Init)
            // Then State Should be UserLoggedSuccesfully
            assert(expectItem() is FeedState.UserLoggedSuccesfully)
            // Then State Should be Loading
            assert(expectItem() is FeedState.Loading)
            // Then State Should be Success
            val item = expectItem()
            assert(item is FeedState.FeedSuccess)
            assert((item as FeedState.FeedSuccess).feedItems == fakeFeedWrappper.feedItems)
            // Then should not receive any more events
            expectNoEvents()
        }
    }
}