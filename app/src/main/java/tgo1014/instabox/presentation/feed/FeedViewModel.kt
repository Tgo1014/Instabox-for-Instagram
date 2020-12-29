package tgo1014.instabox.presentation.feed

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tgo1014.instabox.managers.UserManager
import tgo1014.instabox.presentation.feed.interactors.ActionOnFeedItemInteractor
import tgo1014.instabox.presentation.feed.interactors.GetArchivedPhotosInteractor
import tgo1014.instabox.presentation.feed.interactors.GetSelfFeedInteractor
import tgo1014.instabox.presentation.feed.models.FeedItem
import tgo1014.instabox.presentation.feed.models.FeedState
import tgo1014.instabox.presentation.pickpicture.models.Errors
import timber.log.Timber

class FeedViewModel @ViewModelInject constructor(
    private val userManager: UserManager,
    private val getArchivedPhotosInteractor: GetArchivedPhotosInteractor,
    private val actionOnFeedItemInteractor: ActionOnFeedItemInteractor,
    private val getSelfFeedInteractor: GetSelfFeedInteractor,
) : ViewModel(), LifecycleObserver {

    private val _state = MutableStateFlow<FeedState>(FeedState.Init)
    val state: StateFlow<FeedState> get() = _state

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

    private fun getSelfFeed() = viewModelScope.launch {
        try {
            _state.value = FeedState.Loading
            val feedWrapper = getSelfFeedInteractor(nextMaxId)
            nextMaxId = feedWrapper.nextPageMaxId
            moreResultAvailable = feedWrapper.moreResultAvailable
            _state.value = FeedState.FeedSuccess(feedWrapper.feedItems)
        } catch (e: Exception) {
            Timber.d(e)
            _state.value = FeedState.Error(Errors.UnableToGetFeedError)
        }
    }

    private fun getUserArchivedFeed() = viewModelScope.launch {
        try {
            _state.value = FeedState.Loading
            val feedWrapper = getArchivedPhotosInteractor(nextMaxId)
            nextMaxId = feedWrapper.nextPageMaxId
            moreResultAvailable = feedWrapper.moreResultAvailable
            _state.value = FeedState.FeedSuccess(feedWrapper.feedItems)
        } catch (e: Exception) {
            Timber.d(e)
            _state.value = FeedState.Error(Errors.UnableToGetFeedError)
        }
    }

    private fun stateLoggedSuccessfully() {
        _state.tryEmit(FeedState.UserLoggedSuccesfully)
    }

    private fun stateHasToLogin() {
        _state.tryEmit(FeedState.UserHasToLogin)
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
                feedItem = feedItem,
                onSuccess = {
                    if (error) return@performFeedItemAction
                    if (count == feedItems.size) {
                        _state.value = FeedState.FeedItemActionSuccess(*feedItems)
                    } else {
                        count += 1
                        _state.value = FeedState.FeedActionRunning(count, feedItems.size, feedItem)
                    }
                }, onError = {
                    if (error) return@performFeedItemAction
                    error = true
                    _state.value = FeedState.Error(Errors.UnableToGetFeedError)
                }
            )
        }
    }

    private fun performFeedItemAction(
        feedItem: FeedItem,
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) = viewModelScope.launch {
        try {
            actionOnFeedItemInteractor(feedItem)
            onSuccess()
        } catch (e: Exception) {
            Timber.d(e)
            onError()
        }
    }
}
