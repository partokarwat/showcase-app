package com.partokarwat.showcase.di

import com.partokarwat.showcase.data.remote.CoinCapApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

    @Provides
    fun provideRetrofit(json: Json): Retrofit =
        Retrofit
            .Builder()
            .baseUrl("https://api.coincap.io/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun provideCryptoCoroutinesApi(retrofit: Retrofit): CoinCapApi = retrofit.create()
}
