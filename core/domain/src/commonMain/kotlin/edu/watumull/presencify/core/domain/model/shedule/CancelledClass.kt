package edu.watumull.presencify.core.domain.model.shedule

data class CancelledClass(
    val id: String,
    val classId: String,
    val cancelDate: String,
    val reason: String?,
    val klass: ClassSession?
)