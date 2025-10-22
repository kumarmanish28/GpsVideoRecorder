package com.sisl.gpsvideorecorder.data.datasources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(context: Any?): DataStore<Preferences> {

    return createDataStorePref {
        val documentDir = NSFileManager
            .defaultManager
            .URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                error = null,
                create = false
            )
        requireNotNull(documentDir).path.plus("/").plus(dataStoreFileName)
    }

}