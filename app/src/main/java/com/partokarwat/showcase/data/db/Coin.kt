package com.partokarwat.showcase.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coins")
data class Coin(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val priceEur: Double,
    val changePercent24Hr: Double,
)
