package tgo1014.instabox.pickpicture

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tgo1014.instabox.R
import tgo1014.instabox.common.utils.addHashtags
import tgo1014.instabox.common.utils.toast
import tgo1014.instabox.common.utils.viewBinding
import tgo1014.instabox.databinding.BottomsheetResultBinding
import tgo1014.instabox.pickpicture.models.Prediction
import java.io.File

typealias OnHashtagClickedListener = (hashtag: String) -> Unit

class BottomSheetFragment(
    private val predictionList: List<Prediction>,
    private val image: File,
    private val onHashTagClicked: OnHashtagClickedListener,
) : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomsheetResultBinding::bind)

    private val clipboardManager
        get() = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.bottomsheet_result, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.bottomsheetResultTv.addHashtags(predictionList, onHashTagClicked)
        setListeners()
    }

    private fun setListeners() {
        binding.bottomsheetResultBtnInsta.setOnClickListener {
            shareToInsta(image)
        }
        binding.bottomsheetResultBtnCopy.setOnClickListener {
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(
                    "hashtags",
                    binding.bottomsheetResultTv.text
                )
            )
            binding.bottomsheetResultBtnInsta.isEnabled = true
            toast("Copied")
        }
    }

    /**
     * From https://developers.facebook.com/docs/instagram/sharing-to-feed
     */
    private fun shareToInsta(media: File) {
        // Create the new Intent using the 'Send' action.
        val share = Intent(Intent.ACTION_SEND)
        // Set the MIME type
        share.type = "image/*"
        // Create the URI from the media
        val uri: Uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            media
        )
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri)
        share.setPackage("com.instagram.android")
        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, getString(R.string.share_to)))
    }
}