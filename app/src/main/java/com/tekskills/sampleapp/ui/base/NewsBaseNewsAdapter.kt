package com.tekskills.sampleapp.ui.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.model.AllNewsItem
import com.tekskills.sampleapp.ui.adapter.NewsAdapter

abstract class NewsBaseNewsAdapter<VB : ViewDataBinding>(
    val onClickListener : NewsAdapter.OnClickListener
) : RecyclerView.Adapter<NewsBaseViewHolder<VB>>() {

    val articles: ArrayList<AllNewsItem> = ArrayList()

    override fun getItemCount(): Int = articles.size

    fun setArticleList(articles: List<AllNewsItem>) {
        this.articles.clear()
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NewsBaseViewHolder<VB>, position: Int) {
        holder.bind(articles[position],onClickListener)
    }
}