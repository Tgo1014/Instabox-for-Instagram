package tgo1014.instabox.common.network

import retrofit2.http.*
import tgo1014.instabox.common.network.responses.FeedResponse

interface InstagramApi {

    @POST("feed/user/{userId}")
    suspend fun gefFeed(
        @Path("userId") userId: String? = null,
        @Query("max_id") maxId: String? = null
    ): FeedResponse

    @POST("feed/only_me_feed")
    suspend fun getArchivedFeed(@Query("max_id") maxId: String? = null): FeedResponse

    @FormUrlEncoded
    @POST("media/{mediaId}/{action}/")
    suspend fun changeMediaVisibility(
        @Path("mediaId") mediaId: String,
        @Path("action") action: String,
        @Field("media_id") mediaIdForm: String
    ): FeedResponse

}