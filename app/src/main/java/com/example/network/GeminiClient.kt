package com.example.network

import com.example.BuildConfig
import com.example.database.TradeEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import android.util.Base64

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "topP") val topP: Float? = null,
    @Json(name = "topK") val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun getTradeReviewInsight(trades: List<TradeEntity>): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Gemini API Key is not configured. Please add your GEMINI_API_KEY to the Secrets panel in AI Studio."
        }

        if (trades.isEmpty()) {
            return "You haven't logged any trades yet. Complete some trades first, then ask the AI to analyze your performance!"
        }

        val tradeSummary = trades.take(30).joinToString(separator = "\n") { trade ->
            "- Trade: ${trade.symbol} (${trade.side}), Status: ${trade.status}, Entry: $${trade.entryPrice}, Exit: ${trade.exitPrice?.let { "$$it" } ?: "N/A"}, Qty: ${trade.quantity}, Setup: ${trade.setup}, Emotion: ${trade.emotion}, PnL: $${trade.pnl}, R-Mult: ${trade.actualRMultiple}"
        }

        val prompt = """
            Act as an elite quantitative prop trading performance psychologist and risk auditor.
            Analyze my logged trades listed below and provide deep actionable behavioral insights.
            Be direct, constructive, and highly pragmatic. Structure your advice using these sections:
            
            1. 📊 Key Performance Leak: Spot physical or psychological errors in risk sizing, SL respect, or emotional triggers (e.g. FOMO, Anxiety, Greed).
            2. 🎯 Winning Setup Synergy: Identify which setups and psychology yield the highest realized R-Multiples.
            3. 🛠️ Concrete Actions: Give 3 prescriptive tactical exercises to implement tomorrow to cut emotional exits and maximize win rate or profit factors.
            
            My Logged Trades (past 30):
            $tradeSummary
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "You are a world-class cognitive-behavioral performance coach specializing in day trading and currency markets. You write clean, professional, and precise market feedback. Avoid generic motivational statements.")))
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No insights could be generated. Check your connection or retry."
        } catch (e: Exception) {
            "Failed to reach Gemini API: ${e.localizedMessage ?: "Unknown Error"}. Make sure you have set a valid GEMINI_API_KEY in the Secrets panel."
        }
    }

    suspend fun analyzeChartScreenshot(bitmap: Bitmap, customPrompt: String? = null): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Gemini API Key is not configured. Please add your GEMINI_API_KEY to the Secrets panel in AI Studio."
        }

        val prompt = customPrompt ?: """
            Act as an elite technical analyst, price action trading guide, and risk manager.
            Examine this trade chart screenshot in detail and perform high-caliber analysis:
            
            1. 📈 Trend & Key Structure: Identify apparent support, resistance, trend lines, market structure (e.g. Higher Highs/Lows, ranges, breakouts).
            2. 🕯️ Candlestick & Volume Patterns: Detect key candlesticks (e.g., Hammers, engulfing patterns, dojis) and volume flags.
            3. 🎯 Execution Feedback: Assess the setup quality, entry triggers, stop-loss / take-profit placement. Point out any structural leaks or high-risk setups.
            
            Ensure your insights are sharp, constructive, professional, and directly actionable.
        """.trimIndent()

        val base64Data = try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        } catch (e: Exception) {
            return "Failed to convert the chart screenshot for analysis: ${e.localizedMessage}"
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Data))
                    )
                )
            ),
            systemInstruction = Content(
                parts = listOf(
                    Part(text = "You are a world-class proprietary market analyst and risk supervisor. You provide authoritative, objective, and deeply technical analysis of charts. No generic financial advice.")
                )
            )
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No response generated. Please try again with a different image."
        } catch (e: Exception) {
            "Failed to reach Gemini API for vision chart analysis: ${e.localizedMessage ?: "Unknown Error"}. Please check your connection."
        }
    }

    suspend fun analyzeSymbol(symbol: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Gemini API Key is not configured. Please add your GEMINI_API_KEY to the Secrets panel in AI Studio."
        }

        val prompt = """
            Provide a deep quantitative and technical analysis breakdown for the trading ticker: $symbol.
            Include:
            1. 📊 Key Price Levels: Major estimated Support and Resistance levels.
            2. 📈 Chart Structure: Likely current price action pattern (e.g., bull flag, double bottom, breakout range).
            3. 🎯 Operational Trading Setup: Suggested tactical Entry Trigger, Stop-Loss, and Take-Profit values / ratios.
            4. ⚠️ Key Risk Warning: A specific systemic risk factor or event to be mindful of for this asset.
            
            Be precise, authoritative, and direct. Format with clean bullet points.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = "You are a world-class premium quantitative researcher and technical analyst. You write clean, direct, and actionable briefings. Do not make generic disclosures or vague recommendations."))
            )
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No analysis generated. Check model connection and retry."
        } catch (e: Exception) {
            "Failed to reach Gemini: ${e.localizedMessage ?: "Unknown Error"}"
        }
    }

    suspend fun getWatchlistSuggestions(theme: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Gemini API Key is not configured. Please add your GEMINI_API_KEY to the Secrets panel in AI Studio."
        }

        val prompt = """
            Suggest 5-6 high-potential ticker symbols / pairs fitting the theme: '$theme'.
            For each asset, explain in one crisp sentence why it fits this specific theme and what to watch for.
            Format clearly. Use the ticker name as title, e.g., AAPL, BTC/USD, TSLA, etc.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = "You are an elite quantitative analyst. Provide clear, highly strategic market theme briefings. No fluff, no generic warnings."))
            )
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No recommendations generated. Try again."
        } catch (e: Exception) {
            "Failed to reach Gemini: ${e.localizedMessage ?: "Unknown Error"}"
        }
    }
}

