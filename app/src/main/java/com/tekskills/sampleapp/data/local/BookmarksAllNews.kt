package com.tekskills.sampleapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tekskills.sampleapp.model.AllNewsItem

@Entity(tableName = "Bookmarks")
data class BookmarksAllNews(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bookmark_id")
    val id: Int,
    @ColumnInfo(name = "news_id")
    val news_id: Int,
    @Embedded val article: AllNewsItem?
)