package tgo1014.instabox.common.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import tgo1014.instabox.common.network.models.FeedResponse

interface InstagramApi {

    @POST("feed/user/{userId}")
    suspend fun gefFeed(
        @Path("userId") userId: String? = null,
        @Query("max_id") maxId: String? = null,
    ): FeedResponse

    @POST("feed/only_me_feed")
    suspend fun getArchivedFeed(@Query("max_id") maxId: String? = null): FeedResponse

    @FormUrlEncoded
    @POST("media/{mediaId}/{action}/")
    suspend fun changeMediaVisibility(
        @Path("mediaId") mediaId: String,
        @Path("action") action: String,
        @Field("media_id") mediaIdForm: String,
    ): FeedResponse
}