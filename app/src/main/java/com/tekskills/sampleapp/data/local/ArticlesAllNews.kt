package com.tekskills.sampleapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Articles")
data class ArticlesAllNews(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bookmark_id")
    val id: Int = 0,
    @ColumnInfo(name = "news_id")
    val news_id: Int,
    @ColumnInfo(name = "view_count")
    var view_count: Int = 0,
    @ColumnInfo(name = "share_count")
    var share_count: Int = 0
)