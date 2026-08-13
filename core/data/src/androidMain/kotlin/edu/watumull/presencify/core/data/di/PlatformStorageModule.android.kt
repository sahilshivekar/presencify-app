package edu.watumull.presencify.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import edu.watumull.presencify.core.data.Constants
import edu.watumull.presencify.core.data.local.DataStoreFactory
import edu.watumull.presencify.core.data.local.FCMTokenProvider
import edu.watumull.presencify.core.data.local.PlatformContext
import edu.watumull.presencify.core.data.local.SettingsFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformStorageModule = module {
    single { SettingsFactory(PlatformContext(androidContext())) }
    single<DataStore<Preferences>> {
        DataStoreFactory.create { androidContext().filesDir.resolve(Constants.DATASTORE_FILENAME).absolutePath }
    }
    single { FCMTokenProvider(PlatformContext(androidContext())) }
}
