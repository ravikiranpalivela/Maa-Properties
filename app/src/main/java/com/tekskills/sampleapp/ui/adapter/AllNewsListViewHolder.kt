package com.tekskills.sampleapp.ui.adapter

import android.app.Activity
import androidx.lifecycle.Lifecycle
import com.tekskills.sampleapp.databinding.ItemArticleViewtypeListBinding
import com.tekskills.sampleapp.ui.base.AllNewsBaseViewHolder
import com.tekskills.sampleapp.ui.base.NewsBaseViewHolder
import com.tekskills.sampleapp.ui.main.MainViewModel

class AllNewsListViewHolder(
    activity: Activity,
    viewModel: MainViewModel,
    lifecycle: Lifecycle,
    private val bindingData: ItemArticleViewtypeListBinding
) :
    AllNewsBaseViewHolder<ItemArticleViewtypeListBinding>(
        activity,
        viewModel,
        bindingData,
        lifecycle
    ) {
    override val binding: ItemArticleViewtypeListBinding
        get() = bindingData
}