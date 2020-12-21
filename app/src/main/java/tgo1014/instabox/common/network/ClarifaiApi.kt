package tgo1014.instabox.common.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import tgo1014.instabox.BuildConfig
import tgo1014.instabox.pickpicture.models.PredictRequest
import tgo1014.instabox.pickpicture.models.PredictResponse

interface ClarifaiApi {

    @POST("models/aaa03c23b3724a16a56b629203edc62c/versions/aa9ca48295b37401f8af92ad1af0d91d/outputs")
    @Headers("Content-Type: application/json")
    suspend fun getHashtags(
        @Body base64: PredictRequest,
        @Header("Authorization") auth: String = BuildConfig.KEY,
    ): PredictResponse
}