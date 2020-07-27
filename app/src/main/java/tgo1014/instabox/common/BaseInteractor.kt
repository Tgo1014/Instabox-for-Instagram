package tgo1014.instabox.common

interface BaseInteractor<R> {
    suspend fun execute(): R
}