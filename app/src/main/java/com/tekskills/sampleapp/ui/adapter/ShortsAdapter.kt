package com.tekskills.sampleapp.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.model.AllNewsItem
import com.tekskills.sampleapp.databinding.ItemArticleViewtypeListBinding
import com.tekskills.sampleapp.ui.base.ShortsBaseNewsAdapter
import com.tekskills.sampleapp.ui.base.ShortsBaseViewHolder

class ShortsAdapter(
    private val activity: Activity,
    private val lifecycle: Lifecycle,
    clickListener: (AllNewsItem, ImageView) -> Unit,
    doubleClickListener: (AllNewsItem, ImageView) -> Unit,
    longClickListener: (AllNewsItem, ImageView) -> Unit,
    shareClickListener: (AllNewsItem, View) -> Unit,
) : ShortsBaseNewsAdapter<ItemArticleViewtypeListBinding>(
    clickListener,
    doubleClickListener,
    longClickListener,
    shareClickListener
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemArticleViewtypeListBinding>(
            inflater,
            R.layout.item_article_viewtype_list,
            parent,
            false
        )
        return ShortsListViewHolder(activity, lifecycle, binding)
    }
}

class ShortsListViewHolder(
    activity: Activity,
    lifecycle: Lifecycle,
    private val bindingData: ItemArticleViewtypeListBinding
) :
    ShortsBaseViewHolder<ItemArticleViewtypeListBinding>(activity, bindingData, lifecycle) {

    override val binding: ItemArticleViewtypeListBinding
        get() = bindingData

}





