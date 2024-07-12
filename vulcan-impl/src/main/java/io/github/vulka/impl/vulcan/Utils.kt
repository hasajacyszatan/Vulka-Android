package io.github.vulka.impl.vulcan

import com.google.gson.Gson
import io.github.vulka.core.api.types.Student
import io.github.vulka.impl.vulcan.hebe.types.HebeStudent
import java.util.UUID

object Utils {
    @JvmStatic
    fun uuid(seed: String?): String {
        return if (!seed.isNullOrEmpty()) {
            UUID.nameUUIDFromBytes(seed.toByteArray()).toString()
        } else {
            UUID.randomUUID().toString()
        }
    }
}

fun Student.toHebe(): HebeStudent = Gson().fromJson(customData, HebeStudent::class.java)