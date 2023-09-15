package com.tekskills.sampleapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface BookmarkDAO {
    @Insert
    suspend fun insertArticle(bookmarksAllNews: BookmarksAllNews): Long

    @Delete
    suspend fun deleteArticle(bookmarksAllNews: BookmarksAllNews)

    @Query("SELECT * FROM Bookmarks ORDER BY  bookmark_id DESC")
    fun getAllBookmarks(): LiveData<List<BookmarksAllNews>>

    @Query("DELETE FROM Bookmarks")
    suspend fun deleteAll()

}