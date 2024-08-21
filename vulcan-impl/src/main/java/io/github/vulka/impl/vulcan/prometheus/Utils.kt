package io.github.vulka.impl.vulcan.prometheus

import io.github.vulka.impl.vulcan.prometheus.login.JwtPayload
import kotlinx.serialization.json.Json
import java.util.*

fun decodeJWT(jwt: String): JwtPayload {
    val chunks = jwt.split(".")
    val decoded = String(Base64.getUrlDecoder().decode(chunks[1]))
    return Json.decodeFromString<JwtPayload>(decoded)
}