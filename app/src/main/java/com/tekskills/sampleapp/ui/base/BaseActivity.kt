package com.tekskills.sampleapp.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.tekskills.sampleapp.data.prefrences.AppPreferences
import com.tekskills.sampleapp.ui.main.MainViewModelFactory

abstract class BaseActivity<binding : ViewDataBinding, viewModel : ViewModel, viewModelstoreOwner : ViewModelStoreOwner> :
    AppCompatActivity() {

    abstract fun getViewBinding(): binding
    abstract fun getViewModel(): Class<viewModel>
    abstract fun getViewModelStoreOwner(): viewModelstoreOwner
    abstract fun getContext(): Context

    protected lateinit var binding: binding

    protected lateinit var viewModel: viewModel

    protected lateinit var prefrences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()

        prefrences = AppPreferences(this)

        val factory = MainViewModelFactory(prefrences)
        viewModel = ViewModelProvider(getViewModelStoreOwner(), factory)[getViewModel()]
    }
}