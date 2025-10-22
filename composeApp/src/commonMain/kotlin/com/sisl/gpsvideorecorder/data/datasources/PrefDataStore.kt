package com.sisl.gpsvideorecorder.data.datasources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStorePref(producePath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory
        .createWithPath(
            produceFile = {
                producePath().toPath()
            }
        )

}

const val dataStoreFileName = "pref_datastore.preferences_pb"

expect fun createDataStore(context: Any?): DataStore<Preferences>