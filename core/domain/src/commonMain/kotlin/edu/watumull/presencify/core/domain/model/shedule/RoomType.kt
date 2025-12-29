package edu.watumull.presencify.core.domain.model.shedule

enum class RoomType {
    CLASSROOM,
    LAB,
    OFFICE;

    companion object {
        fun fromSerialized(value: String?): RoomType? = when (value?.lowercase()) {
            "classroom" -> CLASSROOM
            "lab" -> LAB
            "office" -> OFFICE
            else -> null
        }
    }

    fun toSerialized(): String = when (this) {
        CLASSROOM -> "Classroom"
        LAB -> "Lab"
        OFFICE -> "Office"
    }
}
