package tgo1014.instabox.presentation.login

import androidx.lifecycle.Observer
import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import tgo1014.instabox.MainCoroutineRule
import tgo1014.instabox.TestHelper
import tgo1014.instabox.managers.CookieManager
import tgo1014.instabox.managers.UserManager

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    // Set the main coroutines dispatcher for unit testing
    @get:Rule
    var coroutinesRule = MainCoroutineRule()

    private var cookieManager: CookieManager = spy()

    private val testDispatcher = TestCoroutineDispatcher()
    private val userManager: UserManager = spy()
    private var viewModel: LoginViewModel

    init {
        viewModel = LoginViewModel(userManager, cookieManager)
    }

    @Test
    fun validCookie_isTrue() = runBlockingTest {
        viewModel.state.test {
            // Given An User Cookie
            var cookie = ""
            // When Cookie is valid
            cookie = TestHelper.getFileAsString("valid_token.txt")
            whenever(cookieManager.getCookie(any())).thenReturn(cookie)
            // Then Should Return True
            assert(expectItem() is LoginState.Init)
            whenever(userManager.isUserLogged).thenReturn(false)
            viewModel.verifyIfUserIsLogged()
            assert(expectItem() is LoginState.UserHasToLogin)
            viewModel.handleCookie(cookie)
            assert(expectItem() is LoginState.UserLoggedSuccessfully)
            expectNoEvents()
        }
    }

    @Test
    fun verifyingLogin_userNotLogged() = runBlockingTest {
        viewModel.state.test {
            // Given Verifying If The User Is Logged In
            // When User Is Not Logged In
            whenever(userManager.isUserLogged).thenReturn(false)
            viewModel.verifyIfUserIsLogged()
            // Then State Should Be UserHasToLogin
            assert(expectItem() is LoginState.Init)
            assert(expectItem() is LoginState.UserHasToLogin)
            expectNoEvents()
        }
    }

    @Test
    fun verifyingLogin_userLogged() = runBlockingTest {
        viewModel.state.test {
            // Given Verifying If The User Is Logged In
            // When User Is Logged In
            whenever(userManager.isUserLogged).thenReturn(true)
            viewModel.verifyIfUserIsLogged()
            // Then State Should Be UserLoggedSuccessfully
            assert(expectItem() is LoginState.Init)
            assert(expectItem() is LoginState.UserLoggedSuccessfully)
            expectNoEvents()
        }
    }
}