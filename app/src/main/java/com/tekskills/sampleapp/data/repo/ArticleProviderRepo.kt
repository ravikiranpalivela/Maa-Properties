package com.tekskills.sampleapp.data.repo

import com.tekskills.sampleapp.data.remote.NewsService
import com.tekskills.sampleapp.data.remote.RetrofitInstance
import com.tekskills.sampleapp.model.AllNewsDetailsData
import com.tekskills.sampleapp.model.NewsDetails
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.LikeResponse
import com.tekskills.sampleapp.model.PosterDetails
import com.tekskills.sampleapp.model.PublicAdsDetails
import retrofit2.Response

class ArticleProviderRepo {

    private val retService = RetrofitInstance.getRetrofitInstance().create(
        NewsService::class.java
    )

    suspend fun getNews(category: String): Response<NewsDetails> {

        return when (category) {

            "All" -> retService.getMainNews()

            "News" -> retService.getMainNews()

            "Wishes" -> retService.getWishes()

            "Posters" -> retService.getMainNews()

            else -> retService.getShorts()
        }
    }

    suspend fun getAllNewsDetails(): Response<AllNewsDetailsData> {
        return retService.getAllNews()
    }

    suspend fun getPoster(): Response<PosterDetails> {
        return retService.getPosters()
    }

    suspend fun getBanner(): Response<BannerItem> {
        return retService.getBanners()
    }

    suspend fun getPublicAds(): Response<PublicAdsDetails> {
        return retService.getPublicAds()
    }

    suspend fun postComments(
        articleID: String,
        type: String,
        text: String
    ): Response<LikeResponse> {
        val requestBody: MutableMap<String, String> = HashMap()
        requestBody["type"] = type
        requestBody["newsId"] = articleID
        requestBody["comments"] = text
        return retService.postComments(requestBody)
    }

    suspend fun updateNewsLike(articleID: Int, type: String): Response<LikeResponse> {
        return retService.updateNewsLike(articleID, type)
    }

    suspend fun updateNewsShare(articleID: Int, type: String): Response<LikeResponse> {
        return retService.updateNewsShare(articleID, type)
    }

}