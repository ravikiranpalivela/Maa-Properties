package com.tekskills.sampleapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemId: Int, // Foreign key to link comments to list items
    val text: String
)