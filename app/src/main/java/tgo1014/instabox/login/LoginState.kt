package tgo1014.instabox.login

sealed class LoginState {
    object UserHasToLogin : LoginState()
    object UserLoggedSuccessfully : LoginState()
    class Error(e: Exception) : LoginState()
}
