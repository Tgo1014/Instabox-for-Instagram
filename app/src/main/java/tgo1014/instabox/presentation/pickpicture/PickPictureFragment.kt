package tgo1014.instabox.presentation.pickpicture

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import tgo1014.instabox.R
import tgo1014.instabox.utils.longToast
import tgo1014.instabox.utils.toast
import tgo1014.instabox.utils.viewBinding
import tgo1014.instabox.databinding.PickPictureFragmentBinding
import tgo1014.instabox.presentation.main.MainActivity
import tgo1014.instabox.presentation.pickpicture.models.Errors
import tgo1014.instabox.presentation.pickpicture.models.PickPictureState
import tgo1014.instabox.presentation.pickpicture.models.Prediction
import java.io.File

@AndroidEntryPoint
class PickPictureFragment : Fragment(R.layout.pick_picture_fragment) {

    private val binding by viewBinding(PickPictureFragmentBinding::bind)
    private val viewModel: PickPictureViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        lifecycleScope.launchWhenCreated {
            viewModel.state.collect(::handleState)
        }
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
            PickPictureState.ShowPicker -> startImagePicker()
            is PickPictureState.Error -> {
                handleViewLoading(false)
                when (state.error) {
                    Errors.InvalidClarifaiKeyError -> longToast(getString(R.string.invalid_clarifai_key))
                    else -> toast(getString(R.string.error))
                }
            }
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
        with(binding) {
            pickPicProgress.isVisible = isLoading
            pickPickProgressTv.isVisible = isLoading
            pickPicLottie.isVisible = !isLoading
            pickPicFab.isEnabled = !isLoading
        }
    }

    private fun setListeners() {
        binding.pickPicFab.setOnClickListener { viewModel.onPickImageClikced() }
        (activity as? MainActivity)?.onHashtagFragmentResumedListener = {
            binding.pickPicLottie.playAnimation()
        }
    }

    private fun startImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 777
        fun newInstance() = PickPictureFragment()
    }
}