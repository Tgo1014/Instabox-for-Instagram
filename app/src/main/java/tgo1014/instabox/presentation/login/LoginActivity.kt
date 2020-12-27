package tgo1014.instabox.presentation.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import tgo1014.instabox.R
import tgo1014.instabox.utils.toast
import tgo1014.instabox.utils.viewBinding
import tgo1014.instabox.databinding.ActivityLoginBinding

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private val binding by viewBinding(ActivityLoginBinding::inflate)
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.state.observe(this, ::handleState)
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
        setSupportActionBar(binding.loginToolbar)
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
        binding.loginWebview.apply {
            settings.javaScriptEnabled = true
            getSettings().setBuiltInZoomControls(false);
            getSettings().setSupportZoom(false);
            getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            getSettings().setAllowFileAccess(true);
            getSettings().setDomStorageEnabled(true);
            webViewClient = viewModel.webviewClient
            loadUrl(viewModel.instaLoginUrl)
        }
    }
}