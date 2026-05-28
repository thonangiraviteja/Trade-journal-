package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.TradeDatabase
import com.example.database.TradeEntity
import com.example.database.TradeRepository
import com.example.database.WatchlistEntity
import com.example.network.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import android.graphics.Bitmap

class TradeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TradeRepository
    private val sharedPrefs = application.getSharedPreferences("trading_journal_prefs", Context.MODE_PRIVATE)

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("TradeViewModel", "Unhandled coroutine error caught", exception)
    }

    private val _customSetups = MutableStateFlow<List<String>>(emptyList())
    val customSetups: StateFlow<List<String>> = _customSetups.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean>(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _accentColorName = MutableStateFlow<String>("Classic Ice")
    val accentColorName: StateFlow<String> = _accentColorName.asStateFlow()

    private val _selectedCurrency = MutableStateFlow<String>("USD")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    init {
        val database = TradeDatabase.getDatabase(application)
        repository = TradeRepository(database.tradeDao())

        val savedSetups = sharedPrefs.getStringSet("custom_setups", null) ?: emptySet()
        _customSetups.value = savedSetups.toList().sorted()
        _isDarkMode.value = sharedPrefs.getBoolean("pref_dark_mode", true)
        _accentColorName.value = sharedPrefs.getString("pref_accent_color_name", "Classic Ice") ?: "Classic Ice"
        _selectedCurrency.value = sharedPrefs.getString("pref_currency_code", "USD") ?: "USD"
    }

    fun addCustomSetup(name: String) {
        val currentSet = _customSetups.value.toMutableSet()
        val cleanName = name.trim()
        if (cleanName.isNotEmpty() && !currentSet.contains(cleanName)) {
            currentSet.add(cleanName)
            _customSetups.value = currentSet.toList().sorted()
            sharedPrefs.edit().putStringSet("custom_setups", currentSet).apply()
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        sharedPrefs.edit().putBoolean("pref_dark_mode", enabled).apply()
    }

    fun selectAccentColor(name: String) {
        _accentColorName.value = name
        sharedPrefs.edit().putString("pref_accent_color_name", name).apply()
    }

    fun selectCurrency(currencyCode: String) {
        _selectedCurrency.value = currencyCode
        sharedPrefs.edit().putString("pref_currency_code", currencyCode).apply()
    }

    val tradesState: StateFlow<List<TradeEntity>> = repository.allTrades
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _aiInsights = MutableStateFlow<String>("")
    val aiInsights: StateFlow<String> = _aiInsights.asStateFlow()

    private val _isGeneratingInsights = MutableStateFlow(false)
    val isGeneratingInsights: StateFlow<Boolean> = _isGeneratingInsights.asStateFlow()

    fun logTrade(
        symbol: String,
        side: String,
        status: String,
        entryPrice: Double,
        exitPrice: Double?,
        quantity: Double,
        commissions: Double,
        stopLoss: Double?,
        takeProfit: Double?,
        notes: String,
        setup: String,
        emotion: String,
        imageUri: String? = null,
        tradeType: String = "Swing",
        entryDate: Long = System.currentTimeMillis(),
        exitDate: Long? = null
    ) {
        viewModelScope.launch(errorHandler) {
            val trade = TradeEntity(
                symbol = symbol.uppercase().trim(),
                side = side,
                status = status,
                entryPrice = entryPrice,
                exitPrice = exitPrice,
                quantity = quantity,
                commissions = commissions,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                notes = notes,
                setup = setup,
                emotion = emotion,
                imageUri = imageUri,
                tradeType = tradeType,
                entryDate = entryDate,
                exitDate = exitDate ?: if (status == "Closed") System.currentTimeMillis() else null
            )
            repository.insert(trade)
        }
    }

    fun updateTrade(trade: TradeEntity) {
        viewModelScope.launch(errorHandler) {
            repository.update(trade)
        }
    }

    fun deleteTrade(trade: TradeEntity) {
        viewModelScope.launch(errorHandler) {
            repository.delete(trade)
        }
    }

    fun deleteTradeById(id: Long) {
        viewModelScope.launch(errorHandler) {
            repository.deleteById(id)
        }
    }

    fun getAiInsights() {
        if (_isGeneratingInsights.value) return
        _isGeneratingInsights.value = true
        _aiInsights.value = "Consulting performance psychologist..."
        viewModelScope.launch(errorHandler) {
            val currentTrades = tradesState.value
            val insights = GeminiClient.getTradeReviewInsight(currentTrades)
            _aiInsights.value = insights
            _isGeneratingInsights.value = false
        }
    }

    fun clearInsights() {
        _aiInsights.value = ""
    }

    private val _screenshotInsights = MutableStateFlow<String>("")
    val screenshotInsights: StateFlow<String> = _screenshotInsights.asStateFlow()

    private val _isAnalyzingScreenshot = MutableStateFlow(false)
    val isAnalyzingScreenshot: StateFlow<Boolean> = _isAnalyzingScreenshot.asStateFlow()

    fun analyzeChartScreenshot(bitmap: Bitmap, customPrompt: String? = null) {
        if (_isAnalyzingScreenshot.value) return
        _isAnalyzingScreenshot.value = true
        _screenshotInsights.value = "Uploading and scanning chart setup with Gemini Vision AI..."
        viewModelScope.launch(errorHandler) {
            val result = GeminiClient.analyzeChartScreenshot(bitmap, customPrompt)
            _screenshotInsights.value = result
            _isAnalyzingScreenshot.value = false
        }
    }

    fun clearScreenshotInsights() {
        _screenshotInsights.value = ""
    }

    val watchlistsState: StateFlow<List<WatchlistEntity>> = repository.allWatchlists
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addWatchlist(name: String, symbols: String = "") {
        viewModelScope.launch(errorHandler) {
            repository.insertWatchlist(WatchlistEntity(name = name, symbols = symbols))
        }
    }

    fun updateWatchlist(watchlist: WatchlistEntity) {
        viewModelScope.launch(errorHandler) {
            repository.updateWatchlist(watchlist)
        }
    }

    fun deleteWatchlist(watchlist: WatchlistEntity) {
        viewModelScope.launch(errorHandler) {
            repository.deleteWatchlist(watchlist)
        }
    }

    fun deleteWatchlistById(id: Long) {
        viewModelScope.launch(errorHandler) {
            repository.deleteWatchlistById(id)
        }
    }

    private val _watchlistAiAnalysis = MutableStateFlow<String>("")
    val watchlistAiAnalysis: StateFlow<String> = _watchlistAiAnalysis.asStateFlow()

    private val _isAnalyzingWatchlist = MutableStateFlow(false)
    val isAnalyzingWatchlist: StateFlow<Boolean> = _isAnalyzingWatchlist.asStateFlow()

    fun analyzeWatchlistSymbol(symbol: String) {
        if (symbol.isBlank()) return
        if (_isAnalyzingWatchlist.value) return
        _isAnalyzingWatchlist.value = true
        _watchlistAiAnalysis.value = "Gemini is performing deep technical and sentiment analysis on $symbol..."
        viewModelScope.launch(errorHandler) {
            val response = GeminiClient.analyzeSymbol(symbol)
            _watchlistAiAnalysis.value = response
            _isAnalyzingWatchlist.value = false
        }
    }

    fun getWatchlistSuggestions(theme: String) {
        if (_isAnalyzingWatchlist.value) return
        _isAnalyzingWatchlist.value = true
        _watchlistAiAnalysis.value = "Gemini AI is scanning market concepts for standard '$theme' watchlists..."
        viewModelScope.launch(errorHandler) {
            val response = GeminiClient.getWatchlistSuggestions(theme)
            _watchlistAiAnalysis.value = response
            _isAnalyzingWatchlist.value = false
        }
    }

    fun clearWatchlistAnalysis() {
        _watchlistAiAnalysis.value = ""
    }
}

