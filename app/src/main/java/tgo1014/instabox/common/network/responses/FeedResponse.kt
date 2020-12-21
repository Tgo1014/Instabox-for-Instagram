package tgo1014.instabox.common.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedResponse(
    @Json(name = "auto_load_more_enabled")
    val autoLoadMoreEnabled: Boolean? = null,
    val items: List<Item?>? = null,
    @Json(name = "max_id")
    val maxId: String? = null,
    @Json(name = "more_available")
    val moreAvailable: Boolean? = null,
    @Json(name = "next_max_id")
    val nextMaxId: String? = null,
    @Json(name = "num_results")
    val numResults: Long? = null,
    val status: String? = null,
) {
    @JsonClass(generateAdapter = true)
    data class Item(
        @Json(name = "can_see_insights_as_brand")
        val canSeeInsightsAsBrand: Boolean? = null,
        @Json(name = "can_view_more_preview_comments")
        val canViewMorePreviewComments: Boolean? = null,
        @Json(name = "can_viewer_reshare")
        val canViewerReshare: Boolean? = null,
        @Json(name = "can_viewer_save")
        val canViewerSave: Boolean? = null,
        val caption: Caption? = null,
        @Json(name = "caption_is_edited")
        val captionIsEdited: Boolean? = null,
        @Json(name = "carousel_media")
        val carouselMedia: List<CarouselMedia?>? = null,
        @Json(name = "carousel_media_count")
        val carouselMediaCount: Long? = null,
        @Json(name = "client_cache_key")
        val clientCacheKey: String? = null,
        val code: String? = null,
        @Json(name = "comment_count")
        val commentCount: Long? = null,
        @Json(name = "comment_likes_enabled")
        val commentLikesEnabled: Boolean? = null,
        @Json(name = "comment_threading_enabled")
        val commentThreadingEnabled: Boolean? = null,
        @Json(name = "device_timestamp")
        val deviceTimestamp: Long? = null,
        @Json(name = "facepile_top_likers")
        val facepileTopLikers: List<FacepileTopLiker?>? = null,
        @Json(name = "fb_user_tags")
        val fbUserTags: FbUserTags? = null,
        @Json(name = "filter_type")
        val filterType: Long? = null,
        @Json(name = "has_audio")
        val hasAudio: Boolean? = null,
        @Json(name = "has_liked")
        val hasLiked: Boolean? = null,
        @Json(name = "has_more_comments")
        val hasMoreComments: Boolean? = null,
        val id: String? = null,
        @Json(name = "image_versions2")
        val imageVersions2: ImageVersions2? = null,
        @Json(name = "inline_composer_display_condition")
        val inlineComposerDisplayCondition: String? = null,
        @Json(name = "is_dash_eligible")
        val isDashEligible: Int? = null,
        val lat: Double? = null,
        @Json(name = "like_count")
        val likeCount: Long? = null,
        val likers: List<Any?>? = null,
        val lng: Double? = null,
        val location: Location? = null,
        @Json(name = "max_num_visible_preview_comments")
        val maxNumVisiblePreviewComments: Long? = null,
        @Json(name = "media_type")
        val mediaType: Long? = null,
        @Json(name = "next_max_id")
        val nextMaxId: Long? = null,
        @Json(name = "number_of_qualities")
        val numberOfQualities: Int? = null,
        @Json(name = "organic_tracking_token")
        val organicTrackingToken: String? = null,
        @Json(name = "original_height")
        val originalHeight: Long? = null,
        @Json(name = "original_width")
        val originalWidth: Long? = null,
        @Json(name = "photo_of_you")
        val photoOfYou: Boolean? = null,
        val pk: Long? = null,
        @Json(name = "preview_comments")
        val previewComments: List<PreviewComment?>? = null,
        @Json(name = "taken_at")
        val takenAt: Long? = null,
        @Json(name = "top_likers")
        val topLikers: List<String?>? = null,
        val user: User? = null,
        @Json(name = "video_codec")
        val videoCodec: String? = null,
        @Json(name = "video_dash_manifest")
        val videoDashManifest: String? = null,
        @Json(name = "video_duration")
        val videoDuration: Double? = null,
        @Json(name = "video_versions")
        val videoVersions: List<VideoVersion?>? = null,
        @Json(name = "view_count")
        val viewCount: Double? = null,
        val visibility: String? = null,
    ) {
        @JsonClass(generateAdapter = true)
        data class Caption(
            @Json(name = "bit_flags")
            val bitFlags: Long? = null,
            @Json(name = "content_type")
            val contentType: String? = null,
            @Json(name = "created_at")
            val createdAt: Long? = null,
            @Json(name = "created_at_utc")
            val createdAtUtc: Long? = null,
            @Json(name = "did_report_as_spam")
            val didReportAsSpam: Boolean? = null,
            @Json(name = "media_id")
            val mediaId: Long? = null,
            val pk: Long? = null,
            @Json(name = "share_enabled")
            val shareEnabled: Boolean? = null,
            val status: String? = null,
            val text: String? = null,
            val type: Long? = null,
            val user: User? = null,
            @Json(name = "user_id")
            val userId: Long? = null,
        ) {
            @JsonClass(generateAdapter = true)
            data class User(
                @Json(name = "allowed_commenter_type")
                val allowedCommenterType: String? = null,
                @Json(name = "can_boost_post")
                val canBoostPost: Boolean? = null,
                @Json(name = "can_see_organic_insights")
                val canSeeOrganicInsights: Boolean? = null,
                @Json(name = "full_name")
                val fullName: String? = null,
                @Json(name = "has_anonymous_profile_picture")
                val hasAnonymousProfilePicture: Boolean? = null,
                @Json(name = "is_private")
                val isPrivate: Boolean? = null,
                @Json(name = "is_unpublished")
                val isUnpublished: Boolean? = null,
                @Json(name = "is_verified")
                val isVerified: Boolean? = null,
                @Json(name = "latest_reel_media")
                val latestReelMedia: Long? = null,
                val pk: Long? = null,
                @Json(name = "profile_pic_id")
                val profilePicId: String? = null,
                @Json(name = "profile_pic_url")
                val profilePicUrl: String? = null,
                @Json(name = "reel_auto_archive")
                val reelAutoArchive: String? = null,
                @Json(name = "show_insights_terms")
                val showInsightsTerms: Boolean? = null,
                val username: String? = null,
            )
        }

        @JsonClass(generateAdapter = true)
        data class CarouselMedia(
            @Json(name = "carousel_parent_id")
            val carouselParentId: String? = null,
            @Json(name = "fb_user_tags")
            val fbUserTags: FbUserTags? = null,
            val id: String? = null,
            @Json(name = "image_versions2")
            val imageVersions2: ImageVersions2? = null,
            @Json(name = "media_type")
            val mediaType: Long? = null,
            @Json(name = "original_height")
            val originalHeight: Long? = null,
            @Json(name = "original_width")
            val originalWidth: Long? = null,
            val pk: Long? = null,
        ) {
            @JsonClass(generateAdapter = true)
            data class FbUserTags(
                @Json(name = "in")
                val inX: List<Any?>? = null,
            )

            @JsonClass(generateAdapter = true)
            data class ImageVersions2(
                val candidates: List<Candidate?>? = null,
            ) {
                @JsonClass(generateAdapter = true)
                data class Candidate(
                    val height: Long? = null,
                    val url: String? = null,
                    val width: Long? = null,
                )
            }
        }

        @JsonClass(generateAdapter = true)
        data class FacepileTopLiker(
            @Json(name = "full_name")
            val fullName: String? = null,
            @Json(name = "is_private")
            val isPrivate: Boolean? = null,
            @Json(name = "is_verified")
            val isVerified: Boolean? = null,
            val pk: Long? = null,
            @Json(name = "profile_pic_id")
            val profilePicId: String? = null,
            @Json(name = "profile_pic_url")
            val profilePicUrl: String? = null,
            val username: String? = null,
        )

        @JsonClass(generateAdapter = true)
        data class FbUserTags(
            @Json(name = "in")
            val inX: List<Any?>? = null,
        )

        @JsonClass(generateAdapter = true)
        data class ImageVersions2(
            val candidates: List<Candidate?>? = null,
        ) {
            @JsonClass(generateAdapter = true)
            data class Candidate(
                val height: Long? = null,
                val url: String? = null,
                val width: Long? = null,
            )
        }

        @JsonClass(generateAdapter = true)
        data class Location(
            val address: String? = null,
            val city: String? = null,
            @Json(name = "external_source")
            val externalSource: String? = null,
            @Json(name = "facebook_places_id")
            val facebookPlacesId: Long? = null,
            val lat: Double? = null,
            val lng: Double? = null,
            val name: String? = null,
            val pk: Long? = null,
            @Json(name = "short_name")
            val shortName: String? = null,
        )

        @JsonClass(generateAdapter = true)
        data class PreviewComment(
            @Json(name = "bit_flags")
            val bitFlags: Long? = null,
            @Json(name = "comment_like_count")
            val commentLikeCount: Long? = null,
            @Json(name = "content_type")
            val contentType: String? = null,
            @Json(name = "created_at")
            val createdAt: Long? = null,
            @Json(name = "created_at_utc")
            val createdAtUtc: Long? = null,
            @Json(name = "did_report_as_spam")
            val didReportAsSpam: Boolean? = null,
            @Json(name = "has_liked_comment")
            val hasLikedComment: Boolean? = null,
            @Json(name = "has_translation")
            val hasTranslation: Boolean? = null,
            @Json(name = "media_id")
            val mediaId: Long? = null,
            @Json(name = "parent_comment_id")
            val parentCommentId: Long? = null,
            val pk: Long? = null,
            @Json(name = "restricted_status")
            val restrictedStatus: Long? = null,
            @Json(name = "share_enabled")
            val shareEnabled: Boolean? = null,
            val status: String? = null,
            val text: String? = null,
            val type: Long? = null,
            val user: User? = null,
            @Json(name = "user_id")
            val userId: Long? = null,
        ) {
            @JsonClass(generateAdapter = true)
            data class User(
                @Json(name = "full_name")
                val fullName: String? = null,
                @Json(name = "is_private")
                val isPrivate: Boolean? = null,
                @Json(name = "is_verified")
                val isVerified: Boolean? = null,
                val pk: Long? = null,
                @Json(name = "profile_pic_id")
                val profilePicId: String? = null,
                @Json(name = "profile_pic_url")
                val profilePicUrl: String? = null,
                val username: String? = null,
            )
        }

        @JsonClass(generateAdapter = true)
        data class User(
            @Json(name = "allowed_commenter_type")
            val allowedCommenterType: String? = null,
            @Json(name = "can_boost_post")
            val canBoostPost: Boolean? = null,
            @Json(name = "can_see_organic_insights")
            val canSeeOrganicInsights: Boolean? = null,
            @Json(name = "full_name")
            val fullName: String? = null,
            @Json(name = "has_anonymous_profile_picture")
            val hasAnonymousProfilePicture: Boolean? = null,
            @Json(name = "is_private")
            val isPrivate: Boolean? = null,
            @Json(name = "is_unpublished")
            val isUnpublished: Boolean? = null,
            @Json(name = "is_verified")
            val isVerified: Boolean? = null,
            @Json(name = "latest_reel_media")
            val latestReelMedia: Long? = null,
            val pk: Long? = null,
            @Json(name = "profile_pic_id")
            val profilePicId: String? = null,
            @Json(name = "profile_pic_url")
            val profilePicUrl: String? = null,
            @Json(name = "reel_auto_archive")
            val reelAutoArchive: String? = null,
            @Json(name = "show_insights_terms")
            val showInsightsTerms: Boolean? = null,
            val username: String? = null,
        )

        @JsonClass(generateAdapter = true)
        data class VideoVersion(
            val height: Int? = null,
            val id: String? = null,
            val type: Int? = null,
            val url: String? = null,
            val width: Int? = null,
        )
    }
}