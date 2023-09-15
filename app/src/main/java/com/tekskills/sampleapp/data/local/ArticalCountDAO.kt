package com.tekskills.sampleapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface ArticalCountDAO {
    @Insert
    suspend fun insertArticleCount(articalCount: ArticalCount): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdateArticleCount(articalCount: ArticalCount): Long

    @Query("SELECT * FROM ArticalCount WHERE artical_id = :id")
    suspend fun getArticleCountById(id: String): ArticalCount?

    @Delete
    suspend fun deleteArticle(articalCount: ArticalCount)

    @Query("SELECT * FROM ArticalCount ORDER BY  artical_id DESC")
    fun getAllArticalCount(): LiveData<List<ArticalCount>>

    @Query("DELETE FROM ArticalCount")
    suspend fun deleteAll()

    @Update
    suspend fun updateArticleCount(article: ArticalCount)

}