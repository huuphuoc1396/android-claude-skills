package com.example.sample.feature.details

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** `1h 53m`, or `53m` under an hour, in every language. */
internal fun formatRuntime(minutes: Int): String {
    val hours = minutes / 60
    val rest = minutes % 60
    return if (hours > 0) "${hours}h ${rest}m" else "${rest}m"
}

/** `dd/MM/yyyy` in Portuguese, `MM/dd/yyyy` otherwise. */
internal fun formatReleaseDate(date: LocalDate, locale: Locale): String {
    val pattern = if (locale.language == "pt") "dd/MM/yyyy" else "MM/dd/yyyy"
    return date.format(DateTimeFormatter.ofPattern(pattern, locale))
}
