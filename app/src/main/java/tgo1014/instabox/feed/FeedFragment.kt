package tgo1014.instabox.feed

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peekandpop.shalskar.peekandpop.PeekAndPop
import kotlinx.android.synthetic.main.dialog_action.view.*
import kotlinx.android.synthetic.main.feed_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import tgo1014.instabox.R
import tgo1014.instabox.common.utils.GridSpacingItemDecoration
import tgo1014.instabox.common.utils.openActivity
import tgo1014.instabox.common.utils.showX
import tgo1014.instabox.common.utils.toast
import tgo1014.instabox.feed.models.FeedItem
import tgo1014.instabox.feed.models.FeedState
import tgo1014.instabox.login.LoginActivity


class FeedFragment : Fragment(R.layout.feed_fragment) {

    private val isArchive
        get() = arguments?.getBoolean(PARAM_SHOW_ARCHIVED) ?: false

    private var actionDialog: AlertDialog? = null
    private var actionDialogView: View? = null
    private val viewModel: FeedViewModel by viewModel()
    private val layoutManager by lazy { GridLayoutManager(requireContext(), 3) }
    private val adapter by lazy {
        FeedAdapter(
            peekAndPop,
            { viewModel.loadMore() },
            { hasSelectedItems -> showFab(hasSelectedItems) }
        )
    }
    private val peekAndPop by lazy {
        PeekAndPop.Builder(requireActivity())
            .peekLayout(R.layout.peek_view)
            .parentViewGroupToDisallowTouchEvents(feedRecycler)
            .build()
    }

    private fun showFab(show: Boolean) {
        if (show) feedFab.showX() else feedFab.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setupRecycler()
        handleViewModel()
    }

    private fun handleViewModel() {
        lifecycle.addObserver(viewModel)
        viewModel.state.observe(viewLifecycleOwner, Observer { handleState(it) })
        viewModel.init(isArchive)
    }

    private fun setListeners() {
        feedFab.text = getString(if (isArchive) R.string.unarchive else R.string.archive)
        feedFab.setOnClickListener {
            viewModel.feedItemAction(*adapter.selectedIdsList.toTypedArray())
        }
        feedBtnLogin.setOnClickListener { openActivity<LoginActivity>() }
        feedSwipe.setOnRefreshListener { refresh() }
        feedFabRefresh.setOnClickListener { refresh() }
    }

    private fun refresh() {
        feedSwipe.isRefreshing = false
        adapter.clear()
        viewModel.resetAndReload()
    }

    private fun setupRecycler() {
        feedRecycler.layoutManager = layoutManager
        feedRecycler.addItemDecoration(
            GridSpacingItemDecoration(
                3,
                resources.getDimension(R.dimen.feedItemPaddingBottom).toInt(),
                false
            )
        )
        feedRecycler.adapter = adapter
        feedRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                feedSwipe.isEnabled =
                    layoutManager.findFirstCompletelyVisibleItemPosition() == 0 // 0 is for first item position
            }
        })
    }

    private fun removeItemFromList(vararg feedItem: FeedItem) {
        adapter.removeItem(*feedItem, commitCallback = Runnable { handleListSizeState() })
    }

    private fun handleState(state: FeedState) {
        when (state) {
            FeedState.UserHasToLogin -> feedGroupLoggedOff.isVisible = true
            FeedState.UserLoggedSuccesfully -> feedGroupLoggedOff.isVisible = false
            is FeedState.FeedActionRunning -> {
                showDialog(state.actionAlreadyDoneSize, state.itemsToPerfomeActionSize)
                adapter.selectedIdsList.remove(state.lastRemovedItem)
                showFab(false)
            }
            is FeedState.FeedItemActionSuccess -> {
                removeItemFromList(*state.feedItem)
                clearDialog()
            }
            is FeedState.FeedSuccess -> {
                adapter.addItems(state.feedItems, Runnable {
                    handleListSizeState()
                })
                feedFabRefresh.isEnabled = true
            }
            is FeedState.Loading -> {
                adapter.addLoading()
                feedFabRefresh.isEnabled = false
            }
            is FeedState.Error -> {
                toast("Error")
                feedFabRefresh.isEnabled = true
            }
        }
    }

    private fun handleListSizeState() {
        feedGroupEmpty.isVisible = adapter.currentList.isEmpty()
    }

    private fun clearDialog() {
        actionDialog?.dismiss()
        actionDialog = null
        actionDialogView = null
    }

    private fun showDialog(actionAlreadyDoneSize: Int, itemsTOPerformActionSize: Int) {

        fun updateDialogValue() {
            actionDialogView?.dialogActionTv?.text = String.format(
                getString(R.string.action_dialog_message),
                if (!isArchive) getString(R.string.archiving) else getString(R.string.unarchiving),
                actionAlreadyDoneSize,
                itemsTOPerformActionSize
            )
        }

        if (actionDialog != null) {
            updateDialogValue()
            return
        }

        actionDialogView = layoutInflater.inflate(R.layout.dialog_action, null)
        actionDialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setView(actionDialogView)
            .create()

        updateDialogValue()

        actionDialog?.show()

    }

    companion object {
        private const val PARAM_SHOW_ARCHIVED = "PARAM_SHOW_ARCHIVED"
        fun newInstance(isArchive: Boolean = false): FeedFragment {
            val b = Bundle().apply { putBoolean(PARAM_SHOW_ARCHIVED, isArchive) }
            return FeedFragment().apply { arguments = b }
        }
    }

}