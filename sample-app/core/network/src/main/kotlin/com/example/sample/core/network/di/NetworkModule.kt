package com.example.sample.core.network.di

import com.example.sample.core.network.NewsNetworkDataSource
import com.example.sample.core.network.retrofit.NewsApi
import com.example.sample.core.network.retrofit.RetrofitNewsNetwork
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

private const val BASE_URL = "https://api.spaceflightnewsapi.net/"

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    @Provides
    @Singleton
    fun providesNewsApi(okHttpClient: OkHttpClient): NewsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkBindsModule {
    @Binds
    fun bindsNewsNetworkDataSource(impl: RetrofitNewsNetwork): NewsNetworkDataSource
}
