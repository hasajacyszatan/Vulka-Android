package io.github.vulka.ui.utils

import io.github.vulka.core.api.types.Student
import androidx.compose.ui.text.intl.Locale
import java.time.LocalDate
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
