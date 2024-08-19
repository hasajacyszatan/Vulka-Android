package io.github.vulka.ui.utils

import androidx.compose.runtime.Composable
import io.github.vulka.core.api.types.Student
import androidx.compose.ui.text.intl.Locale
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale as JavaLocale

fun Student.getInitials(): String {
    return fullName.split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
}

fun LocalDate.formatByLocale(locale: Locale): String {
    val javaLocale = JavaLocale(locale.language, locale.region)

    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(javaLocale)
    return this.format(formatter)
}

fun LocalDateTime.formatByLocale(locale: Locale): String {
    val javaLocale = JavaLocale(locale.language, locale.region)

    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(javaLocale)
    return this.format(formatter)
}
fun Locale.toJavaLocale() = JavaLocale(language, region)

@Composable
fun Float?.formatForUI() = String.format(
    Locale.current.toJavaLocale(),
    "%.2f",this
)