package com.tekskills.sampleapp.data.remote

import com.tekskills.sampleapp.model.NewsDetails
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.LikeResponse
import com.tekskills.sampleapp.model.PosterDetails
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface NewsService{

    @GET(APIEndPoint.GET_WISHES_LIST)
    suspend fun getWishes(): Response<NewsDetails>

    @GET(APIEndPoint.GET_ALL_NEWS_LIST)
    suspend fun getAllNews(): Response<NewsDetails>

    @GET(APIEndPoint.GET_MAIN_NEWS_LIST)
    suspend fun getMainNews(): Response<NewsDetails>

    @GET(APIEndPoint.GET_POSTERS_LIST)
    suspend fun getPosters(): Response<PosterDetails>

    @GET(APIEndPoint.GET_SHORT_LIST)
    suspend fun getShorts(): Response<NewsDetails>

    @GET(APIEndPoint.GET_BANNER_LIST)
    suspend fun getBanners(): Response<BannerItem>

    @PUT("${APIEndPoint.POST_NEWS_LIKE}/{id}")
    suspend fun postNewsLike(@Path("id") id:String): Response<LikeResponse>

    @PUT("${APIEndPoint.POST_WISHES_LIKE}/{id}")
    suspend fun postWishesLike(@Path("id") articleID:String): Response<LikeResponse>

    @PUT("${APIEndPoint.POST_SHORTS_LIKE}/{id}")
    suspend fun postShortsLike(@Path("id") articleID:String): Response<LikeResponse>

    @PUT("${APIEndPoint.POST_POSTER_LIKE}/{id}")
    suspend fun postPostersLike(@Path("id") articleID:String): Response<LikeResponse>
//    @Headers("Content-Type: application/json","X-HTTP-Method-Override: POST")
    @POST("https://admin2.maaproperties.com/api/common")
    suspend fun postComments(@Body user: Map<String,String>): Response<LikeResponse>

//    @Headers("Content-Type: application/json")
//    @PUT(APIEndPoint.PUT_LIKES_COMMON)
//    suspend fun updateNewsLike(
//        @Query("id") id: Int,
//        @Query("type") type: String,
//    ): Response<LikeResponse>

    @PUT("https://admin2.maaproperties.com/api/common")
    suspend fun updateNewsLike(
        @Query("id") id: Int = 855,
        @Query("type") type: String = "news"
    ): Response<LikeResponse>

    @PUT
    suspend fun updateNewsLike(@Url url: String): Response<LikeResponse>
    @PUT("https://admin2.maaproperties.com/api/common?id=855&type=news")
    suspend fun checkLike(): Response<LikeResponse>
}