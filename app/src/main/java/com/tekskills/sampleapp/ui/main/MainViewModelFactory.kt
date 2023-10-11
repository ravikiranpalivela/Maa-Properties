package com.tekskills.sampleapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tekskills.sampleapp.data.prefrences.SharedPrefManager
import com.tekskills.sampleapp.data.repo.ArticleProviderRepo
import java.lang.IllegalArgumentException

class MainViewModelFactory(private val repository: ArticleProviderRepo, private val preferences: SharedPrefManager): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(repository,preferences) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}