package com.tekskills.sampleapp.ui.adapter

import android.view.View
import android.widget.ImageView
import com.tekskills.sampleapp.model.AllNewsItem

interface OnAllNewsClickListener {
    fun clickListener(newsItem: AllNewsItem, imageView: ImageView)
    fun doubleClickListener(newsItem: AllNewsItem?, imageView: ImageView)
    fun readMoreClickListener(newsItem: AllNewsItem, imageView: ImageView)
    fun shareClickListener(newsItem: AllNewsItem, imageView: View)
    fun likeClickListener(newsItem: AllNewsItem, imageView: View)
    fun commentClickListener(newsItem: AllNewsItem, imageView: View)
}