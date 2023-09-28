package com.tekskills.sampleapp.data.repo

import com.tekskills.sampleapp.data.remote.APIEndPoint
import com.tekskills.sampleapp.data.remote.NewsService
import com.tekskills.sampleapp.data.remote.RetrofitInstance
import com.tekskills.sampleapp.model.NewsDetails
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.LikeResponse
import com.tekskills.sampleapp.model.PosterDetails
import org.json.JSONObject
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

            "Posters" -> retService.getAllNews()

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

    suspend fun postNewsLike(articleID : String): Response<LikeResponse>
    {
        return  retService.postNewsLike(articleID)
    }

    suspend fun postPostersLike(articleID : String): Response<LikeResponse>
    {
        return  retService.postPostersLike(articleID)
    }

    suspend fun postWishesLike(articleID : String): Response<LikeResponse>
    {
        return  retService.postWishesLike(articleID)
    }

    suspend fun postShortsLike(articleID : String): Response<LikeResponse>
    {
        return  retService.postShortsLike(articleID)
    }

    suspend fun postComments(articleID:String,type:String,text:String): Response<LikeResponse>
    {
        val requestBody: MutableMap<String, String> = HashMap()
        requestBody["type"] = type
        requestBody["newsId"] = articleID
        requestBody["comments"] = text
        return  retService.postComments(requestBody)
    }

    suspend fun updateNewsLike(articleID:Int,type:String): Response<LikeResponse>
    {
        return  retService.updateNewsLike(articleID,type)
    }

    suspend fun checkLike(): Response<LikeResponse>
    {
        return retService.checkLike()
    }
}