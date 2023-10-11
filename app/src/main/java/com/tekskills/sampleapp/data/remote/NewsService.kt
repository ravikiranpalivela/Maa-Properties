package com.tekskills.sampleapp.data.remote

import com.tekskills.sampleapp.model.AllNewsDetailsData
import com.tekskills.sampleapp.model.NewsDetails
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.LikeResponse
import com.tekskills.sampleapp.model.PosterDetails
import com.tekskills.sampleapp.model.PublicAdsDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface NewsService{

    @GET(APIEndPoint.GET_WISHES_LIST)
    suspend fun getWishes(): Response<NewsDetails>

    @GET(APIEndPoint.GET_ALL_NEWS_LIST)
    suspend fun getAllNews(): Response<AllNewsDetailsData>

    @GET(APIEndPoint.GET_MAIN_NEWS_LIST)
    suspend fun getMainNews(): Response<NewsDetails>

    @GET(APIEndPoint.GET_POSTERS_LIST)
    suspend fun getPosters(): Response<PosterDetails>

    @GET(APIEndPoint.GET_SHORT_LIST)
    suspend fun getShorts(): Response<NewsDetails>

    @GET(APIEndPoint.GET_BANNER_LIST)
    suspend fun getBanners(): Response<BannerItem>

    @GET(APIEndPoint.GET_PUBLIC_ADS)
    suspend fun getPublicAds(): Response<PublicAdsDetails>

    @POST(APIEndPoint.POST_COMMENTS)
    suspend fun postComments(@Body user: Map<String,String>): Response<LikeResponse>

    @PUT(APIEndPoint.PUT_LIKES_COMMON)
    suspend fun updateNewsLike(
        @Query("id") id: Int = 0,
        @Query("type") type: String = "news"
    ): Response<LikeResponse>

    @PUT(APIEndPoint.PUT_SHARE_COMMON)
    suspend fun updateNewsShare(
        @Query("id") id: Int = 0,
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