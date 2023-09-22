package com.tekskills.sampleapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tekskills.sampleapp.model.BannerItemItem

@Database(entities = [ArticlesAllNews::class,CommentItem::class, BannerItemItem::class], version = 1, exportSchema = false)
abstract class ArticlesDatabase :RoomDatabase(){
    abstract val dao: ArticleDAO
    abstract val commentDao: CommentDao
    abstract val bannerDao: BannerItemDao

    companion object{
        @Volatile
        private var INSTANCE: ArticlesDatabase? = null

        fun getInstance(context: Context): ArticlesDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ArticlesDatabase::class.java,
                        "Sample_Application"
                    ).allowMainThreadQueries().build()

                }
                    return instance

            }

        }
    }
}