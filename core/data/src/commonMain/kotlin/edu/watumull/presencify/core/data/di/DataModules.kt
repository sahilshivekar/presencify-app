package edu.watumull.presencify.core.data.di

import edu.watumull.presencify.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module

val coreDataModule = module {
    // 1. Include the native engine (OkHttp/Darwin)
    includes(platformModule)

    // 2. Include the base networking (HttpClient)
    single { HttpClientFactory.create(get()) }

    // 3. Include all categorized repository/datasource modules

}

expect val platformModule: Module
