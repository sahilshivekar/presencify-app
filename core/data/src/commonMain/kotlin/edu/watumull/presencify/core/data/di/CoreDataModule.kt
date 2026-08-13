package edu.watumull.presencify.core.data.di

import org.koin.dsl.module

val coreDataModule = module {
    includes(storageModule)
    includes(networkModule)
    includes(repositoryModule)
    includes(clockModule)
}