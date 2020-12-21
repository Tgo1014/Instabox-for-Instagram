package tgo1014.instabox.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import tgo1014.instabox.R
import tgo1014.instabox.common.utils.toast

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this, Observer { handleState(it) })
        viewModel.verifyIfUserIsLogged()
        setupToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        setSupportActionBar(loginToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun handleState(state: LoginState) {
        when (state) {
            LoginState.UserHasToLogin -> setupWebview()
            LoginState.UserLoggedSuccessfully -> {
                toast(getString(R.string.success))
                onBackPressed()
            }
            is LoginState.Error -> toast(getString(R.string.error))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebview() {
        loginWebview.apply {
            settings.javaScriptEnabled = true
            webViewClient = viewModel.webviewClient
            loadUrl(viewModel.instaLoginUrl)
        }
    }
}