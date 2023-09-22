package com.tekskills.sampleapp.data.remote

import com.tekskills.sampleapp.model.NewsDetails
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.PosterDetails
import retrofit2.Response
import retrofit2.http.GET

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

}