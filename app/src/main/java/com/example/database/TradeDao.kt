package com.example.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY entryDate DESC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntity): Long

    @Update
    suspend fun updateTrade(trade: TradeEntity)

    @Delete
    suspend fun deleteTrade(trade: TradeEntity)

    @Query("DELETE FROM trades WHERE id = :id")
    suspend fun deleteTradeById(id: Long)

    @Query("SELECT * FROM trades WHERE id = :id LIMIT 1")
    suspend fun getTradeById(id: Long): TradeEntity?

    @Query("SELECT * FROM watchlists ORDER BY id ASC")
    fun getAllWatchlists(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WatchlistEntity): Long

    @Update
    suspend fun updateWatchlist(watchlist: WatchlistEntity)

    @Delete
    suspend fun deleteWatchlist(watchlist: WatchlistEntity)

    @Query("DELETE FROM watchlists WHERE id = :id")
    suspend fun deleteWatchlistById(id: Long)
}
