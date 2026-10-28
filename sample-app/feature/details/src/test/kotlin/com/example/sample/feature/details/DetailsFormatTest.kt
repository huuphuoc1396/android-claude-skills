package com.example.sample.feature.details

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.util.Locale

class DetailsFormatTest {
    @Test
    fun formatRuntime_hoursAndMinutes() {
        assertEquals("1h 53m", formatRuntime(113))
    }

    @Test
    fun formatRuntime_underAnHour() {
        assertEquals("45m", formatRuntime(45))
    }

    @Test
    fun formatReleaseDate_portuguese() {
        assertEquals("10/12/2021", formatReleaseDate(LocalDate.of(2021, 12, 10), Locale.forLanguageTag("pt-BR")))
    }

    @Test
    fun formatReleaseDate_english() {
        assertEquals("12/10/2021", formatReleaseDate(LocalDate.of(2021, 12, 10), Locale.US))
    }
}
