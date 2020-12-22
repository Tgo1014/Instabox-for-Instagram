package tgo1014.instabox.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peekandpop.shalskar.peekandpop.PeekAndPop
import com.peekandpop.shalskar.peekandpop.PeekAndPop.OnHoldAndReleaseListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.internal.toLongOrDefault
import tgo1014.instabox.R
import tgo1014.instabox.common.utils.load
import tgo1014.instabox.common.utils.removeAndVerifyIfEmpty
import tgo1014.instabox.databinding.ItemFeedBinding
import tgo1014.instabox.databinding.ItemFeedLoadingBinding
import tgo1014.instabox.feed.models.FeedItem
import tgo1014.instabox.feed.models.FeedMediaType
import timber.log.Timber
import kotlin.random.Random

typealias OnLastItemReached = () -> Unit
typealias OnSelected = (hasSelectedItems: Boolean) -> Unit

class FeedAdapter(
    private val peekAndPop: PeekAndPop,
    private val onLastItemReached: OnLastItemReached,
    private val hasSelectedItems: OnSelected,
) : ListAdapter<FeedItem, RecyclerView.ViewHolder>(diffUtil) {

    val selectedIdsList = mutableListOf<FeedItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ViewType.values()[viewType]) {
            ViewType.MEDIA -> FeedViewHolder(
                ItemFeedBinding.inflate(LayoutInflater.from(parent.context)).root
            )
            ViewType.LOADING -> LoadingViewHolder(
                ItemFeedLoadingBinding.inflate(LayoutInflater.from(parent.context)).root
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        peekAndPop.addLongClickView(holder.itemView, holder.adapterPosition)
        setupPeekAndPopHoldAndRelease()
        when (holder) {
            is FeedViewHolder -> holder.bind(getItem(holder.adapterPosition))
        }
        if (position >= itemCount - 1) {
            onLastItemReached()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).id == "") ViewType.LOADING.ordinal else ViewType.MEDIA.ordinal
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.substringBefore("_").toLongOrDefault(0)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemFeedBinding.bind(itemView)

        fun bind(feedItem: FeedItem) {
            with(binding) {
                itemFeedIvImage.load(feedItem.thumbUrl)
                itemFeedIvArchive.isVisible = feedItem.isArchived
                when (feedItem.mediaType) {
                    FeedMediaType.UNKNOWN -> itemFeedIvType.isVisible = false
                    FeedMediaType.PHOTO -> itemFeedIvType.isVisible = false
                    FeedMediaType.VIDEO -> {
                        itemFeedIvType.setImageResource(R.drawable.ic_play)
                        itemFeedIvType.isVisible = true
                    }
                    FeedMediaType.ALBUM -> {
                        itemFeedIvType.setImageResource(R.drawable.ic_album)
                        itemFeedIvType.isVisible = true
                    }
                }
                // On recycling verify status
                if (selectedIdsList.any { it == feedItem }) {
                    itemView.setPadding(20)
                    itemFeedIvSelection.isVisible = true
                } else {
                    itemFeedIvSelection.isVisible = false
                    itemView.setPadding(0)
                }
                root.setOnClickListener { handleSelection(feedItem) }
            }
        }

        private fun handleSelection(feedItem: FeedItem) {
            if (selectedIdsList.any { it == feedItem }) {
                itemView.setPadding(0)
                binding.itemFeedIvSelection.isVisible = false
                hasSelectedItems.invoke(!selectedIdsList.removeAndVerifyIfEmpty(feedItem))
            } else {
                itemView.setPadding(20)
                selectedIdsList.add(feedItem)
                binding.itemFeedIvSelection.isVisible = true
                hasSelectedItems.invoke(true)
            }
        }
    }

    enum class ViewType { MEDIA, LOADING }

    fun addLoading() {
        addItems(listOf(FeedItem("", "", "", FeedMediaType.UNKNOWN, false)))
    }

    fun addItems(feedItemList: List<FeedItem>, commitCallback: Runnable? = null) {
        // This filter remove the loading
        val newList = currentList.filterNot { it.id == "" }.toMutableList()
        newList.addAll(feedItemList)
        submitList(newList) {
            commitCallback?.run()
        }
    }

    fun removeItem(vararg feedItem: FeedItem, commitCallback: Runnable? = null) {
        val newList = currentList.toMutableList()
        feedItem.forEach {
            selectedIdsList.remove(it)
            val index = newList.indexOf(it)
            if (index != -1) newList.removeAt(index)
        }
        submitList(newList, commitCallback)
    }

    fun clear() {
        submitList(null)
        selectedIdsList.clear()
        hasSelectedItems.invoke(false)
    }

    private fun setupPeekAndPopHoldAndRelease() {
        with(peekAndPop) {
            addHoldAndReleaseView(R.id.peekIvAction)
            setOnGeneralActionListener(object : PeekAndPop.OnGeneralActionListener {
                override fun onPop(longClickView: View?, position: Int) {
                    Timber.i("PeekAndPop onPop")
                }

                override fun onPeek(longClickView: View?, position: Int) {
                    val item = getItem(position)
                    val peekIvImage = peekView.findViewById<ImageView>(R.id.peekIvImage)
                    peekIvImage.load(item.imageUrl, item.thumbUrl)
                    Timber.i("PeekAndPop onPeek")
                }
            })
            setOnHoldAndReleaseListener(object : OnHoldAndReleaseListener {
                override fun onHold(view: View, position: Int) {
                    Timber.i("PeekAndPop onHolder")
                }

                override fun onLeave(view: View, position: Int) {
                    Timber.i("PeekAndPop onLeave")
                }

                override fun onRelease(view: View, position: Int) {
                    Timber.i("PeekAndPop onRelease")
                }
            })
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<FeedItem>() {
            override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem) =
                oldItem.id == newItem.id
        }
    }
}
