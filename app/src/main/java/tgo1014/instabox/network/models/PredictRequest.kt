package tgo1014.instabox.network.models

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class PredictRequest(val inputs: List<Input>) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Input(val `data`: Data) {
        @Keep
        @JsonClass(generateAdapter = true)
        data class Data(val image: Image) {
            @Keep
            @JsonClass(generateAdapter = true)
            data class Image(val base64: String)
        }
    }

    constructor(base64: String) : this(listOf(Input(Input.Data(Input.Data.Image(base64)))))
}