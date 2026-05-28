package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val side: String, // "Long" or "Short"
    val status: String, // "Open" or "Closed"
    val entryPrice: Double,
    val exitPrice: Double? = null,
    val quantity: Double,
    val commissions: Double = 0.0,
    val entryDate: Long = System.currentTimeMillis(),
    val exitDate: Long? = null,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val notes: String = "",
    val setup: String = "None",
    val emotion: String = "Neutral",
    val imageUri: String? = null,
    val tradeType: String = "Swing"
) {
    val pnl: Double
        get() {
            if (status != "Closed" || exitPrice == null) return 0.0
            val directionMultiplier = if (side == "Long") 1.0 else -1.0
            return ((exitPrice - entryPrice) * quantity * directionMultiplier) - commissions
        }

    val targetRiskReward: Double
        get() {
            val stop = stopLoss ?: return 0.0
            val initialRisk = Math.abs(entryPrice - stop)
            if (initialRisk == 0.0) return 0.0
            val target = takeProfit ?: return 0.0
            val potentialReward = Math.abs(target - entryPrice)
            return potentialReward / initialRisk
        }

    val actualRMultiple: Double
        get() {
            val stop = stopLoss ?: return 0.0
            val initialRisk = Math.abs(entryPrice - stop)
            if (initialRisk == 0.0) return 0.0
            return pnl / (initialRisk * quantity)
        }
}
