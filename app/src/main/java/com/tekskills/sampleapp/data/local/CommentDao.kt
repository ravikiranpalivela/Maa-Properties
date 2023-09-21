package com.tekskills.sampleapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CommentDao {
    @Insert
    fun insertComment(comment: CommentItem)

    @Query("SELECT * FROM comments WHERE itemId = :itemId")
    fun getCommentsForItem(itemId: Int): List<CommentItem>
}