package com.sisl.gpsvideorecorder.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PrefDataStoreManager(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun save(key: String, value: String) {
        withContext(Dispatchers.IO) {
            dataStore
                .edit {
                    it[stringPreferencesKey(key)] = value
                }
        }
    }

    suspend fun clearAllData() {
        withContext(Dispatchers.IO) {
            dataStore
                .edit {
                    it.clear()
                }
        }
    }

    suspend fun getValue(key: String): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.map {
                it[stringPreferencesKey(key)] ?: ""
            }.first()
        }
    }

}