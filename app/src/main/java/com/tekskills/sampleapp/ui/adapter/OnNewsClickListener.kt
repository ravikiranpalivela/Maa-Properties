package com.tekskills.sampleapp.ui.adapter

import android.view.View
import android.widget.ImageView
import com.tekskills.sampleapp.model.NewsItem

interface OnNewsClickListener {
    fun clickListener(newsItem: NewsItem, imageView: ImageView)
    fun doubleClickListener(newsItem: NewsItem?, imageView: ImageView)
    fun readMoreClickListener(newsItem: NewsItem, imageView: ImageView)
    fun shareClickListener(newsItem: NewsItem, imageView: View)
    fun likeClickListener(newsItem: NewsItem, imageView: View)
    fun commentClickListener(newsItem: NewsItem, imageView: View)

    fun voteClickListener(newsItem: NewsItem, pollOption: String)

}