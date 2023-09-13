package com.tekskills.sampleapp.ui.base

import android.view.View
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.model.AllNewsItem

abstract class ShortsBaseNewsAdapter<VB : ViewDataBinding>(
    private val clickListener: (AllNewsItem, ImageView) -> Unit,
    private val doubleClickListener: (AllNewsItem, ImageView) -> Unit,
    private val longClickListener: (AllNewsItem, ImageView) -> Unit,
    private val shareClickListener: (AllNewsItem, View) -> Unit
) : RecyclerView.Adapter<ShortsBaseViewHolder<VB>>() {

    val articles: ArrayList<AllNewsItem> = ArrayList()

    override fun getItemCount(): Int = articles.size

    fun setArticleList(articles: List<AllNewsItem>) {
        this.articles.clear()
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ShortsBaseViewHolder<VB>, position: Int) {
        holder.bind(articles[position], clickListener,doubleClickListener, longClickListener,shareClickListener)
    }
}