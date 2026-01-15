package edu.watumull.presencify.feature.admin.auth.di

import edu.watumull.presencify.feature.admin.auth.forgot_password.AdminForgotPasswordViewModel
import edu.watumull.presencify.feature.admin.auth.login.AdminLoginViewModel
import edu.watumull.presencify.feature.admin.auth.verify_code.AdminVerifyCodeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val adminAuthModule = module {
    viewModel { AdminLoginViewModel(get()) }
    viewModel { AdminForgotPasswordViewModel(get()) }
    viewModel { AdminVerifyCodeViewModel(get(), get()) }
}
