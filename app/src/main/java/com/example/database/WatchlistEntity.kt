package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlists")
data class WatchlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val symbols: String = "" // Comma-separated symbols (e.g., "BTC/USD, ETH/USD, AAPL")
)
