package tgo1014.instabox.pickpicture.models

sealed class Errors : Exception() {
    object UnableToGetImageError : Errors()
    object UnableToGetFeed: Errors()
    object UnableToFinishAction: Errors()
}