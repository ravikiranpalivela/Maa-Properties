package com.tekskills.sampleapp.ui.base

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.model.NewsItem
import com.tekskills.sampleapp.ui.adapter.NewsAdapter

abstract class NewsBaseNewsAdapter<VB : ViewDataBinding>(
    val onClickListener: NewsAdapter.OnClickListener
) : RecyclerView.Adapter<NewsBaseViewHolder<VB>>() {

    val articles: ArrayList<NewsItem?> = ArrayList()

    override fun getItemCount(): Int = articles.size

    fun setArticleList(articles: List<NewsItem?>) {
        this.articles.clear()
        this.articles.addAll(articles)
        Log.d("TAG","articles of ${articles}")
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (articles[position] != null) 1 else 2
    }

    override fun onBindViewHolder(holder: NewsBaseViewHolder<VB>, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                if (articles[position] != null)
                    holder.bind(articles[position]!!, onClickListener)
            }

            2 -> {
                holder.bindAdView(onClickListener)
            }

            else -> {
                if (articles[position] != null)
                    holder.bind(articles[position]!!, onClickListener)
                else
                    holder.bindAdView(onClickListener)
            }
        }
    }
}