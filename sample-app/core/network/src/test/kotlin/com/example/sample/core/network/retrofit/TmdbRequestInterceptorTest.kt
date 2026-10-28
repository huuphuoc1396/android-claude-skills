package com.example.sample.core.network.retrofit

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class TmdbRequestInterceptorTest {
    @Test
    fun tmdbLanguage_portuguese_isPtBr() {
        assertEquals("pt-BR", tmdbLanguage(Locale.forLanguageTag("pt-BR")))
    }

    @Test
    fun tmdbLanguage_portugalPortuguese_isPtBr() {
        assertEquals("pt-BR", tmdbLanguage(Locale.forLanguageTag("pt-PT")))
    }

    @Test
    fun tmdbLanguage_english_isEnUs() {
        assertEquals("en-US", tmdbLanguage(Locale.forLanguageTag("en-GB")))
    }

    @Test
    fun tmdbLanguage_otherLanguage_isEnUs() {
        assertEquals("en-US", tmdbLanguage(Locale.forLanguageTag("vi-VN")))
    }
}
