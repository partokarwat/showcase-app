package com.partokarwat.showcase.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Coin::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao

    companion object {
        fun buildDatabase(context: Context): AppDatabase =
            Room
                .databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "showcase_database",
                ).build()
    }
}
