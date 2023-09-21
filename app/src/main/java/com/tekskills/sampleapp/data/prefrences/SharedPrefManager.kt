package com.tekskills.sampleapp.data.prefrences

import android.content.Context

class SharedPrefManager private constructor(private val mCtx: Context) {

    val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    val bannerSelect: Int
        get() {
            return sharedPreferences.getInt("BannerSelect", 0)
        }

    fun saveBannerSelect()
    {
        var count = bannerSelect + 1
        val editor = sharedPreferences.edit()
        editor.putInt("BannerSelect", count)
        editor.apply()
    }

    fun getPrefData(key: String,defValue: String): String
    {
        return sharedPreferences.getString(key, defValue)!!
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val SHARED_PREF_NAME = "my_shared_pref"
        private var mInstance: SharedPrefManager? = null

        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }

}