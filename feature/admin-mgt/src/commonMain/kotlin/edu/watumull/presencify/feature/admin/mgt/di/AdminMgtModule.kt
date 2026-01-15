package edu.watumull.presencify.feature.admin.mgt.di

import edu.watumull.presencify.feature.admin.mgt.add_admin.AddAdminViewModel
import edu.watumull.presencify.feature.admin.mgt.admin_details.AdminDetailsViewModel
import edu.watumull.presencify.feature.admin.mgt.update_password.UpdatePasswordViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val adminMgtModule = module {
    viewModel { AdminDetailsViewModel(get(), get(), get()) }
    viewModel { AddAdminViewModel(get()) }
    viewModel { UpdatePasswordViewModel(get()) }
}
