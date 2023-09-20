package com.tekskills.sampleapp.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.local.BookmarksDatabase
import com.tekskills.sampleapp.data.local.BookmarksRepository
import com.tekskills.sampleapp.databinding.ItemArticleViewtypeListBinding
import com.tekskills.sampleapp.model.AllNewsItem
import com.tekskills.sampleapp.ui.base.NewsBaseNewsAdapter
import com.tekskills.sampleapp.ui.base.NewsBaseViewHolder
import com.tekskills.sampleapp.ui.main.MainViewModel

class NewsAdapter(
    private val activity: Activity,
    private val viewModel: MainViewModel,
    private val lifecycle: Lifecycle,
    onClickListener: OnClickListener,
) : NewsBaseNewsAdapter<ItemArticleViewtypeListBinding>(onClickListener) {

    interface OnClickListener {
        fun clickListener(allNewsItem: AllNewsItem, imageView: ImageView)
        fun doubleClickListener(allNewsItem: AllNewsItem, imageView: ImageView)
        fun readMoreClickListener(allNewsItem: AllNewsItem, imageView: ImageView)
        fun shareClickListener(allNewsItem: AllNewsItem, imageView: View)
        fun likeClickListener(allNewsItem: AllNewsItem, imageView: View)
        fun commentClickListener(allNewsItem: AllNewsItem, imageView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemArticleViewtypeListBinding>(
            inflater,
            R.layout.item_article_viewtype_list,
            parent,
            false
        )
        return NewsListViewHolder(activity, viewModel, lifecycle, binding)
    }
}

class NewsListViewHolder(
    activity: Activity,
    viewModel: MainViewModel,
    lifecycle: Lifecycle,
    private val bindingData: ItemArticleViewtypeListBinding
) :
    NewsBaseViewHolder<ItemArticleViewtypeListBinding>(activity,viewModel, bindingData, lifecycle) {
    override val binding: ItemArticleViewtypeListBinding
        get() = bindingData
}





