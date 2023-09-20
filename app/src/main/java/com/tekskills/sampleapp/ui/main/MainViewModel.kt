package com.tekskills.sampleapp.ui.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.local.BannerItemRepository
import com.tekskills.sampleapp.data.local.BookMarkViewCount
import com.tekskills.sampleapp.data.local.BookmarksAllNews
import com.tekskills.sampleapp.data.local.BookmarksRepository
import com.tekskills.sampleapp.data.prefrences.AppPreferences
import com.tekskills.sampleapp.data.repo.ArticleProvider
import com.tekskills.sampleapp.model.AllNews
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.BannerItemItem
import com.tekskills.sampleapp.model.PosterItem
import com.tekskills.sampleapp.utils.Event
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class MainViewModel(
    private val repository: BookmarksRepository,
    private val bannerRepo: BannerItemRepository,
    private val prefrences: AppPreferences
) : ViewModel(), Observable {
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    @Bindable
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    val category: MutableLiveData<String> = MutableLiveData("Top Headlines")

    private val message: MutableLiveData<Event<String>> = MutableLiveData()
    val errorMessage get() = message

    var responseLiveData: MutableLiveData<Response<AllNews>?> = MutableLiveData()
    var responseEditorLiveData: MutableLiveData<Response<PosterItem>?> = MutableLiveData()
    var responseBannerLiveData: MutableLiveData<Response<BannerItem>?> = MutableLiveData()

    var bookmarkList: LiveData<List<BookmarksAllNews>> = repository.bookmarks

    val _viewCount: MutableLiveData<BookMarkViewCount> = MutableLiveData<BookMarkViewCount>()
    val viewCount: LiveData<BookMarkViewCount> get() = _viewCount

    val appPreferences: AppPreferences
        get() = prefrences

    fun refreshResponse() {

        viewModelScope.launch {
            isLoading.value = true
            try {
                when (category.value!!) {
                    "All", "News", "Wishes" -> {
                        getAllNews()
                    }

                    "Posters" -> {
                        getPosterDetails()
                    }

                    else -> {
                        getAllNews()
                    }
                }


//                val response = ArticleProvider().getNews(category.value!!)
//                val posterResponse = ArticleProvider().getPosterNews(category.value!!)
//                if (response.isSuccessful) {
//                    responseLiveData.postValue(response)
//                }
//                else {
//                    responseLiveData.postValue(null)
//                    message.value = Event(response.errorBody().toString())
//                }
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

    private suspend fun getAllNews() {
        val response = ArticleProvider().getNews(category.value!!)
        if (response.isSuccessful) {
            responseLiveData.postValue(response)
        } else {
            responseLiveData.postValue(null)
            message.value = Event(response.errorBody().toString())
        }
    }

    private suspend fun getPosterDetails() {
        val response = ArticleProvider().getPoster(category.value!!)
        if (response.isSuccessful) {
            responseEditorLiveData.postValue(response)
        } else {
            responseEditorLiveData.postValue(null)
            message.value = Event(response.errorBody().toString())
        }
    }

    private suspend fun getBannerDetails() {
        val response = ArticleProvider().getBanner()
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

    fun addABookmark(id: Int = 0, news_id: Int, viewCount: Int) {
        viewModelScope.launch {
            repository.insertOrUpdateBookmark(id, news_id, viewCount)
        }
    }

    fun addBanners(article: List<BannerItemItem>) {
        viewModelScope.launch {
            bannerRepo.insertBannerItems(article)
        }
    }

    fun getBanners(): List<BannerItemItem> {
        return bannerRepo.getAllBannerItems()
    }

    fun deleteABookmark(bookmark: BookmarksAllNews) {
        viewModelScope.launch {
            repository.deleteArticleFromBookmarks(bookmark)
        }
    }

    fun clearAllBookmarks() {
        viewModelScope.launch {
            repository.deleteALlArticleFromBookmarks()
        }
    }

    fun getBookMarkCount(news_id: Int): Int {
        var count = 0
        viewModelScope.launch {
            _viewCount.postValue(repository.getBookMarkViewCount(news_id))
        }
        if (viewCount.value != null)
            viewCount.value?.let {
                count = it.view_count
            }
        else
            count = 0

        return count
    }

    fun getBookMarkViewCount(news_id: Int): Int {
        return getBookMarkCount(news_id)
    }
}