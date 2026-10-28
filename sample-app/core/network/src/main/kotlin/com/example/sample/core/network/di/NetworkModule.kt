package com.example.sample.core.network.di

import com.example.sample.core.network.BuildConfig
import com.example.sample.core.network.TmdbNetworkDataSource
import com.example.sample.core.network.retrofit.RetrofitTmdbNetwork
import com.example.sample.core.network.retrofit.TmdbApi
import com.example.sample.core.network.retrofit.TmdbRequestInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                // Request lines only, and never in release builds; the API key must not reach logcat.
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
                redactQueryParams("api_key")
            },
        )
        .build()

    @Provides
    @Singleton
    fun providesTmdbApi(okHttpClient: OkHttpClient): TmdbApi = Retrofit.Builder()
        .baseUrl(TMDB_BASE_URL)
        .client(okHttpClient.newBuilder().addInterceptor(TmdbRequestInterceptor()).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TmdbApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkBindsModule {
    @Binds
    fun bindsTmdbNetworkDataSource(impl: RetrofitTmdbNetwork): TmdbNetworkDataSource
}
