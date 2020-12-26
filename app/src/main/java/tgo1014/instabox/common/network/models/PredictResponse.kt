package tgo1014.instabox.common.network.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import tgo1014.instabox.pickpicture.models.Prediction

@Keep
data class PredictResponse(
    val outputs: List<Output?>? = null,
    val status: Status? = null,
) {
    @Keep
    data class Output(
        @Json(name = "created_at")
        val createdAt: String? = null,
        val `data`: Data? = null,
        val id: String? = null,
        val input: Input? = null,
        val model: Model? = null,
        val status: Status? = null,
    ) {
        @Keep
        data class Data(
            val concepts: List<Concept?>? = null,
        ) {
            @Keep
            data class Concept(
                @Json(name = "app_id")
                val appId: String? = null,
                val id: String? = null,
                val name: String? = null,
                val value: Double? = null,
            )
        }

        @Keep
        data class Input(
            val `data`: Data? = null,
            val id: String? = null,
        ) {
            @Keep
            data class Data(
                val image: Image? = null,
            ) {
                @Keep
                data class Image(
                    val base64: String? = null,
                    val url: String? = null,
                )
            }
        }

        @Keep
        data class Model(
            @Json(name = "app_id")
            val appId: String? = null,
            @Json(name = "created_at")
            val createdAt: String? = null,
            @Json(name = "display_name")
            val displayName: String? = null,
            val id: String? = null,
            @Json(name = "model_version")
            val modelVersion: ModelVersion? = null,
            val name: String? = null,
            @Json(name = "output_info")
            val outputInfo: OutputInfo? = null,
        ) {
            @Keep
            data class ModelVersion(
                @Json(name = "created_at")
                val createdAt: String? = null,
                val id: String? = null,
                val status: Status? = null,
                @Json(name = "worker_id")
                val workerId: String? = null,
            ) {
                @Keep
                data class Status(
                    val code: Int? = null,
                    val description: String? = null,
                )
            }

            @Keep
            data class OutputInfo(
                val message: String? = null,
                val type: String? = null,
                @Json(name = "type_ext")
                val typeExt: String? = null,
            )
        }

        @Keep
        data class Status(
            val code: Int? = null,
            val description: String? = null,
        )
    }

    @Keep
    data class Status(
        val code: Int? = null,
        val description: String? = null,
        @Json(name = "req_id")
        val reqId: String? = null,
    )

    fun toPredictionList(): List<Prediction> {
        val predictions = this.outputs?.firstOrNull()?.data?.concepts?.filter { it?.name != null }
            ?.map { Prediction(it!!.name!!) }
        return predictions.orEmpty()
    }
}