package com.tekskills.sampleapp.data.prefrences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.google.gson.Gson
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.PublicAdsDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharedPrefManager private constructor(private val mCtx: Context) {

    private val sharedPreferences: SharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private val datastore: DataStore<Preferences> = mCtx.createDataStore(
        name = SHARED_PREF_NAME
    )

    val view_type: Flow<String?>
        get() = datastore.data.map {
            it[KEY_VIEW_TYPE]
        }

    val theme: Flow<String?>
        get() = datastore.data.map {
            it[KEY_THEME]
        }

    val language: Flow<String?>
        get() = datastore.data.map {
            it[KEY_LANGUAGE]
        }

    val bannerCount: Flow<Int?>
        get() = datastore.data.map {
            it[KEY_BANNER]
        }

    suspend fun saveViewType(type: String) {
        datastore.edit {
            it[KEY_VIEW_TYPE] = type
        }
    }

    suspend fun saveLanguage(type: String) {
        datastore.edit {
            it[KEY_LANGUAGE] = type
        }
    }

    suspend fun saveUserTheme(theme: String) {
        datastore.edit {
            it[KEY_THEME] = theme
        }
    }

    suspend fun saveBannerEdit(count: Int) {
        datastore.edit {
            it[KEY_BANNER] = count
        }
    }

    val bannerSelect: Int
        get() {
            return sharedPreferences.getInt("BannerSelect", 0)
        }

    fun saveBannerSelect() {
        var count = bannerSelect + 1
        val editor = sharedPreferences.edit()
        editor.putInt("BannerSelect", count)
        editor.apply()
    }

    fun saveBannerData(jsonString: String) {
        sharedPreferences.edit().putString("BannerData", jsonString).apply()
    }

    @SuppressLint("HardwareIds")
    fun getDeviceID():String {
        return Settings.Secure.getString(mCtx.contentResolver, Settings.Secure.ANDROID_ID)
    }

    // Retrieve the data from SharedPreferences
    fun getBannerData(): String? {
        return sharedPreferences.getString("BannerData", null)
    }

    fun getBannerDetailsData(): BannerItem? {
        val gson = Gson()
        val jsonString = sharedPreferences.getString("BannerData", null)
        return gson.fromJson(jsonString, BannerItem::class.java)
    }

    fun setBannerSelectValue() {
        var count = 0
        val editor = sharedPreferences.edit()
        editor.putInt("BannerSelect", count)
        editor.apply()
    }

    val publicAdsAdsSelect: Int
        get() {
            return sharedPreferences.getInt("PublicAdsAdsSelect", 0)
        }

    fun getPublicAdsData(): String? {
        return sharedPreferences.getString("PublicAdsData", null)
    }

    fun getPublicAdsDetailsData(): PublicAdsDetails? {
        val gson = Gson()
        val jsonString = sharedPreferences.getString("PublicAdsData", null)
        return gson.fromJson(jsonString, PublicAdsDetails::class.java)
    }

    fun setPublicAdsSelectValue() {
        var count = 0
        val editor = sharedPreferences.edit()
        editor.putInt("PublicAdsAdsSelect", count)
        editor.apply()
    }

    fun savePublicAdsAdsSelect() {
        var count = publicAdsAdsSelect + 1
        val editor = sharedPreferences.edit()
        editor.putInt("PublicAdsAdsSelect", count)
        editor.apply()
    }

    fun getPrefData(key: String, defValue: String): String {
        return sharedPreferences.getString(key, defValue)!!
    }

    fun savePublicAdsData(jsonString: String) {
        sharedPreferences.edit().putString("PublicAdsData", jsonString).apply()
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val SHARED_PREF_NAME = "my_shared_pref"
        private var mInstance: SharedPrefManager? = null
        private val KEY_VIEW_TYPE = preferencesKey<String>("View_Type")
        private val KEY_THEME = preferencesKey<String>("User_Theme")
        private val KEY_LANGUAGE = preferencesKey<String>("User_Language")
        private val KEY_BANNER = preferencesKey<Int>("Banner_Select")

        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }

}