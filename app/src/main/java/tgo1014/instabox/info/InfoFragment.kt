package tgo1014.instabox.info

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_info.*
import tgo1014.instabox.BuildConfig
import tgo1014.instabox.R

class InfoFragment : Fragment(R.layout.fragment_info) {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragInfoTvVersion.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        fragInfoBtnPrivacy.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.privacy_url))
            startActivity(i)
        }
    }

    companion object {
        fun newInstance() = InfoFragment()
    }

}