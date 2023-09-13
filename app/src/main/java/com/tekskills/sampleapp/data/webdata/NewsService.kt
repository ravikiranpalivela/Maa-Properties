package com.tekskills.sampleapp.data.webdata

import com.tekskills.sampleapp.model.AllNews
import com.tekskills.sampleapp.model.PosterItem
import retrofit2.Response
import retrofit2.http.GET

interface NewsService{

    @GET(APIEndPoint.GET_WISHES_LIST)
    suspend fun getWishes(): Response<AllNews>

    @GET(APIEndPoint.GET_ALL_NEWS_LIST)
    suspend fun getAllNews(): Response<AllNews>

    @GET(APIEndPoint.GET_MAIN_NEWS_LIST)
    suspend fun getMainNews(): Response<AllNews>

    @GET(APIEndPoint.GET_POSTERS_LIST)
    suspend fun getPosters(): Response<PosterItem>


    @GET(APIEndPoint.GET_SHORT_LIST)
    suspend fun getShorts(): Response<AllNews>

}