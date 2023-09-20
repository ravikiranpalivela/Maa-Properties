package com.tekskills.sampleapp.data.local

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookmarkDAO {
    @Insert
    suspend fun insertArticle(bookmarksAllNews: BookmarksAllNews): Long

    @Delete
    suspend fun deleteArticle(bookmarksAllNews: BookmarksAllNews)

    @Query("SELECT * FROM Bookmarks ORDER BY  bookmark_id DESC")
    fun getAllBookmarks(): LiveData<List<BookmarksAllNews>>

    @Query("SELECT * FROM bookmarks WHERE news_id = :articleId")
    fun getBookmarksById(articleId: Int): BookmarksAllNews?


    @Query("DELETE FROM Bookmarks")
    suspend fun deleteAll()

    @Query("SELECT news_id, view_count FROM Bookmarks WHERE news_id = :id")
    suspend fun getBookmarkCountById(id: Int): BookMarkViewCount

    @Update
    suspend fun updateBookmarksCount(article: BookmarksAllNews)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(article: BookmarksAllNews)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBookmarks(article: BookmarksAllNews)

    @Update
    suspend fun updateBookmark(article: BookmarksAllNews)

    @Query("SELECT * FROM bookmarks WHERE news_id = :articleId")
    suspend fun getBookmarkById(articleId: Int): BookmarksAllNews?

    @Query("UPDATE bookmarks SET view_count = :count WHERE news_id = :articleId")
    fun incrementViewCount(articleId: Int, count: Int)
}