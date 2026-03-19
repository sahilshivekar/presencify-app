package edu.watumull.presencify.core.data.di

import com.russhwolf.settings.Settings
import edu.watumull.presencify.core.data.Constants
import edu.watumull.presencify.core.data.local.NoOpStringCipher
import edu.watumull.presencify.core.data.local.SettingsFactory
import edu.watumull.presencify.core.data.local.StringCipher
import edu.watumull.presencify.core.data.repository.auth.UserRepositoryImpl
import edu.watumull.presencify.core.data.repository.auth.TokenRepository
import edu.watumull.presencify.core.domain.repository.auth.UserRepository
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val storageModule = module {
    // Include platform-specific storage setup
    includes(platformStorageModule)

    // Named Settings for auth tokens only
    single<Settings>(named(Constants.AUTH_SETTINGS_QUALIFIER)) { get<SettingsFactory>().create(Constants.AUTH_TOKENS_NAME) }
    // Default cipher can be overridden by platform-specific modules
    single<StringCipher> { NoOpStringCipher }

    // TokenRepository for managing auth tokens
    single { TokenRepository(get(named(Constants.AUTH_SETTINGS_QUALIFIER))) }

    // RoleRepository for managing user role
    single { UserRepositoryImpl(get()) } bind UserRepository::class

    // Add local data sources here as they are implemented
}