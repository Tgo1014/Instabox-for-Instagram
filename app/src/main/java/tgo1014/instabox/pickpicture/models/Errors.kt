package tgo1014.instabox.pickpicture.models

sealed class Errors : Exception() {
    object UnableToGetImageError : Errors()
    object UnableToGetFeedError : Errors()
    object UnableToFinishActionError : Errors()
    object InvalidClarifaiKeyError : Errors()
}