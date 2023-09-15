package com.tekskills.sampleapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookmarksAllNews::class], version = 2, exportSchema = false)
abstract class BookmarksDatabase :RoomDatabase(){

    abstract val dao: BookmarkDAO

    companion object{
        @Volatile
        private var INSTANCE: BookmarksDatabase? = null

        fun getInstance(context: Context): BookmarksDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BookmarksDatabase::class.java,
                        "Bookmarks"
                    ).build()

                }
                    return instance

            }

        }
    }
}