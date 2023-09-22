package com.tekskills.sampleapp.ui.base


import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.model.NewsItem
import com.tekskills.sampleapp.ui.adapter.ShortsAdapter

abstract class ShortsBaseNewsAdapter<VB : ViewDataBinding>(
    private val onClickListener: ShortsAdapter.OnClickListener
) : RecyclerView.Adapter<ShortsBaseViewHolder<VB>>() {

    val articles: ArrayList<NewsItem?> = ArrayList()

    override fun getItemCount(): Int = articles.size

    fun setArticleList(articles: List<NewsItem?>) {
        this.articles.clear()
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (articles[position] != null) 1 else 2
    }

    override fun onBindViewHolder(holder: ShortsBaseViewHolder<VB>, position: Int) {
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