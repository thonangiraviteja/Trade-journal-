package com.example.database

import kotlinx.coroutines.flow.Flow

class TradeRepository(private val tradeDao: TradeDao) {
    val allTrades: Flow<List<TradeEntity>> = tradeDao.getAllTrades()

    suspend fun insert(trade: TradeEntity): Long = tradeDao.insertTrade(trade)

    suspend fun update(trade: TradeEntity) = tradeDao.updateTrade(trade)

    suspend fun delete(trade: TradeEntity) = tradeDao.deleteTrade(trade)

    suspend fun deleteById(id: Long) = tradeDao.deleteTradeById(id)

    suspend fun getById(id: Long): TradeEntity? = tradeDao.getTradeById(id)

    val allWatchlists: Flow<List<WatchlistEntity>> = tradeDao.getAllWatchlists()

    suspend fun insertWatchlist(watchlist: WatchlistEntity): Long = tradeDao.insertWatchlist(watchlist)

    suspend fun updateWatchlist(watchlist: WatchlistEntity) = tradeDao.updateWatchlist(watchlist)

    suspend fun deleteWatchlist(watchlist: WatchlistEntity) = tradeDao.deleteWatchlist(watchlist)

    suspend fun deleteWatchlistById(id: Long) = tradeDao.deleteWatchlistById(id)
}
