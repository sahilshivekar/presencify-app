package edu.watumull.presencify.core.domain.enums

import edu.watumull.presencify.core.domain.DisplayLabelProvider
import kotlinx.serialization.SerialName

enum class CourseType(val value: String) : DisplayLabelProvider {
    @SerialName("Lecture")
    LECTURE("Lecture"),
    @SerialName("Practical")
    PRACTICAL("Practical");

    override fun toDisplayLabel(): String = value

    companion object {
        fun fromValue(value: String): CourseType? = entries.find { it.value == value }
    }
}
