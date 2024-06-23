package io.github.vulka.database

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.LessonChange
import io.github.vulka.core.api.types.Parent
import java.lang.reflect.Type
import io.github.vulka.core.api.types.Student
import io.github.vulka.core.api.types.StudentImpl
import io.github.vulka.impl.vulcan.hebe.types.HebeStudent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromStudent(student: Student): String {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Student::class.java, StudentTypeAdapter())
            .create()
        val s = gson.toJson(student)
        Log.d("Room Converters",s)
        return s
    }

    @TypeConverter
    fun toStudent(studentString: String): Student {
        Log.d("Room Converters",studentString)
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Student::class.java, StudentTypeAdapter())
            .create()
        val type = object : TypeToken<Student>() {}.type
        val student = gson.fromJson<Student>(studentString, type)
        return student
    }

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

// TODO: Make simpler Student deserialized
class StudentTypeAdapter : JsonSerializer<Student>, JsonDeserializer<Student> {
    override fun serialize(src: Student, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("fullName", src.fullName)
        jsonObject.addProperty("isParent", src.isParent)
        jsonObject.add("parent", context.serialize(src.parent))
        jsonObject.addProperty("classId", src.classId)

        val implObject = when (src.impl) {
            is HebeStudent -> context.serialize(src.impl, HebeStudent::class.java)
            else -> JsonObject()
        }
        jsonObject.add("impl", implObject)

        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Student {
        val jsonObject = json.asJsonObject

        val fullName = jsonObject.get("fullName").asString
        val isParent = jsonObject.get("isParent").asBoolean
        val parent = context.deserialize<Parent>(jsonObject.get("parent"), Parent::class.java)
        val classId = if (jsonObject.get("classId") != null )jsonObject.get("classId").asString else null

        val implObject = jsonObject.get("impl")
        val impl = if (implObject.asJsonObject.has("ClassDisplay")) {
            context.deserialize<HebeStudent>(implObject, HebeStudent::class.java)
        } else {
            StudentImpl()
        }

        return Student(fullName, isParent, parent, classId, impl)
    }
}