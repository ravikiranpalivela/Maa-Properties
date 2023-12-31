package com.tekskills.sampleapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tekskills.sampleapp.data.prefrences.AppPreferences
import java.lang.IllegalArgumentException

class MainViewModelFactory(private val preferences: AppPreferences): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(preferences) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}