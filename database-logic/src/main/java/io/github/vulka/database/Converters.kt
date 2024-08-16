package io.github.vulka.database

import androidx.room.TypeConverter
import io.github.vulka.core.api.types.HomeworkAttachment
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.LessonChange
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun localDateFromString(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(it, dateFormatter)
        }
    }

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun localDateTimeFromString(value: String?): LocalDateTime? {
        return value?.let {
            LocalDateTime.parse(it, dateTimeFormatter)
        }
    }

    @TypeConverter
    fun localDateTimeToString(date: LocalDateTime?): String? {
        return date?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun fromLessonType(value: Lesson?): String? {
        return value?.let {
            Json.encodeToString(value)
        }
    }

    @TypeConverter
    fun toLessonType(value: String): Lesson? {
        return Json.decodeFromString<Lesson?>(value)
    }

    @TypeConverter
    fun fromLessonChange(change: LessonChange?): String {
        return Json.encodeToString(change)
    }

    @TypeConverter
    fun toLessonChange(changeString: String?): LessonChange? {
        return changeString?.let {
            Json.decodeFromString<LessonChange?>(changeString)
        }
    }

    @TypeConverter
    fun fromListHomeworkAttachment(change: List<HomeworkAttachment>): String {
        return Json.encodeToString(change)
    }

    @TypeConverter
    fun toListHomeworkAttachment(changeString: String): List<HomeworkAttachment> {
        return Json.decodeFromString<List<HomeworkAttachment>>(changeString)
    }
}
