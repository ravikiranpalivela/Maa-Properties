package com.tekskills.sampleapp.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.databinding.ItemArticleViewtypeListBinding
import com.tekskills.sampleapp.ui.base.AllNewsBaseNewsAdapter
import com.tekskills.sampleapp.ui.base.NewsBaseNewsAdapter
import com.tekskills.sampleapp.ui.main.MainViewModel

class AllNewsDetailsAdapter(
    private val activity: Activity,
    private val viewModel: MainViewModel,
    private val lifecycle: Lifecycle,
    onClickListener: OnAllNewsClickListener,
) : AllNewsBaseNewsAdapter<ItemArticleViewtypeListBinding>(onClickListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemArticleViewtypeListBinding>(
            inflater,
            R.layout.item_article_viewtype_list,
            parent,
            false
        )
        return AllNewsListViewHolder(activity, viewModel, lifecycle, binding)
    }
}






