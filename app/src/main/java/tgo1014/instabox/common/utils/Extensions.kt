package tgo1014.instabox.common.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tgo1014.instabox.pickpicture.OnHashtagClickedListener
import tgo1014.instabox.pickpicture.models.Prediction
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun Fragment.toast(message: String) {
    requireContext().toast(message)
}

fun Fragment.longToast(message: String) {
    requireContext().longToast(message)
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(context, start, block)

fun ViewModel.launchOnIO(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(Dispatchers.IO, start, block)

fun ViewModel.launchOnMain(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(Dispatchers.Main, start, block)

fun ViewModel.tryOnIO(
    block: suspend CoroutineScope.() -> Unit,
    exceptionHandler: (e: Exception) -> Unit,
) {
    launchOnIO {
        try {
            block()
        } catch (e: Exception) {
            Timber.e(e)
            exceptionHandler.invoke(e)
        }
    }
}

fun TextView.addHashtags(hashtags: List<Prediction>, onHashTagClicked: OnHashtagClickedListener) {
    val hashtagList = hashtags.map { "#${it.description}" }
    this.text = hashtagList.joinToString(" ")
    val spannableString = SpannableString(this.text)

    hashtagList.forEach { hashtag ->
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                onHashTagClicked.invoke(hashtag.substring(1)) // Remove '#'
            }
        }
        val startIndexOfLink = this.text.toString().indexOf(hashtag)
        spannableString.setSpan(
            clickableSpan,
            startIndexOfLink,
            startIndexOfLink + hashtag.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun String.toCamelCase(): String {
    return this.split(" ").mapIndexed { index, s ->
        return@mapIndexed if (index == 0) s.decapitalize() else s.capitalize()
    }.joinToString("")
}

fun List<Prediction>.formatToCamelCase() = this.map { Prediction(it.description.toCamelCase()) }

inline val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
inline val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun String.substringBetween(start: String, end: String) =
    substringAfter(start, "").substringBefore(end, "")

inline fun <reified A : AppCompatActivity> Context.openActivity(noinline extra: (Intent.() -> Unit)? = null) {
    val intent = Intent(this, A::class.java)
    extra?.let { intent.it() }
    startActivity(intent)
}

inline fun <reified A : AppCompatActivity> Fragment.openActivity(noinline extra: (Intent.() -> Unit)? = null) {
    requireActivity().openActivity<A>(extra)
}

fun ImageView.load(url: String, thumbUrl: String? = null) {
    Glide.with(context.applicationContext)
        .load(url)
        .apply {
            if (thumbUrl != null) {
                thumbnail(Glide.with(context.applicationContext).load(thumbUrl))
            }
        }
        .into(this)
}

fun <T> MutableList<T>.removeAndVerifyIfEmpty(item: T): Boolean {
    this.remove(item)
    return this.isEmpty()
}

/**
 * Unlike [FloatingActionButton.show] animates button even it not currently
 * laid out
 */
fun ExtendedFloatingActionButton.showX() {
    if (ViewCompat.isLaidOut(this)) {
        show()
    } else {
        animate().cancel() //cancel all animations
        scaleX = 0f
        scaleY = 0f
        alpha = 0f
        visibility = View.VISIBLE
        //values from support lib source code
        animate().setDuration(200).scaleX(1f).scaleY(1f).alpha(1f).interpolator =
            LinearOutSlowInInterpolator()
        extend()
    }
}

/**
 * https://stackoverflow.com/a/56446508/6022725
 */
fun RecyclerView.executeAfterAllAnimationsAreFinished(callback: (RecyclerView) -> Unit) = post(
    object : Runnable {
        override fun run() {
            if (isAnimating) {
                itemAnimator?.isRunning {
                    post(this)
                }
            } else {
                callback(this@executeAfterAllAnimationsAreFinished)
            }
        }
    }
)