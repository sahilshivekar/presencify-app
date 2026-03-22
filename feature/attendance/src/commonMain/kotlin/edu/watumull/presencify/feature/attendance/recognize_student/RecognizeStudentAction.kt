package edu.watumull.presencify.feature.attendance.recognize_student

sealed interface RecognizeStudentAction {
    data object NavigateBack : RecognizeStudentAction
}
