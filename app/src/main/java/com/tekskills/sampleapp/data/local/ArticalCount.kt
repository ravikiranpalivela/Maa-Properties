package com.tekskills.sampleapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tekskills.sampleapp.model.AllNewsItem

@Entity(tableName = "ArticalCount")
data class ArticalCount(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "artical_id")
    val id: Int,
    @Embedded val article: AllNewsItem?,
    var viewCount: Int = 0
)