package edu.watumull.presencify.core.domain.model.academics

data class University(
    val id: String,
    val name: String,
    val abbreviation: String,
    val schemes: List<Scheme>? = null
)
