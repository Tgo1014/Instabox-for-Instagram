package tgo1014.instabox.pickpicture.models

import java.io.File

sealed class PickPictureState {
    object Uploading : PickPictureState()
    class Success(val predictionList: List<Prediction>, val image: File) : PickPictureState()
    class Error(e: Exception) : PickPictureState()
}
