package com.tekskills.sampleapp

import android.app.Application

class OnlyNewsApp: Application(){


    companion object {
        const val API_KEY = "ab4cead390ad4e3fab91aefd5473fdc3"
    }

//    override fun onCreate() {
//        super.onCreate()
//
//        initExoPlayerCaching()
//    }
//
//    private fun initExoPlayerCaching() {
//
//        val exoPlayerCaching = ExoPlayerCaching()
//        exoPlayerCaching.init(this, StorageUtil.getExoCacheDir(this))
//
//    }

}