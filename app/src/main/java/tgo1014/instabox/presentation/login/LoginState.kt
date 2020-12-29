package tgo1014.instabox.presentation.login

sealed class LoginState {
    object Init : LoginState()
    object UserHasToLogin : LoginState()
    object UserLoggedSuccessfully : LoginState()
    class Error(e: Exception) : LoginState()
}
