package com.example.sample.core.network.retrofit

import com.example.sample.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

/** Adds the API key and the content language to every TMDB request. */
internal class TmdbRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
            .addQueryParameter("language", tmdbLanguage(Locale.getDefault()))
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}

/** Any Portuguese gets Brazilian Portuguese content; every other language gets English. */
internal fun tmdbLanguage(locale: Locale): String = if (locale.language == "pt") "pt-BR" else "en-US"

/** Videos in the content language, then English, then ones with no language; TMDB filters by language otherwise. */
internal fun tmdbVideoLanguages(locale: Locale): String = if (locale.language == "pt") "pt,en,null" else "en,null"

/** Release dates follow the content language: Brazil for Portuguese, the US otherwise. */
internal fun tmdbRegion(locale: Locale): String = if (locale.language == "pt") "BR" else "US"
