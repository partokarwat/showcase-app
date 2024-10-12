package com.partokarwat.showcase.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Query("SELECT * FROM coins")
    fun getCoinAllCoins(): Flow<List<Coin>>

    @Query("SELECT * FROM coins ORDER BY changePercent24Hr DESC LIMIT :amount")
    fun getTopGainersCoins(amount: Int): Flow<List<Coin>>

    @Query("SELECT * FROM coins ORDER BY changePercent24Hr ASC LIMIT :amount")
    fun getTopLoserCoins(amount: Int): Flow<List<Coin>>

    @Query("SELECT * FROM coins WHERE id = :id LIMIT 1")
    fun getCoinById(id: String): Flow<Coin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoins(coins: List<Coin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoin(coins: Coin)

    @Query("DELETE FROM coins")
    fun deleteAllCoins()

    @Query("DELETE FROM coins WHERE id = :id")
    fun deleteCoinById(id: String)

    @Upsert
    suspend fun upsertAll(coins: List<Coin>)
}
