package tgo1014.instabox.pickpicture.models

import java.io.File

sealed class PickPictureState {
    object Uploading : PickPictureState()
    object ShowPicker : PickPictureState()
    class Success(val predictionList: List<Prediction>, val image: File) : PickPictureState()
    class Error(val error: Errors) : PickPictureState()
}
