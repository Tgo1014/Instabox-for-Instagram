@file:Suppress("unused")

package tgo1014.instabox

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import tgo1014.instabox.di.CoroutinesDispatcherProvider
import tgo1014.instabox.network.models.FeedResponse
import tgo1014.instabox.presentation.feed.models.FeedItem
import tgo1014.instabox.presentation.feed.models.FeedMediaType
import tgo1014.instabox.presentation.feed.models.FeedWrapper
import java.io.File
import kotlin.coroutines.CoroutineContext

object TestHelper {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val fakeFeedItem = FeedItem("", "", "", FeedMediaType.UNKNOWN, true)
    val fakeFeedResponse = moshi.adapter(FeedResponse::class.java)
        .fromJson(getFileAsString("feed_response.json"))

    fun Any.getFakeFeedWrapper(isArchive: Boolean) =
        FeedWrapper(arrayListOf(fakeFeedItem), null, isArchive, false)

    fun generateRetrofit(mockWebServer: MockWebServer): Retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    fun MockWebServer.setResponse(fileName: String, responseCode: Int = 200) {
        enqueue(
            MockResponse()
                .setResponseCode(responseCode)
                .setBody(getFileAsString(fileName))
        )
    }

    /**
     *  In order to test LiveData, the `InstantTaskExecutorRule` rule needs to be applied via JUnit.
     *  As we are running it with Kotest, the "rule" will be implemented in this way instead
     *
     *  Based on https://github.com/spekframework/spek/issues/337#issuecomment-396000505
     */
    fun Any.enableLiveDataTesting() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }
        })
    }

    fun Any.disableLiveDataTesting() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    fun getFileAsString(filePath: String): String {
        val uri = ClassLoader.getSystemResource(filePath)
        val file = File(uri.path)
        return String(file.readBytes())
    }

    @ExperimentalCoroutinesApi
    fun Any.provideFakeCoroutinesDispatcherProvider(
        dispatcher: TestCoroutineDispatcher?,
    ): CoroutinesDispatcherProvider {
        val sharedTestCoroutineDispatcher = TestCoroutineDispatcher()
        return CoroutinesDispatcherProvider(
            dispatcher ?: sharedTestCoroutineDispatcher,
            dispatcher ?: sharedTestCoroutineDispatcher,
            dispatcher ?: sharedTestCoroutineDispatcher
        )
    }

    interface ManagedCoroutineScope : CoroutineScope {
        fun launch(block: suspend CoroutineScope.() -> Unit): Job
    }

    class LifecycleManagedCoroutineScope(
        private val lifecycleCoroutineScope: LifecycleCoroutineScope,
        override val coroutineContext: CoroutineContext,
    ) : ManagedCoroutineScope {
        override fun launch(block: suspend CoroutineScope.() -> Unit): Job =
            lifecycleCoroutineScope.launchWhenStarted(block)
    }

    @ExperimentalCoroutinesApi
    class TestScope(override val coroutineContext: CoroutineContext) : ManagedCoroutineScope {
        private val scope = TestCoroutineScope(coroutineContext)
        override fun launch(block: suspend CoroutineScope.() -> Unit): Job {
            return scope.launch {
                block.invoke(this)
            }
        }
    }
}