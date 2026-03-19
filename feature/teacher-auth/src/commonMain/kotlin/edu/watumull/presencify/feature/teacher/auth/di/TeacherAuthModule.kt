package edu.watumull.presencify.feature.teacher.auth.di

import edu.watumull.presencify.feature.teacher.auth.forgot_password.TeacherForgotPasswordViewModel
import edu.watumull.presencify.feature.teacher.auth.login.TeacherLoginViewModel
import edu.watumull.presencify.feature.teacher.auth.verify_code.TeacherVerifyCodeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val teacherAuthModule = module {
    viewModel { TeacherLoginViewModel(get()) }
    viewModel { TeacherForgotPasswordViewModel(get()) }
    viewModel { TeacherVerifyCodeViewModel(get(), get()) }
}
