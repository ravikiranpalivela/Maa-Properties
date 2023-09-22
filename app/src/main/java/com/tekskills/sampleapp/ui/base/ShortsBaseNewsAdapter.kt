package com.tekskills.sampleapp.ui.base


import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.model.NewsItem
import com.tekskills.sampleapp.ui.adapter.ShortsAdapter

abstract class ShortsBaseNewsAdapter<VB : ViewDataBinding>(
    private val onClickListener: ShortsAdapter.OnClickListener
) : RecyclerView.Adapter<ShortsBaseViewHolder<VB>>() {

    val articles: ArrayList<NewsItem> = ArrayList()

    override fun getItemCount(): Int = articles.size

    fun setArticleList(articles: List<NewsItem>) {
        this.articles.clear()
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ShortsBaseViewHolder<VB>, position: Int) {
        holder.bind(
            articles[position],
            onClickListener,
        )
    }
}