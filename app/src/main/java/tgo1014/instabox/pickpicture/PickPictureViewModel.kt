package tgo1014.instabox.pickpicture

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.util.Base64
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tgo1014.instabox.BuildConfig
import tgo1014.instabox.common.network.ClarifaiApi
import tgo1014.instabox.common.utils.formatToCamelCase
import tgo1014.instabox.common.utils.launchOnMain
import tgo1014.instabox.common.utils.tryOnIO
import tgo1014.instabox.pickpicture.models.Errors
import tgo1014.instabox.pickpicture.models.PickPictureState
import tgo1014.instabox.common.network.models.PredictRequest
import tgo1014.instabox.pickpicture.models.Prediction
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.Charset

class PickPictureViewModel @ViewModelInject constructor(
    private val clarifaiApi: ClarifaiApi,
) : ViewModel() {

    private val _state = MutableLiveData<PickPictureState>()
    val state: LiveData<PickPictureState>
        get() = _state

    fun imageSelected(
        context: Context,
        cacheDir: File,
        contextResolver: ContentResolver,
        data: Intent?,
    ) {

        stateUploading()

        if (data?.data == null) {
            stateErrorUnableToGetImage()
            return
        }

        tryOnIO(
            {
                val tempFile = File(cacheDir, "image.jpg")
                withContext(Dispatchers.IO) {
                    tempFile.createNewFile()
                    copyStreamToFile(contextResolver.openInputStream(data.data!!)!!, tempFile)
                }
                val compressedImageFile = Compressor.compress(context, tempFile)
                val base64 = Base64.encode(compressedImageFile.readBytes(), Base64.NO_WRAP)
                    .toString(Charset.defaultCharset())
                val response = clarifaiApi.getHashtags(PredictRequest(base64))
                    .toPredictionList()
                    .formatToCamelCase()
                launchOnMain { stateSuccess(response, compressedImageFile) }
            },
            {
                Timber.d(it)
                launchOnMain { stateErrorUnableToGetImage() }
            }
        )
    }

    fun onPickImageClikced() {
        if (BuildConfig.KEY == "Key") {
            _state.value = PickPictureState.Error(Errors.InvalidClarifaiKeyError)
            return
        }
        _state.value = PickPictureState.ShowPicker
    }

    private fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    private fun stateUploading() {
        _state.value = PickPictureState.Uploading
    }

    private fun stateSuccess(response: List<Prediction>, image: File) {
        _state.value = PickPictureState.Success(response, image)
    }

    private fun stateErrorUnableToGetImage() {
        _state.value = PickPictureState.Error(Errors.UnableToGetImageError)
    }
}