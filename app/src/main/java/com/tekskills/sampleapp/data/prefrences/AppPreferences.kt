package com.tekskills.sampleapp.data.prefrences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences (
    context: Context
){

    private val applicationContext = context.applicationContext
    private val datastore: DataStore<Preferences> = applicationContext.createDataStore(
        name = "com.tekskills.sampleapp"
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

    suspend fun saveViewType(type: String){
        datastore.edit {
            it[KEY_VIEW_TYPE] = type
        }
    }

    suspend fun saveLanguage(type: String){
        datastore.edit {
            it[KEY_LANGUAGE] = type
        }
    }

    suspend fun saveUserTheme(theme: String){
        datastore.edit {
            it[KEY_THEME] = theme
        }
    }

    suspend fun saveBannerEdit(count: Int)
    {
        datastore.edit {
            it[KEY_BANNER] = count
        }
    }

    companion object{
        private val KEY_VIEW_TYPE = preferencesKey<String>("View_Type")
        private val KEY_THEME = preferencesKey<String>("User_Theme")
        private val KEY_LANGUAGE = preferencesKey<String>("User_Language")
        private val KEY_BANNER = preferencesKey<Int>("Banner_Select")

    }
}