package tgo1014.instabox.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import tgo1014.instabox.TestHelper
import tgo1014.instabox.TestHelper.disableLiveDataTesting
import tgo1014.instabox.TestHelper.enableLiveDataTesting
import tgo1014.instabox.common.managers.CookieManager
import tgo1014.instabox.common.managers.UserManager

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private var cookieManager: CookieManager = spy()
    private var viewStateObserver: Observer<LoginState> = mock()

    private val testDispatcher = TestCoroutineDispatcher()
    private val userManager: UserManager = spy()
    private var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        enableLiveDataTesting()
    }

    init {
        enableLiveDataTesting() // This is needed before setting the observer
        viewModel = LoginViewModel(userManager, cookieManager)
        viewModel.state.observeForever(viewStateObserver)
    }

    @After
    fun shutdown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
        disableLiveDataTesting()
    }

    @Test
    fun validCookie_isTrue() {
        // Given An User Cookie
        var cookie = ""
        // When Cookie is valid
        cookie = TestHelper.getFileAsString("valid_token.txt")
        whenever(cookieManager.getCookie(any())).thenReturn(cookie)
        // Then Should Return True
        whenever(userManager.isUserLogged).thenReturn(false)
        viewModel.verifyIfUserIsLogged()
        verify(viewStateObserver).onChanged(LoginState.UserHasToLogin)
        viewModel.handleCookie(cookie)
        verify(viewStateObserver).onChanged(LoginState.UserLoggedSuccessfully)
    }

    @Test
    fun verifyingLogin_userNotLogged() {
        // Given Verifying If The User Is Logged In
        // When User Is Not Logged In
        whenever(userManager.isUserLogged).thenReturn(false)
        viewModel.verifyIfUserIsLogged()
        // Then State Should Be UserHasToLogin
        verify(viewStateObserver).onChanged(LoginState.UserHasToLogin)
    }

    @Test
    fun verifyingLogin_userLogged() {
        // Given Verifying If The User Is Logged In
        // When User Is Logged In
        whenever(userManager.isUserLogged).thenReturn(true)
        viewModel.verifyIfUserIsLogged()
        // Then State Should Be UserLoggedSuccessfully
        verify(viewStateObserver).onChanged(LoginState.UserLoggedSuccessfully)
    }

}