package edu.watumull.presencify.core.domain.enums

import edu.watumull.presencify.core.domain.DisplayLabelProvider
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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

        @OptIn(ExperimentalTime::class)
        fun isEvenSemesterPeriod(): Boolean {
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
            return now.month.number in 1..6
        }

        fun isOddSemesterPeriod(): Boolean = !isEvenSemesterPeriod()

        @OptIn(ExperimentalTime::class)
        fun getCurrentAcademicStartYear(): Int {
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val currentMonth = now.month.number
            val currentYear = now.year
            return if (currentMonth in 1..6) currentYear - 1 else currentYear
        }

        @OptIn(ExperimentalTime::class)
        fun getCurrentAcademicEndYear(): Int {
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val currentMonth = now.month.number
            val currentYear = now.year
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
