package io.github.vulka.impl.vulcan

import io.github.vulka.core.api.types.Student
import io.github.vulka.impl.vulcan.hebe.types.HebeStudent
import io.github.vulka.impl.vulcan.prometheus.hebece.types.HebeCeStudent
import kotlinx.serialization.json.Json
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

fun Student.toHebe(): HebeStudent = Json.decodeFromString<HebeStudent>(customData.orEmpty())
fun Student.toHebeCe(): HebeCeStudent = Json.decodeFromString<HebeCeStudent>(customData!!)