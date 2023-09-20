package com.tekskills.sampleapp.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.tekskills.sampleapp.data.local.BannerItemRepository
import com.tekskills.sampleapp.data.local.BannersDatabase
import com.tekskills.sampleapp.data.local.BookmarksDatabase
import com.tekskills.sampleapp.data.local.BookmarksRepository
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
        val database: BookmarksDatabase =
            BookmarksDatabase.getInstance(context = applicationContext)
        val bannerDatabase: BannersDatabase = BannersDatabase.getInstance(context = applicationContext)

        val dao = database.dao
        val articleDao = bannerDatabase.bannerDao
        val repository = BookmarksRepository(dao)
        val bannerRepo = BannerItemRepository(articleDao)
        prefrences = AppPreferences(this)
        val factory = MainViewModelFactory(repository, bannerRepo,prefrences)
        viewModel = ViewModelProvider(getViewModelStoreOwner(), factory)[getViewModel()]
    }
}