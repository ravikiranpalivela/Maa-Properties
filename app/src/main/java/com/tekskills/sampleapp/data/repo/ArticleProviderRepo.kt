package com.tekskills.sampleapp.data.repo

import com.tekskills.sampleapp.data.remote.NewsService
import com.tekskills.sampleapp.data.remote.RetrofitInstance
import com.tekskills.sampleapp.model.NewsDetails
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.PosterDetails
import retrofit2.Response

class ArticleProviderRepo {
    private val retService = RetrofitInstance.getRetrofitInstance().create(
        NewsService::class.java
    )

    suspend fun getNews(category: String): Response<NewsDetails> {

        return when (category) {

            "All" -> retService.getAllNews()

            "News" -> retService.getAllNews()

            "Wishes" -> retService.getWishes()

            "Posters" -> retService.getWishes()

            else -> retService.getShorts()
        }
    }

    suspend fun getPoster(category: String): Response<PosterDetails> {

        return when (category) {
            "Posters" -> retService.getPosters()
            else -> {
                retService.getPosters()
            }
        }
    }

    suspend fun getBanner(): Response<BannerItem> {
        return retService.getBanners()
    }
}