package com.partokarwat.showcase.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.partokarwat.showcase.data.db.AppDatabase
import com.partokarwat.showcase.data.db.CoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_preferences",
)

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = AppDatabase.getInstance(context)

    @Provides
    fun provideCoinDao(appDatabase: AppDatabase): CoinDao = appDatabase.coinDao()

    @Provides
    @Singleton
    fun provideDataStorePreferences(
        @ApplicationContext applicationContext: Context,
    ): DataStore<Preferences> = applicationContext.dataStore
}
