package com.tekskills.sampleapp.ui.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.google.gson.Gson
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.local.ArticleViewCount
import com.tekskills.sampleapp.data.local.ArticlesAllNews
import com.tekskills.sampleapp.data.local.ArticlesRepository
import com.tekskills.sampleapp.data.prefrences.SharedPrefManager
import com.tekskills.sampleapp.data.repo.ArticleProviderRepo
import com.tekskills.sampleapp.model.AllNewsDetailsData
import com.tekskills.sampleapp.model.NewsDetails
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.PosterDetails
import com.tekskills.sampleapp.model.VideoInfo
import com.tekskills.sampleapp.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Exception

class MainViewModel(
    private val repository: ArticlesRepository,
    private val prefrences: SharedPrefManager
) : ViewModel(), Observable {
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    @Bindable
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    val category: MutableLiveData<String> = MutableLiveData("All")

    private val message: MutableLiveData<Event<String>> = MutableLiveData()
    val errorMessage get() = message

    var responseLiveData: MutableLiveData<Response<NewsDetails>?> = MutableLiveData()
    var responseEditorLiveData: MutableLiveData<Response<PosterDetails>?> = MutableLiveData()
    var responseBannerLiveData: MutableLiveData<Response<BannerItem>?> = MutableLiveData()
    var responseAllNewsLiveData: MutableLiveData<Response<AllNewsDetailsData>?> = MutableLiveData()
    val videoInfoLiveData = MutableLiveData<VideoInfo>()

    var bookmarkList: LiveData<List<ArticlesAllNews>> = repository.articles

    val _viewCount: MutableLiveData<ArticleViewCount> = MutableLiveData<ArticleViewCount>()
    val viewCount: LiveData<ArticleViewCount> get() = _viewCount

    val appPreferences: SharedPrefManager
        get() = prefrences

    fun refreshResponse() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                when (category.value!!) {
                    "All" ->{
                        getAllNewsDetails()
                    }
                    "News", "Wishes" -> {
                        getNewsDetails()
                    }

                    "Posters" -> {
                        getPosterDetails()
                    }

                    else -> {
                        getAllNewsDetails()
                    }
                }
            } catch (e: Exception) {
                responseLiveData.postValue(null)
                message.value = Event(e.toString())
            }

            isLoading.value = false
        }

        viewModelScope.launch {
            getBannerDetails()
        }
    }

    private suspend fun getNewsDetails() {
        val response = ArticleProviderRepo().getNews(category.value!!)
        if (response.isSuccessful) {
            responseLiveData.postValue(response)
        } else {
            responseLiveData.postValue(null)
            message.value = Event(response.errorBody().toString())
        }
    }

    fun fetchVideoInfo(videoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val videoInfo = getVideoInfo(videoUrl)
                videoInfoLiveData.postValue(videoInfo)
            } catch (e: Exception) {
                // Handle any errors or exceptions here
            }
        }
    }

    private fun getVideoInfo(videoUrl: String): VideoInfo {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(videoUrl)
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody: ResponseBody? = response.body
            responseBody?.let {
                // Parse the video information from the response
                val title = "Video Title" // Replace with your parsing logic
                val description = "Video Description" // Replace with your parsing logic
                val duration = "Video Duration" // Replace with your parsing logic

                return VideoInfo(title, description, duration, videoUrl)
            }
        }

        throw Exception("Failed to fetch video information")
    }

    fun postNewsLike(newsID: Int,newsType: String) {
        viewModelScope.launch {
//           val response = ArticleProviderRepo().checkLike()
            val response = when (category.value) {
                "All" -> ArticleProviderRepo().updateNewsLike(newsID,newsType)

                "News" -> ArticleProviderRepo().updateNewsLike(newsID,"NEWS")

                "Wishes" -> ArticleProviderRepo().updateNewsLike(newsID,"WISH")

                "Posters" -> ArticleProviderRepo().updateNewsLike(newsID,"POSTER")

                else -> ArticleProviderRepo().updateNewsLike(newsID,"SORT")
            }
            if (response.isSuccessful) {
                refreshUpdatedResponse()
            } else {
                message.value = Event(response.errorBody().toString())
            }
        }
    }

    fun postNewsShare(newsID: Int,newsType: String) {
        viewModelScope.launch {
//           val response = ArticleProviderRepo().checkLike()
            val response = when (category.value) {
                "All" -> ArticleProviderRepo().updateNewsShare(newsID,newsType)

                "News" -> ArticleProviderRepo().updateNewsShare(newsID,"NEWS")

                "Wishes" -> ArticleProviderRepo().updateNewsShare(newsID,"WISH")

                "Posters" -> ArticleProviderRepo().updateNewsShare(newsID,"POSTER")

                else -> ArticleProviderRepo().updateNewsShare(newsID,"SORT")
            }
            if (response.isSuccessful) {
                refreshUpdatedResponse()
            } else {
                message.value = Event(response.errorBody().toString())
            }
        }
    }

    fun postNewsShare(newsID: Int) {
        viewModelScope.launch {
//           val response = ArticleProviderRepo().checkLike()
            val response = when (category.value) {
                "All" -> ArticleProviderRepo().updateNewsShare(newsID,"NEWS")

                "News" -> ArticleProviderRepo().updateNewsShare(newsID,"NEWS")

                "Wishes" -> ArticleProviderRepo().updateNewsShare(newsID,"WISH")

                "Posters" -> ArticleProviderRepo().updateNewsShare(newsID,"POSTER")

                else -> ArticleProviderRepo().updateNewsShare(newsID,"SORT")
            }
            if (response.isSuccessful) {
                refreshUpdatedResponse()
            } else {
                message.value = Event(response.errorBody().toString())
            }
        }
    }

    suspend fun refreshUpdatedResponse()
    {
        when (category.value!!) {
            "All" ->{
                getAllNewsDetails()
            }
            "News", "Wishes" -> {
                getNewsDetails()
            }

            "Posters" -> {
                getPosterDetails()
            }

            else -> {
                getAllNewsDetails()
            }
        }
    }

    fun postComments(newsID: String,comment:String) {
        viewModelScope.launch {

            val response = when (category.value) {
                "All" -> ArticleProviderRepo().postComments(newsID,"NEWS",comment)

                "News" -> ArticleProviderRepo().postComments(newsID,"NEWS",comment)

                "Wishes" -> ArticleProviderRepo().postComments(newsID,"WISH",comment)

                "Posters" -> ArticleProviderRepo().postComments(newsID,"POSTER",comment)

                else -> ArticleProviderRepo().postComments(newsID,"SORT",comment)
            }
            if (response.isSuccessful) {
                refreshUpdatedResponse()
            } else {
                message.value = Event(response.errorBody().toString())
            }
        }
    }

    private suspend fun getPosterDetails() {
        val response = ArticleProviderRepo().getPoster()
        if (response.isSuccessful) {
            responseEditorLiveData.postValue(response)
        } else {
            responseEditorLiveData.postValue(null)
            message.value = Event(response.errorBody().toString())
        }
    }

    private suspend fun getAllNewsDetails() {
        val response = ArticleProviderRepo().getAllNewsDetails()
        if (response.isSuccessful) {
            responseAllNewsLiveData.postValue(response)
        } else {
            responseAllNewsLiveData.postValue(null)
            message.value = Event(response.errorBody().toString())
        }
    }

    private suspend fun getBannerDetails() {
        val response = ArticleProviderRepo().getBanner()
        if (response.isSuccessful) {
            responseBannerLiveData.postValue(response)
        } else {
            responseBannerLiveData.postValue(null)
            message.value = Event(response.errorBody().toString())
        }
    }

    fun changeViewType(id: Int) {

        when (id) {
            R.id.radio_button1 -> {
                viewModelScope.launch {
                    prefrences.saveViewType("List")
                }
            }

            R.id.radio_button2 -> {
                viewModelScope.launch {
                    prefrences.saveViewType("Tab")
                }
            }
        }

        refreshResponse()
    }

    fun changeLanguage(id: Int) {

        when (id) {
            R.id.radio_button1 -> {
                viewModelScope.launch {
                    prefrences.saveLanguage("Telugu")
                }
            }

            R.id.radio_button2 -> {
                viewModelScope.launch {
                    prefrences.saveLanguage("English")
                }
            }
        }
        refreshResponse()
    }

    fun addAArticle(id: Int = 0, news_id: Int, viewCount: Int) {
        viewModelScope.launch {
            repository.insertOrUpdateArticleViewCount(id, news_id, viewCount)
        }
    }

    fun deleteAArticle(bookmark: ArticlesAllNews) {
        viewModelScope.launch {
            repository.deleteArticleFromArticles(bookmark)
        }
    }

    fun clearAllArticles() {
        viewModelScope.launch {
            repository.deleteALlArticleFromArticles()
        }
    }

    fun saveBannerData(dataModel: BannerItem) {
        val gson = Gson()
        val jsonString = gson.toJson(dataModel)
        appPreferences.saveBannerData(jsonString)
    }

    // Retrieve the data from SharedPreferences
    fun getBannerData(): BannerItem? {
        val gson = Gson()
        val jsonString = appPreferences.getBannerData()
        return gson.fromJson(jsonString, BannerItem::class.java)
    }


    fun getArticleCount(news_id: Int): Int {
        var count = 0
        viewModelScope.launch {
            _viewCount.postValue(repository.getArticleViewCount(news_id))
        }
        if (viewCount.value != null)
            viewCount.value?.let {
                count = it.view_count
            }
        else
            count = 0

        return count
    }

    fun getArticleViewCount(news_id: Int): Int {
        return getArticleCount(news_id)
    }
}