package com.tekskills.sampleapp.data.remote

import com.tekskills.sampleapp.model.AllNewsDetailsData
import com.tekskills.sampleapp.model.NewsDetails
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.LikeResponse
import com.tekskills.sampleapp.model.PosterDetails
import com.tekskills.sampleapp.model.PublicAdsDetails
import com.tekskills.sampleapp.utils.AppConstant.APP_JSON
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface NewsService {

    @GET(APIEndPoint.GET_WISHES_LIST)
    suspend fun getWishes(): Response<NewsDetails>

    @GET(APIEndPoint.GET_ALL_NEWS_LIST)
    suspend fun getAllNews(@Query("builtId") builtId: String = "android",): Response<AllNewsDetailsData>

    @GET("${APIEndPoint.GET_MAIN_NEWS_LIST}/{id}")
    suspend fun getMainNews(@Path("id") builtId: String = "android"): Response<NewsDetails>

    @GET(APIEndPoint.GET_POSTERS_LIST)
    suspend fun getPosters(): Response<PosterDetails>

    @GET(APIEndPoint.GET_SHORT_LIST)
    suspend fun getShorts(): Response<NewsDetails>

    @GET(APIEndPoint.GET_BANNER_LIST)
    suspend fun getBanners(): Response<BannerItem>

    @GET(APIEndPoint.GET_PUBLIC_ADS)
    suspend fun getPublicAds(): Response<PublicAdsDetails>

    @POST(APIEndPoint.POST_COMMENTS)
    suspend fun postComments(@Body user: Map<String, String>): Response<LikeResponse>

    @PUT(APIEndPoint.PUT_LIKES_COMMON)
    suspend fun updateNewsLike(
        @Query("id") id: Int = 0,
        @Query("builtId") builtId: String = "android",
        @Query("type") type: String = "news"
    ): Response<LikeResponse>

    @POST(APIEndPoint.POST_VOTE)
    suspend fun updatePollingVote(@Query("ipAddress") deviceId: String = "android",
        @Query("pollId") pollId: Int = 0,
        @Query("pollOption") pollOption: String = "news",
        @Query("description") description: String = "news",
        @Query("newsId") newsId: Int = 0,
    ): Response<LikeResponse>

    @Headers(APP_JSON)
    @POST(APIEndPoint.POST_VOTE)
    suspend fun postPollingVote(
        @Body user: Map<String, String>
    ): Response<LikeResponse>

    @PUT(APIEndPoint.PUT_SHARE_COMMON)
    suspend fun updateNewsShare(
        @Query("id") id: Int = 0,
        @Query("builtId") builtId: String = "android",
        @Query("type") type: String = "news"
    ): Response<LikeResponse>

    @GET
    suspend fun getVideoInfo(@Url videoUrl: String): VideoInfoResponse
}

data class VideoInfoResponse(
//    @Json(name = "download_url")
    val downloadUrl: String,
//    @Json(name = "preview_url")
    val previewUrl: String
)