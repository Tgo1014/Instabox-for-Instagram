package tgo1014.instabox.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peekandpop.shalskar.peekandpop.PeekAndPop
import com.peekandpop.shalskar.peekandpop.PeekAndPop.OnHoldAndReleaseListener
import kotlinx.android.synthetic.main.item_feed.view.*
import kotlinx.android.synthetic.main.peek_view.view.*
import okhttp3.internal.toLongOrDefault
import tgo1014.instabox.R
import tgo1014.instabox.common.utils.load
import tgo1014.instabox.common.utils.removeAndVerifyIfEmpty
import tgo1014.instabox.feed.models.FeedItem
import tgo1014.instabox.feed.models.FeedMediaType
import timber.log.Timber
import kotlin.random.Random

typealias OnLastItemReached = () -> Unit
typealias OnSelected = (hasSelectedItems: Boolean) -> Unit

class FeedAdapter(
    private val peekAndPop: PeekAndPop,
    private val onLastItemReached: OnLastItemReached,
    private val hasSelectedItems: OnSelected
) : ListAdapter<FeedItem, FeedAdapter.FeedViewHolder>(diffUtil) {

    private val random = Random
    val selectedIdsList = mutableListOf<FeedItem>()

    override fun getItemId(position: Int): Long {
        return currentList[position].id.toLongOrDefault(random.nextLong())
    }

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(feedItem: FeedItem) {
            with(itemView) {
                itemFeedIvImage?.load(feedItem.thumbUrl)
                itemFeedIvArchive?.isVisible = feedItem.isArchived
                when (feedItem.mediaType) {
                    FeedMediaType.UNKNOWN -> itemFeedIvType?.isVisible = false
                    FeedMediaType.PHOTO -> itemFeedIvType?.isVisible = false
                    FeedMediaType.VIDEO -> {
                        itemFeedIvType?.setImageResource(R.drawable.ic_play)
                        itemFeedIvType?.isVisible = true
                    }
                    FeedMediaType.ALBUM -> {
                        itemFeedIvType?.setImageResource(R.drawable.ic_album)
                        itemFeedIvType?.isVisible = true
                    }
                }
                // On recycling verify status
                if (selectedIdsList.any { it == feedItem }) {
                    itemView.setPadding(20)
                    itemFeedIvSelection?.isVisible = true
                } else {
                    itemFeedIvSelection?.isVisible = false
                    itemView.setPadding(0)
                }
                setOnClickListener { handleSelection(feedItem) }
            }
        }

        private fun handleSelection(feedItem: FeedItem) {
            if (selectedIdsList.any { it == feedItem }) {
                itemView.setPadding(0)
                itemView.itemFeedIvSelection?.isVisible = false
                hasSelectedItems.invoke(!selectedIdsList.removeAndVerifyIfEmpty(feedItem))
            } else {
                itemView.setPadding(20)
                selectedIdsList.add(feedItem)
                itemView.itemFeedIvSelection?.isVisible = true
                hasSelectedItems.invoke(true)
            }
        }

    }

    enum class ViewType { MEDIA, LOADING }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).id == "") ViewType.LOADING.ordinal else ViewType.MEDIA.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapter.FeedViewHolder {
        val viewLayout = when (ViewType.values()[viewType]) {
            ViewType.MEDIA -> R.layout.item_feed
            ViewType.LOADING -> R.layout.item_feed_loading
        }
        val view = LayoutInflater.from(parent.context).inflate(viewLayout, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        peekAndPop.addLongClickView(holder.itemView, holder.adapterPosition)
        setupPeekAndPopHoldAndRelease()
        holder.bind(getItem(holder.adapterPosition))
        if (position >= itemCount - 1) {
            onLastItemReached()
        }
    }

    fun addLoading() {
        addItems(listOf(FeedItem("", "", "", FeedMediaType.UNKNOWN, false)))
    }

    fun addItems(feedItemList: List<FeedItem>, commitCallback: Runnable? = null) {
        // This filter remove the loading
        val newList = currentList.filterNot { it.id == "" }.toMutableList()
        newList.addAll(feedItemList)
        submitList(newList, commitCallback)
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
                    peekView.peekIvImage.load(item.imageUrl, item.thumbUrl)
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
