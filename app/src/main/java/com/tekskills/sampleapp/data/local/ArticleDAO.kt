package com.tekskills.sampleapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ArticleDAO {
    @Insert
    suspend fun insertArticle(articlesAllNews: ArticlesAllNews): Long

    @Delete
    suspend fun deleteArticle(articlesAllNews: ArticlesAllNews)

    @Query("SELECT * FROM Articles ORDER BY  bookmark_id DESC")
    fun getAllArticles(): LiveData<List<ArticlesAllNews>>

    @Query("SELECT * FROM Articles WHERE news_id = :articleId")
    fun getArticlesById(articleId: Int): ArticlesAllNews?

    @Query("DELETE FROM Articles")
    suspend fun deleteAll()

    @Query("SELECT news_id, view_count, share_count FROM Articles WHERE news_id = :id")
    suspend fun getArticleCountById(id: Int): ArticleViewCount

    @Update
    suspend fun updateArticlesCount(article: ArticlesAllNews)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertArticles(article: ArticlesAllNews)

    @Update
    suspend fun updateArticle(article: ArticlesAllNews)

    @Query("SELECT * FROM Articles WHERE news_id = :articleId")
    suspend fun getArticleById(articleId: Int): ArticlesAllNews?

    @Query("UPDATE Articles SET view_count = :count WHERE news_id = :articleId")
    fun incrementViewCount(articleId: Int, count: Int)

    @Query("UPDATE Articles SET share_count = :count WHERE news_id = :articleId")
    fun incrementShareCount(articleId: Int, count: Int)
}