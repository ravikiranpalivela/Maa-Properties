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

    val viewtype: Flow<String?>
    get() = datastore.data.map {
        it[KEY_VIEWTYPE]
    }

    val theme: Flow<String?>
    get() = datastore.data.map {
        it[KEY_THEME]
    }

    val language: Flow<String?>
        get() = datastore.data.map {
            it[KEY_LANGUAGE]
        }

    suspend fun saveViewType(type: String){
        datastore.edit {
            it[KEY_VIEWTYPE] = type
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

    companion object{
        private val KEY_VIEWTYPE = preferencesKey<String>("View_Type")
        private val KEY_THEME = preferencesKey<String>("User_Theme")
        private val KEY_LANGUAGE = preferencesKey<String>("User_Language")
    }
}