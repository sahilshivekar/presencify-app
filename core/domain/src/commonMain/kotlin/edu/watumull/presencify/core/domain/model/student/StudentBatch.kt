package edu.watumull.presencify.core.domain.model.student

import edu.watumull.presencify.core.domain.model.academics.Batch

data class StudentBatch(
    val id: String,
    val studentId: String,
    val batchId: String,
    val fromDate: String,
    val toDate: String?,
    val student: Student? = null,
    val batch: Batch? = null
)
