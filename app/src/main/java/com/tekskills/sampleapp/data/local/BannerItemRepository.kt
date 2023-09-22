package com.tekskills.sampleapp.data.local

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.BannerItemItem

class BannerItemRepository(private val dao: BannerItemDao){

    val articles = dao.getAllBannerItems()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBannerItems(newsItems: List<BannerItemItem>)
    {
        dao.insertBannerItems(newsItems)
    }

    fun getBannerItem(id: Int): BannerItemItem
    {
        return dao.getBannerItem(id)
    }

    @Query("SELECT * FROM banner_items")
    fun getAllBannerItems(): List<BannerItemItem>
    {
        return getAllBannerItems()
    }
}