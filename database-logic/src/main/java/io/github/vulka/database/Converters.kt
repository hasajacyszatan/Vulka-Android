package io.github.vulka.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.LessonChange
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
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLessonType(value: String?): Lesson? {
        val type = object : TypeToken<Lesson>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromLessonChange(change: LessonChange?): String? {
        return Gson().toJson(change)
    }

    @TypeConverter
    fun toLessonChange(changeString: String?): LessonChange? {
        return Gson().fromJson(changeString, object : TypeToken<LessonChange>() {}.type)
    }
}
