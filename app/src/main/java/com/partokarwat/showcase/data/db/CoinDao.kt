package com.partokarwat.showcase.data.db

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import androidx.room.Insert
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Query("SELECT * FROM coins ORDER BY changePercent24Hr DESC LIMIT 100")
    fun getTop100GainersCoins(): Flow<List<Coin>>

    @Query("SELECT * FROM coins ORDER BY changePercent24Hr ASC LIMIT 100")
    fun getTop100LoserCoins(): Flow<List<Coin>>

    @Query("SELECT * FROM coins WHERE id = :id LIMIT 1")
    fun getCoinById(id: String): Flow<Coin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoins(coins: List<Coin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoin(coins: Coin)

    @Query("DELETE FROM coins")
    fun deleteAllCoins()

    @Upsert
    suspend fun upsertAll(coins: List<Coin>)
}