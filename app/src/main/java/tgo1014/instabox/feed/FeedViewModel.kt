package tgo1014.instabox.feed

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import tgo1014.instabox.common.CoroutinesDispatcherProvider
import tgo1014.instabox.common.managers.UserManager
import tgo1014.instabox.common.utils.launchOnMain
import tgo1014.instabox.common.utils.tryOnIO
import tgo1014.instabox.feed.interactors.ActionOnFeedItemInteractor
import tgo1014.instabox.feed.interactors.GetArchivedPhotosInteractor
import tgo1014.instabox.feed.interactors.GetSelfFeedInteractor
import tgo1014.instabox.feed.models.FeedItem
import tgo1014.instabox.feed.models.FeedState
import tgo1014.instabox.pickpicture.models.Errors
import timber.log.Timber

class FeedViewModel(
    private val userManager: UserManager,
    private val getArchivedPhotosInteractor: GetArchivedPhotosInteractor,
    private val actionOnFeedItemInteractor: ActionOnFeedItemInteractor,
    private val getSelfFeedInteractor: GetSelfFeedInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel(), LifecycleObserver {

    private val _state = MutableLiveData<FeedState>()
    val state: LiveData<FeedState> get() = _state

    private var moreResultAvailable = true
    private var nextMaxId: String? = null
    private var isArchive = false
    private var alreadyInit = false
    private var feedAlreadyLoadedOnce = false

    fun init(isArchive: Boolean = false) {
        this.isArchive = isArchive
        alreadyInit = true
        verifyUserState()
    }

    private fun isLoading() = _state.value == FeedState.Loading

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun verifyUserState() {
        if (!alreadyInit) {
            return
        }
        if (!userManager.isUserLogged) {
            stateHasToLogin()
            return
        }
        // This check avoid wasting the user data
        if (feedAlreadyLoadedOnce) {
            return
        }
        stateLoggedSuccessfully()
        getCorrectFeed()
        feedAlreadyLoadedOnce = true
    }

    private fun getCorrectFeed() {
        if (isArchive) getUserArchivedFeed() else getSelfFeed()
    }

    private fun getSelfFeed() = viewModelScope.launch(dispatcherProvider.io) {
        try {
            launch(dispatcherProvider.main) { _state.value = FeedState.Loading }
            getSelfFeedInteractor.input = GetSelfFeedInteractor.Input(nextMaxId)
            val feedWrapper = getSelfFeedInteractor.execute()
            nextMaxId = feedWrapper.nextPageMaxId
            moreResultAvailable = feedWrapper.moreResultAvailable
            launch(dispatcherProvider.main) {
                _state.value = FeedState.FeedSuccess(feedWrapper.feedItems)
            }
        } catch (e: Exception) {
            Timber.d(e)
            launch(dispatcherProvider.main) { _state.postValue(FeedState.Error(Errors.UnableToGetFeed)) }
        }
    }

    private fun getUserArchivedFeed() = tryOnIO({
        launchOnMain { _state.value = FeedState.Loading }
        getArchivedPhotosInteractor.input = GetArchivedPhotosInteractor.Input(nextMaxId)
        val feedWrapper = getArchivedPhotosInteractor.execute()
        nextMaxId = feedWrapper.nextPageMaxId
        moreResultAvailable = feedWrapper.moreResultAvailable
        _state.postValue(FeedState.FeedSuccess(feedWrapper.feedItems))
    }, {
        Timber.d(it)
        launchOnMain { _state.value = FeedState.Error(Errors.UnableToGetFeed) }
    })

    private fun stateLoggedSuccessfully() {
        _state.value = FeedState.UserLoggedSuccesfully
    }

    private fun stateHasToLogin() {
        _state.value = FeedState.UserHasToLogin
    }

    fun resetAndReload() {
        nextMaxId = null
        moreResultAvailable = true
        feedAlreadyLoadedOnce = false
        verifyUserState()
    }

    fun loadMore() {
        if (!isLoading() && moreResultAvailable) {
            getCorrectFeed()
        }
    }

    fun feedItemAction(vararg feedItems: FeedItem) {
        var count = 1
        var error = false
        _state.value = FeedState.FeedActionRunning(count, feedItems.size)
        feedItems.forEach loop@{ feedItem ->
            performFeedItemAction(
                feedItem,
                onSuccess = {
                    if (error) return@performFeedItemAction
                    if (count == feedItems.size) {
                        launchOnMain { _state.value = FeedState.FeedItemActionSuccess(*feedItems) }
                    } else {
                        count += 1
                        launchOnMain {
                            _state.value =
                                FeedState.FeedActionRunning(count, feedItems.size, feedItem)
                        }
                    }
                }, onError = {
                    if (error) return@performFeedItemAction
                    error = true
                    launchOnMain { _state.value = FeedState.Error(Errors.UnableToGetFeed) }
                }
            )
        }
    }

    private fun performFeedItemAction(
        feedItem: FeedItem,
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) = tryOnIO({
        actionOnFeedItemInteractor.input = ActionOnFeedItemInteractor.Input(feedItem)
        actionOnFeedItemInteractor.execute()
        onSuccess()
    }, {
        Timber.d(it)
        onError()
    })
}
