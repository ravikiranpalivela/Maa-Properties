package com.tekskills.sampleapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tekskills.sampleapp.data.local.BannerItemRepository
import com.tekskills.sampleapp.data.local.BookmarksRepository
import com.tekskills.sampleapp.data.prefrences.AppPreferences
import java.lang.IllegalArgumentException

class MainViewModelFactory(private val repository: BookmarksRepository,private val bannerRepository: BannerItemRepository, private val preferences: AppPreferences): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(repository,bannerRepository,preferences) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}