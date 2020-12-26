package tgo1014.instabox.presentation.info

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import tgo1014.instabox.BuildConfig
import tgo1014.instabox.R
import tgo1014.instabox.utils.viewBinding
import tgo1014.instabox.databinding.FragmentInfoBinding

class InfoFragment : Fragment(R.layout.fragment_info) {

    private val binding by viewBinding(FragmentInfoBinding::bind)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragInfoTvVersion.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        binding.fragInfoBtnPrivacy.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.privacy_url))
            startActivity(i)
        }
    }

    companion object {
        fun newInstance() = InfoFragment()
    }
}