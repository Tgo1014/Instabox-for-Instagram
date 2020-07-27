package tgo1014.instabox.pickpicture

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.pick_picture_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import tgo1014.instabox.R
import tgo1014.instabox.common.utils.toast
import tgo1014.instabox.main.MainActivity
import tgo1014.instabox.pickpicture.models.PickPictureState
import tgo1014.instabox.pickpicture.models.Prediction
import java.io.File


class PickPictureFragment : Fragment(R.layout.pick_picture_fragment) {

    private val viewModel: PickPictureViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        viewModel.state.observe(viewLifecycleOwner, Observer { handleState(it) })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_REQUEST) {
            viewModel.imageSelected(
                requireContext(),
                requireActivity().cacheDir,
                requireActivity().contentResolver,
                data
            )
            return
        }
        toast(getString(R.string.fail_to_get_image))
    }

    private fun handleState(state: PickPictureState) {
        when (state) {
            is PickPictureState.Success -> {
                handleViewLoading(false)
                showBottomSheet(state.predictionList, state.image)
            }
            PickPictureState.Uploading -> handleViewLoading(true)
            is PickPictureState.Error -> handleViewLoading(false)
        }
    }

    private fun showBottomSheet(predictionList: List<Prediction>, image: File) {
        val dialog = BottomSheetFragment(predictionList, image) { hashtag ->
            val uri: Uri = Uri.parse("https://www.instagram.com/explore/tags/$hashtag/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        dialog.show(parentFragmentManager, BottomSheetFragment::class.java.name)
    }

    private fun handleViewLoading(isLoading: Boolean) {
        pickPicProgress.isVisible = isLoading
        pickPickProgressTv.isVisible = isLoading
        pickPicLottie.isVisible = !isLoading
        pickPicFab.isEnabled = !isLoading
    }

    private fun setListeners() {
        pickPicFab.setOnClickListener { startImagePicker() }
        (activity as? MainActivity)?.onHashtagFragmentResumedListener = {
            pickPicLottie.playAnimation()
        }
    }

    private fun startImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            IMAGE_PICK_REQUEST
        )
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 777
        fun newInstance() = PickPictureFragment()
    }

}