package com.tekskills.sampleapp.ui.base

import android.view.View
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.model.NewsItem

abstract class BaseNewsAdapter<VB : ViewDataBinding>(
    private val clickListener: (NewsItem, ImageView) -> Unit,
    private val longClickListener: (NewsItem, ImageView) -> Unit,
    private val shareClickListener: (NewsItem, View) -> Unit
) : RecyclerView.Adapter<BaseViewHolder<VB>>() {

    val articles: ArrayList<NewsItem> = ArrayList()

    override fun getItemCount(): Int = articles.size

    fun setArticleList(articles: List<NewsItem>) {
        this.articles.clear()
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        holder.bind(articles[position], clickListener, longClickListener,shareClickListener)
    }
}