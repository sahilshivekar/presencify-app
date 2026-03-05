package edu.watumull.presencify.core.domain.enums

import edu.watumull.presencify.core.domain.DisplayLabelProvider
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = SemesterNumberSerializer::class)
enum class SemesterNumber(val value: Int) : DisplayLabelProvider {
    SEMESTER_1(1),
    SEMESTER_2(2),
    SEMESTER_3(3),
    SEMESTER_4(4),
    SEMESTER_5(5),
    SEMESTER_6(6),
    SEMESTER_7(7),
    SEMESTER_8(8);

    override fun toDisplayLabel(): String = "Semester $value"

    fun toAcademicYear(): String {
        return when (value) {
            1, 2 -> "FE"
            3, 4 -> "SE"
            5, 6 -> "TE"
            7, 8 -> "BE"
            else -> "N/A"
        }
    }

    companion object {
        fun fromValue(value: Int): SemesterNumber? = entries.find { it.value == value }

        /**
         * Check if we are currently in even semester period (Jan-June).
         * - Jan-June: Even semesters (2,4,6,8) are active
         * - July-Dec: Odd semesters (1,3,5,7) are active
         */
        fun isEvenSemesterPeriod(): Boolean {
            val now = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            return now.monthNumber in 1..6
        }

        /**
         * Check if we are currently in odd semester period (July-Dec).
         * - Jan-June: Even semesters (2,4,6,8) are active
         * - July-Dec: Odd semesters (1,3,5,7) are active
         */
        fun isOddSemesterPeriod(): Boolean = !isEvenSemesterPeriod()

        /**
         * Get the current academic start year based on the current date.
         * Academic year: July-June cycle
         * - Jan-June: Academic year started previous year (e.g., 2025-2026)
         * - July-Dec: Academic year starts this year (e.g., 2026-2027)
         */
        fun getCurrentAcademicStartYear(): Int {
            val now = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            val currentMonth = now.monthNumber
            val currentYear = now.year

            // Jan-June: Even semesters (2,4,6,8) are active, academic year started previous year
            // July-Dec: Odd semesters (1,3,5,7) are active, academic year starts this year
            return if (currentMonth in 1..6) currentYear - 1 else currentYear
        }

        /**
         * Get the current academic end year based on the current date.
         * Academic year: July-June cycle
         * - Jan-June: Academic year ends this year (e.g., 2025-2026)
         * - July-Dec: Academic year ends next year (e.g., 2026-2027)
         */
        fun getCurrentAcademicEndYear(): Int {
            val now = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            val currentMonth = now.monthNumber
            val currentYear = now.year

            // Jan-June: Even semesters (2,4,6,8) are active, academic year ends this year
            // July-Dec: Odd semesters (1,3,5,7) are active, academic year ends next year
            return if (currentMonth in 1..6) currentYear else currentYear + 1
        }
    }
}

object SemesterNumberSerializer : KSerializer<SemesterNumber> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SemesterNumber", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: SemesterNumber) {
        encoder.encodeInt(value.value)
    }

    override fun deserialize(decoder: Decoder): SemesterNumber {
        val value = decoder.decodeInt()
        return SemesterNumber.fromValue(value) ?: throw SerializationException("Unknown semester number: $value")
    }
}