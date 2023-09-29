package com.tekskills.sampleapp.ui.base

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.model.AllNewsDetails
import com.tekskills.sampleapp.model.NewsItem
import com.tekskills.sampleapp.ui.adapter.AllNewsDetailsAdapter
import com.tekskills.sampleapp.ui.adapter.OnAllNewsClickListener
import com.tekskills.sampleapp.ui.adapter.OnNewsClickListener

abstract class AllNewsBaseNewsAdapter<VB : ViewDataBinding>(
    val onClickListener: OnAllNewsClickListener
) : RecyclerView.Adapter<AllNewsBaseViewHolder<VB>>() {

    val articles: AllNewsDetails = AllNewsDetails()

    override fun getItemCount(): Int = articles.size

    fun setArticleList(articles: AllNewsDetails) {
        this.articles.clear()
        this.articles.addAll(articles)
        Log.d("TAG","articles of ${articles}")
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (articles[position] != null) 1 else 2
    }

    override fun onBindViewHolder(holder: AllNewsBaseViewHolder<VB>, position: Int) {
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