package edu.watumull.presencify.core.data.di

import com.russhwolf.settings.Settings
import edu.watumull.presencify.core.data.Constants
import edu.watumull.presencify.core.data.local.NoOpStringCipher
import edu.watumull.presencify.core.data.local.SettingsFactory
import edu.watumull.presencify.core.data.local.StringCipher
import edu.watumull.presencify.core.data.repository.auth.TokenRepository
import edu.watumull.presencify.core.data.repository.auth.UserRepositoryImpl
import edu.watumull.presencify.core.data.repository.student.FCMTokenLocalRepository
import edu.watumull.presencify.core.domain.repository.auth.UserRepository
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val storageModule = module {
    includes(platformStorageModule)

    single<Settings>(named(Constants.AUTH_SETTINGS_QUALIFIER)) { get<SettingsFactory>().create(Constants.AUTH_TOKENS_NAME) }
    single<Settings>(named(Constants.FCM_SETTINGS_QUALIFIER)) { get<SettingsFactory>().create(Constants.FCM_SETTINGS_NAME) }
    single<StringCipher> { NoOpStringCipher }

    single { TokenRepository(get(named(Constants.AUTH_SETTINGS_QUALIFIER))) }

    single { FCMTokenLocalRepository(get(named(Constants.FCM_SETTINGS_QUALIFIER))) }

    single { UserRepositoryImpl(get()) } bind UserRepository::class

}