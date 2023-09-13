package com.tekskills.sampleapp.data.repo

import com.tekskills.sampleapp.data.webdata.NewsService
import com.tekskills.sampleapp.data.webdata.RetrofitInstance
import com.tekskills.sampleapp.model.AllNews
import com.tekskills.sampleapp.model.PosterItem
import retrofit2.Response

class ArticleProvider{
    private val retService = RetrofitInstance.getRetrofitInstance().create(
        NewsService::class.java)

    suspend fun getNews(category: String):Response<AllNews> {

        return when(category){

            "All" -> retService.getAllNews()

            "News" -> retService.getAllNews()

            "Wishes"-> retService.getWishes()

            "Posters" -> retService.getWishes()

            else ->  retService.getShorts()
        }
    }

    suspend fun getPoster(category: String):Response<PosterItem> {

        return when(category){
            "Posters" -> retService.getPosters()
            else -> {retService.getPosters()}
        }
    }
}