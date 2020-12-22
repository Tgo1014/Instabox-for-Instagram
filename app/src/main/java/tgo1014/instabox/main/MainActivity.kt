package tgo1014.instabox.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import dagger.hilt.android.AndroidEntryPoint
import tgo1014.instabox.R
import tgo1014.instabox.common.utils.viewBinding
import tgo1014.instabox.databinding.ActivityMainBinding
import tgo1014.instabox.feed.FeedFragment
import tgo1014.instabox.info.InfoFragment
import tgo1014.instabox.pickpicture.PickPictureFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel: MainViewModel by viewModels()
    var onHashtagFragmentResumedListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
        replaceFeedFrag()
    }

    private fun setupListeners() {
        binding.mainBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuBottomNavArchive -> replaceArchiveFrag()
                R.id.menuBottomNavFeed -> replaceFeedFrag()
                R.id.menuBottomNavHashtag -> {
                    replaceHashtagFrag()
                    onHashtagFragmentResumedListener?.invoke()
                }
                R.id.menuBottomNavInfo -> replaceInfoFrag()
            }
            true
        }
    }

    private fun replaceArchiveFrag() = replaceFrag(FeedFragment.newInstance(true), "archive")
    private fun replaceFeedFrag() = replaceFrag(FeedFragment.newInstance(), "feed")
    private fun replaceHashtagFrag() = replaceFrag(PickPictureFragment.newInstance(), "hashtag")
    private fun replaceInfoFrag() = replaceFrag(InfoFragment.newInstance(), "info")

    private fun replaceFrag(f: Fragment, tag: String? = null) {
        supportFragmentManager.switch(R.id.mainFragContainer, f, tag ?: f.javaClass.name)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            return
        }
        finish()
    }

    private fun FragmentManager.switch(containerId: Int, newFrag: Fragment, tag: String) {
        var current = findFragmentByTag(tag)
        beginTransaction()
            .apply {
                // Hide the current fragment
                primaryNavigationFragment?.let { hide(it) }
                // Check if current fragment exists in fragmentManager
                if (current == null) {
                    current = newFrag
                    add(containerId, newFrag, tag)
                    return@apply
                }
                show(current!!)
            }
            .apply {
                // Only apply the transition if the user is logged in, otherwise it looks weird
                if (viewModel.userManager.isUserLogged) {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                }
            }
            .setPrimaryNavigationFragment(current)
            .setReorderingAllowed(true)
            .commitNow()
    }
}
