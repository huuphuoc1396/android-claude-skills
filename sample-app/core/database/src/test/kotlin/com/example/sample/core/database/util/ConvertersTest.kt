package com.example.sample.core.database.util

import com.example.sample.core.database.model.CachedCastMember
import com.example.sample.core.database.model.CachedTitle
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun castRoundTrip() {
        val cast = listOf(CachedCastMember("Sandra Bullock", "https://image/sandra.jpg"), CachedCastMember("Viola Davis", null))

        assertEquals(cast, converters.jsonToCast(converters.castToJson(cast)))
    }

    @Test
    fun recommendationsRoundTrip() {
        val titles = listOf(CachedTitle(1, "Tv", "Lucifer", null))

        assertEquals(titles, converters.jsonToTitles(converters.titlesToJson(titles)))
    }

    @Test
    fun genresRoundTrip() {
        val genres = listOf("Drama", "Ação")

        assertEquals(genres, converters.jsonToStrings(converters.stringsToJson(genres)))
    }
}
