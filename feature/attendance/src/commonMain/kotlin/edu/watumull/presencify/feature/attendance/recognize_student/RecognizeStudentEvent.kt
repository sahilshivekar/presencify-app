package edu.watumull.presencify.feature.attendance.recognize_student



sealed interface RecognizeStudentEvent {
    data object NavigateBack : RecognizeStudentEvent
    data object MapsToSuccess : RecognizeStudentEvent
}
