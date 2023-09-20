package com.tekskills.sampleapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tekskills.sampleapp.model.BannerItemItem

@Database(entities = [BannerItemItem::class], version = 1, exportSchema = false)
abstract class BannersDatabase :RoomDatabase(){

    abstract val bannerDao: BannerItemDao

    companion object{
        @Volatile
        private var INSTANCE: BannersDatabase? = null

        fun getInstance(context: Context): BannersDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BannersDatabase::class.java,
                        "banner_items"
                    ).allowMainThreadQueries().build()

                }
                    return instance

            }

        }
    }
}