package edu.watumull.presencify.feature.student.auth.di

import edu.watumull.presencify.feature.student.auth.forgot_password.StudentForgotPasswordViewModel
import edu.watumull.presencify.feature.student.auth.login.StudentLoginViewModel
import edu.watumull.presencify.feature.student.auth.verify_code.StudentVerifyCodeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val studentAuthModule = module {
    viewModel { StudentLoginViewModel(get()) }
    viewModel { StudentForgotPasswordViewModel(get()) }
    viewModel { StudentVerifyCodeViewModel(get(), get()) }
}
