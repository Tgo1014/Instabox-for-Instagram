package tgo1014.instabox.common

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Provide coroutines context.
 */
data class CoroutinesDispatcherProvider(
    val main: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val io: CoroutineDispatcher,
)