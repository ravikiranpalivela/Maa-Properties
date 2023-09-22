package com.tekskills.sampleapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tekskills.sampleapp.model.BannerItemItem

@Dao
interface BannerItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBannerItems(newsItems: List<BannerItemItem>)

    @Query("SELECT * FROM banner_items WHERE id = :id")
    fun getBannerItem(id: Int): BannerItemItem

    @Query("SELECT * FROM banner_items")
    fun getAllBannerItems(): List<BannerItemItem>
}