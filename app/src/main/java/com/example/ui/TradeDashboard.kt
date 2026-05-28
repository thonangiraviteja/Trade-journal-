package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import kotlin.math.sin
import kotlin.math.cos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.database.TradeEntity
import com.example.database.WatchlistEntity
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.content.ContentValues
import android.provider.MediaStore
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import kotlin.math.roundToInt

var currentCurrencyCode: String = "USD"

// Elegant Dark/Light Theme Customizer Palette mappings
data class AppPalette(
    val background: Color,
    val cardBg: Color,
    val subCardBg: Color,
    val neonBlue: Color,
    val accentOrange: Color,
    val softText: Color,
    val dividerGray: Color,
    val chartGreen: Color,
    val chartRed: Color,
    val greenBadgeBg: Color,
    val redBadgeBg: Color,
    val textPrimary: Color,
    val textSecondary: Color
)

fun getPaletteFor(isDark: Boolean, accentColorName: String): AppPalette {
    val (darkAccent, lightAccent) = when (accentColorName) {
        "Classic Ice" -> Color(0xFF38BDF8) to Color(0xFF0F52BA)
        "Emerald Mint" -> Color(0xFF34D399) to Color(0xFF047857)
        "Sunset Fire" -> Color(0xFFFB923C) to Color(0xFFC2410C)
        "Retro Candy" -> Color(0xFFF472B6) to Color(0xFFBE185D)
        "Electric Orchid" -> Color(0xFFC084FC) to Color(0xFF7E22CE)
        else -> Color(0xFF38BDF8) to Color(0xFF0F52BA)
    }

    return if (isDark) {
        AppPalette(
            background = Color(0xFF0A0B0D), // Deeper sleek black
            cardBg = Color(0xFF15171C),     // Polished charcoal card
            subCardBg = Color(0xFF1E2129),  // Elevated content box
            neonBlue = darkAccent,
            accentOrange = darkAccent,
            softText = Color(0xFF94A3B8),   // Crisp platinum gray
            dividerGray = Color(0xFF232733),
            chartGreen = Color(0xFF34D399), // Neon mint green
            chartRed = Color(0xFFF87171),   // Bright warm red
            greenBadgeBg = Color(0xFF064E3B),
            redBadgeBg = Color(0xFF7F1D1D),
            textPrimary = Color(0xFFFAFAFA),
            textSecondary = Color(0xFF64748B)
        )
    } else {
        AppPalette(
            background = Color(0xFFF8FAFC), // Crisp clean canvas light
            cardBg = Color(0xFFFFFFFF),     // Bright pristine paper
            subCardBg = Color(0xFFF1F5F9),  // Soft background panel
            neonBlue = lightAccent,
            accentOrange = lightAccent,
            softText = Color(0xFF475569),   // Muted charcoal body
            dividerGray = Color(0xFFE2E8F0),
            chartGreen = Color(0xFF059669), // Rich emerald green
            chartRed = Color(0xFFDC2626),   // Strong alarm red
            greenBadgeBg = Color(0xFFD1FAE5),
            redBadgeBg = Color(0xFFFEE2E2),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF475569)
        )
    }
}

var currentPaletteState by mutableStateOf(getPaletteFor(true, "Classic Ice"))

val CarbonBlack: Color get() = currentPaletteState.background
val SlateBackground: Color get() = currentPaletteState.background
val CardGray: Color get() = currentPaletteState.cardBg
val ChartGreen: Color get() = currentPaletteState.chartGreen
val ChartRed: Color get() = currentPaletteState.chartRed
val NeonBlue: Color get() = currentPaletteState.neonBlue
val AccentOrange: Color get() = currentPaletteState.accentOrange
val SoftText: Color get() = currentPaletteState.softText
val DividerGray: Color get() = currentPaletteState.dividerGray
val GreenBadgeBg: Color get() = currentPaletteState.greenBadgeBg
val RedBadgeBg: Color get() = currentPaletteState.redBadgeBg
val SubCardBackground: Color get() = currentPaletteState.subCardBg
val TextWhite: Color get() = currentPaletteState.textPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeDashboard(
    viewModel: TradeViewModel,
    modifier: Modifier = Modifier
) {
    val trades by viewModel.tradesState.collectAsStateWithLifecycle()
    val aiInsights by viewModel.aiInsights.collectAsStateWithLifecycle()
    val isGeneratingInsights by viewModel.isGeneratingInsights.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val accentName by viewModel.accentColorName.collectAsStateWithLifecycle()
    val customSetups by viewModel.customSetups.collectAsStateWithLifecycle()
    val screenshotInsights by viewModel.screenshotInsights.collectAsStateWithLifecycle()
    val isAnalyzingScreenshot by viewModel.isAnalyzingScreenshot.collectAsStateWithLifecycle()

    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    LaunchedEffect(isDark, accentName) {
        currentPaletteState = getPaletteFor(isDark, accentName)
    }

    LaunchedEffect(selectedCurrency) {
        currentCurrencyCode = selectedCurrency
    }

    var showLogForm by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showThemeCustomizer by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("main_scaffold"),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(ChartGreen)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "TRADING JOURNAL",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextWhite
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CarbonBlack,
                        titleContentColor = TextWhite
                    ),
                    actions = {
                        IconButton(
                            onClick = { showThemeCustomizer = true },
                            modifier = Modifier.testTag("theme_customizer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Theme Customizer",
                                tint = NeonBlue
                            )
                        }
                        IconButton(
                            onClick = { viewModel.getAiInsights() },
                            modifier = Modifier.testTag("sync_ai_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Trigger AI Coach",
                                tint = if (isGeneratingInsights) SoftText else AccentOrange
                            )
                        }
                    }
                )
                HorizontalDivider(color = DividerGray, modifier = Modifier.fillMaxWidth())
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLogForm = true },
                containerColor = ChartGreen,
                contentColor = Color.Black,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("add_trade_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Log Trade")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEW ENTRY",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        },
        containerColor = CarbonBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Metrics Header Card
            MetricsBannerCard(trades = trades)

            // Dynamic Segmented Pill Tab Bar Redesign
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CarbonBlack)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabLabels = listOf(
                    Triple("PERFORMANCE", Icons.Filled.Home, "tab_performance"),
                    Triple("LEDGER", Icons.AutoMirrored.Filled.List, "tab_ledger"),
                    Triple("AI COACH", Icons.Filled.Star, "tab_ai_coach"),
                    Triple("WATCHLIST", Icons.Filled.Favorite, "tab_watchlist")
                )
                tabLabels.forEachIndexed { index, (label, icon, tag) ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) {
                                    NeonBlue.copy(alpha = 0.12f)
                                } else {
                                    Color.Transparent
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) NeonBlue.copy(alpha = 0.4f) else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp)
                            .testTag(tag),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isSelected) NeonBlue else SoftText
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.3.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (isSelected) TextWhite else SoftText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = DividerGray, modifier = Modifier.fillMaxWidth())

            GeminiAuraBackground(
                isDark = isDark,
                accentColorName = accentName,
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectedTab) {
                    0 -> PerformanceTab(trades = trades, viewModel = viewModel)
                    1 -> LedgerTab(
                        trades = trades,
                        onDeleteTrade = { trade -> viewModel.deleteTrade(trade) }
                    )
                    2 -> AiCoachTab(
                        insights = aiInsights,
                        isGenerating = isGeneratingInsights,
                        onGenerate = { viewModel.getAiInsights() },
                        screenshotInsights = screenshotInsights,
                        isAnalyzingScreenshot = isAnalyzingScreenshot,
                        onAnalyzeScreenshot = { bitmap, prompt -> viewModel.analyzeChartScreenshot(bitmap, prompt) },
                        onClearScreenshotInsights = { viewModel.clearScreenshotInsights() }
                    )
                    3 -> WatchlistTab(viewModel = viewModel)
                }
            }
        }

        if (showThemeCustomizer) {
            AlertDialog(
                onDismissRequest = { showThemeCustomizer = false },
                containerColor = CardGray,
                title = {
                    Text(
                        "THEME & STYLE CUSTOMIZER",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                        color = TextWhite
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Dark Theme", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                                Text("Switch between Light and Dark core interface", style = MaterialTheme.typography.labelSmall, color = SoftText)
                            }
                            Switch(
                                checked = isDark,
                                onCheckedChange = { viewModel.toggleDarkMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = ChartGreen,
                                    uncheckedThumbColor = SoftText,
                                    uncheckedTrackColor = DividerGray
                                )
                            )
                        }

                        HorizontalDivider(color = DividerGray)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Global Currency Unit", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                            Text("Select base indicator for all dynamic calculations", style = MaterialTheme.typography.labelSmall, color = SoftText)
                        }

                        val currencies = listOf(
                            "USD" to "$ (USD)",
                            "EUR" to "€ (EUR)",
                            "GBP" to "£ (GBP)",
                            "INR" to "₹ (INR)",
                            "JPY" to "¥ (JPY)",
                            "CAD" to "C$ (CAD)",
                            "AUD" to "A$ (AUD)"
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            items(items = currencies) { (code, label) ->
                                val isSelected = selectedCurrency == code
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonBlue.copy(alpha = 0.2f) else SlateBackground.copy(alpha = 0.6f))
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) NeonBlue else DividerGray.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.selectCurrency(code) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) TextWhite else SoftText
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = DividerGray)

                        Text("Select Accent Color Palette:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextWhite)

                        val accents = listOf(
                            "Classic Ice" to Color(0xFFD1E4FF),
                            "Emerald Mint" to Color(0xFF66BB6A),
                            "Sunset Fire" to Color(0xFFFFB74D),
                            "Retro Candy" to Color(0xFFF8BBD0),
                            "Electric Orchid" to Color(0xFFCE93D8)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            accents.forEach { (name, color) ->
                                val isSelected = accentName == name
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DividerGray.copy(alpha = 0.5f) else Color.Transparent)
                                        .clickable { viewModel.selectAccentColor(name) }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(2.dp, if (isSelected) TextWhite else Color.Transparent, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) TextWhite else SoftText
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showThemeCustomizer = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("DONE", fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }

        if (showLogForm) {
            LogTradeDialog(
                customSetups = customSetups,
                onAddCustomSetup = { newSetup -> viewModel.addCustomSetup(newSetup) },
                onDismiss = { showLogForm = false },
                onSave = { symbol, side, status, entry, exit, qty, comm, sl, tp, setup, emotion, notes, imageUri, tradeType ->
                    viewModel.logTrade(
                        symbol = symbol,
                        side = side,
                        status = status,
                        entryPrice = entry,
                        exitPrice = exit,
                        quantity = qty,
                        commissions = comm,
                        stopLoss = sl,
                        takeProfit = tp,
                        setup = setup,
                        emotion = emotion,
                        notes = notes,
                        imageUri = imageUri,
                        tradeType = tradeType
                    )
                    showLogForm = false
                }
            )
        }
    }
}

@Composable
fun MetricsBannerCard(trades: List<TradeEntity>) {
    val closedTrades = trades.filter { it.status == "Closed" }
    val totalTrades = trades.size
    val totalPnL = closedTrades.sumOf { it.pnl }
    val winningTrades = closedTrades.filter { it.pnl > 0 }
    val winRate = if (closedTrades.isNotEmpty()) {
        (winningTrades.size.toDouble() / closedTrades.size.toDouble()) * 100
    } else {
        0.0
    }

    val totalWins = winningTrades.sumOf { it.pnl }
    val totalLosses = Math.abs(closedTrades.filter { it.pnl < 0 }.sumOf { it.pnl })
    val profitFactor = if (totalLosses > 0) totalWins / totalLosses else if (totalWins > 0) Double.POSITIVE_INFINITY else 1.0

    // Compute dynamic percentage based on a benchmark entry account size of $10,000
    val initialBalance = 10000.00
    val percentChange = (totalPnL / initialBalance) * 100.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (CarbonBlack == Color(0xFFF8FAFC)) 0.4f else 0.12f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CardGray),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PORTFOLIO NET P&L",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            ),
                            color = SoftText
                        )
                        
                        val badgeText = if (percentChange >= 0) "+${String.format(Locale.US, "%.1f", percentChange)}%" else "${String.format(Locale.US, "%.1f", percentChange)}%"
                        val badgeTextColor = if (percentChange >= 0) ChartGreen else ChartRed
                        val badgeBgColor = if (percentChange >= 0) GreenBadgeBg else RedBadgeBg
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(badgeBgColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = badgeTextColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (totalPnL >= 0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Trend direction",
                            tint = if (totalPnL >= 0) ChartGreen else ChartRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = formatCurrency(totalPnL),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = (-0.5).sp
                            ),
                            color = if (totalPnL >= 0) ChartGreen else ChartRed,
                            modifier = Modifier.testTag("net_pnl_text")
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(62.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = DividerGray.copy(alpha = 0.12f),
                                radius = size.minDimension / 2
                            )
                            drawArc(
                                color = DividerGray.copy(alpha = 0.6f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = if (winRate >= 50.0) ChartGreen else NeonBlue,
                                startAngle = -90f,
                                sweepAngle = (winRate * 3.6).toFloat(),
                                useCenter = false,
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "${winRate.toInt()}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "WIN VALUE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                        color = SoftText
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DividerGray)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SubCardBackground)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    MetricMiniItem(
                        label = "PROFIT FACTOR",
                        value = if (profitFactor.isInfinite()) "∞" else String.format(Locale.US, "%.2f", profitFactor),
                        valueColor = if (profitFactor >= 1.5) ChartGreen else if (profitFactor >= 1.0) TextWhite else ChartRed
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SubCardBackground)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    MetricMiniItem(
                        label = "TOTAL PLACED",
                        value = totalTrades.toString(),
                        valueColor = TextWhite
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SubCardBackground)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    MetricMiniItem(
                        label = "OPEN ENTRIES",
                        value = trades.filter { it.status == "Open" }.size.toString(),
                        valueColor = NeonBlue
                    )
                }
            }
        }
    }
}

@Composable
fun MetricMiniItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = SoftText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            ),
            color = valueColor,
            maxLines = 1
        )
    }
}

@Composable
fun PerformanceTab(trades: List<TradeEntity>, viewModel: TradeViewModel) {
    val closedTrades = trades.filter { it.status == "Closed" }.sortedBy { it.entryDate }
    var showAddSetupDialog by remember { mutableStateOf(false) }

    if (showAddSetupDialog) {
        var newSetupName by remember { mutableStateOf("") }
        var inputError by remember { mutableStateOf<String?>(null) }
        
        AlertDialog(
            onDismissRequest = { showAddSetupDialog = false },
            containerColor = CardGray,
            title = {
                Text(
                    "ADD PLAYBOOK SETUP",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    color = TextWhite
                )
            },
            text = {
                Column {
                    Text(
                        "Enter the name of your custom playbook strategy:",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = newSetupName,
                        onValueChange = { newSetupName = it },
                        placeholder = { Text("e.g. Gaps and Go", color = SoftText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = DividerGray
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (inputError != null) {
                        Text(
                            text = inputError!!,
                            color = ChartRed,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = newSetupName.trim()
                        if (clean.isEmpty()) {
                            inputError = "Setup name cannot be empty."
                        } else {
                            viewModel.addCustomSetup(clean)
                            showAddSetupDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChartGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ADD SETUP", fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSetupDialog = false }) {
                    Text("CANCEL", color = SoftText, fontFamily = FontFamily.Monospace)
                }
            }
        )
    }

    if (closedTrades.isEmpty()) {
        EmptyStateView(
            icon = Icons.Filled.Info,
            title = "No closed trades recorded",
            subtitle = "Once you complete and close trades, dynamic performance metrics, equity growth curves, setup metrics, and emotional leak correlation insights will automatically populate here."
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        // Equity Growth Curve
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(
                    width = 1.dp,
                    color = DividerGray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(20.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = CardGray),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "CUMULATIVE P&L GROWTH CURVE ($)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    ),
                    color = SoftText
                )
                Spacer(modifier = Modifier.height(18.dp))
                AnimatedCumulativePnLChart(closedTrades = closedTrades)
            }
        }

        // Setup performance analysis
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(
                    width = 1.dp,
                    color = DividerGray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(20.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = CardGray),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SETUP STRATEGY LABS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = SoftText
                    )
                    
                    IconButton(
                        onClick = { showAddSetupDialog = true },
                        modifier = Modifier
                            .background(SubCardBackground, CircleShape)
                            .size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Custom Setup",
                            tint = NeonBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                val setupGroups = closedTrades.groupBy { it.setup }
                setupGroups.forEach { (setup, list) ->
                    val avgPnL = list.sumOf { it.pnl } / list.size
                    val setupWins = list.filter { it.pnl > 0 }.size
                    val setupWinRate = (setupWins.toDouble() / list.size) * 100

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = setup,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${list.size} Trades | ${setupWinRate.toInt()}% Win Rate",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = SoftText
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatCurrency(list.sumOf { it.pnl }),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (list.sumOf { it.pnl } >= 0) ChartGreen else ChartRed
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Avg: " + formatCurrency(avgPnL),
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                                color = SoftText
                            )
                        }
                    }
                    HorizontalDivider(color = DividerGray)
                }
            }
        }

        // Psychological leak identification
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(
                    width = 1.dp,
                    color = DividerGray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(20.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = CardGray),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "EMOTION & COHESION REPORT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    ),
                    color = SoftText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Identifies sub-conscious cognitive behaviors and positive anchors.",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = SoftText
                )
                Spacer(modifier = Modifier.height(18.dp))

                val emotionalGroups = closedTrades.groupBy { it.emotion }
                emotionalGroups.forEach { (emotion, list) ->
                    val totalEmotionPnL = list.sumOf { it.pnl }
                    val avgEmotionPnL = totalEmotionPnL / list.size

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (emotion) {
                                            "FOMO", "FOMO (Entry)", "FOMO (Exit)" -> ChartRed
                                            "Greedy" -> AccentOrange
                                            "Fearful (Anxious)", "Anxious", "Impatient" -> Color.Yellow
                                            "Disciplined", "Calm & Patient" -> ChartGreen
                                            else -> SoftText
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = emotion,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                                color = TextWhite
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val count = list.size
                            Text(
                                text = "$count ${if (count == 1) "trade" else "trades"} (Avg ${formatCurrency(avgEmotionPnL)})",
                                style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                color = if (totalEmotionPnL >= 0) ChartGreen else ChartRed
                            )
                        }
                    }
                    HorizontalDivider(color = DividerGray)
                }
            }
        }

        // Win Loss Calendar List
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 56.dp)
                .border(
                    width = 1.dp,
                    color = DividerGray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(20.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = CardGray),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CHRONOLOGICAL P&L OUTCOMES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = SoftText
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(closedTrades.takeLast(14)) { trade ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (trade.pnl >= 0) ChartGreen.copy(alpha = 0.2f) else ChartRed.copy(alpha = 0.2f))
                                    .border(
                                        width = 1.dp,
                                        color = if (trade.pnl >= 0) ChartGreen else ChartRed,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (trade.pnl >= 0) "W" else "L",
                                    fontWeight = FontWeight.Bold,
                                    color = if (trade.pnl >= 0) ChartGreen else ChartRed,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = trade.symbol,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                color = SoftText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedCumulativePnLChart(closedTrades: List<TradeEntity>) {
    val points = remember(closedTrades) {
        val list = mutableListOf<Double>()
        var currentSum = 0.0
        list.add(0.0)
        closedTrades.forEach {
            currentSum += it.pnl
            list.add(currentSum)
        }
        list
    }

    val maxPnl = points.maxOrNull() ?: 1.0
    val minPnl = points.minOrNull() ?: 0.0
    val pnlRange = (maxPnl - minPnl).let { if (it == 0.0) 100.0 else it }

    var activeNodeIndex by remember { mutableStateOf<Int?>(null) }

    val paddingLeft = 40f
    val paddingRight = 40f
    val paddingTop = 45f
    val paddingBottom = 45f

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        val usableWidth = widthPx - (paddingLeft + paddingRight)
        val usableHeight = heightPx - (paddingTop + paddingBottom)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(points) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (points.size > 1 && usableWidth > 0) {
                                val stepX = usableWidth / (points.size - 1)
                                val index = ((offset.x - paddingLeft) / stepX).roundToInt().coerceIn(0, points.size - 1)
                                activeNodeIndex = index
                            }
                        },
                        onDrag = { change, _ ->
                            if (points.size > 1 && usableWidth > 0) {
                                val stepX = usableWidth / (points.size - 1)
                                val index = ((change.position.x - paddingLeft) / stepX).roundToInt().coerceIn(0, points.size - 1)
                                activeNodeIndex = index
                            }
                        },
                        onDragEnd = { activeNodeIndex = null },
                        onDragCancel = { activeNodeIndex = null }
                    )
                }
                .pointerInput(points) {
                    detectTapGestures(
                        onPress = { offset ->
                            if (points.size > 1 && usableWidth > 0) {
                                val stepX = usableWidth / (points.size - 1)
                                val index = ((offset.x - paddingLeft) / stepX).roundToInt().coerceIn(0, points.size - 1)
                                activeNodeIndex = index
                                tryAwaitRelease()
                                activeNodeIndex = null
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 1. Grid overlay behind the path
                val gridColor = DividerGray.copy(alpha = 0.25f)
                val horizontalGridCount = 4
                for (r in 0 until horizontalGridCount) {
                    val y = paddingTop + r * (usableHeight / (horizontalGridCount - 1))
                    drawLine(
                        color = gridColor,
                        start = Offset(paddingLeft, y),
                        end = Offset(widthPx - paddingRight, y),
                        strokeWidth = 2f
                    )
                }
                
                val verticalGridCount = if (points.size in 2..8) points.size else 5
                for (c in 0 until verticalGridCount) {
                    val x = paddingLeft + c * (usableWidth / (verticalGridCount - 1))
                    drawLine(
                        color = gridColor,
                        start = Offset(x, paddingTop),
                        end = Offset(x, heightPx - paddingBottom),
                        strokeWidth = 2f
                    )
                }

                // 2. Base zero line
                if (minPnl < 0.0 && maxPnl > 0.0) {
                    val zeroY = heightPx - paddingBottom - (((0.0 - minPnl) / pnlRange) * usableHeight).toFloat()
                    drawLine(
                        color = DividerGray,
                        start = Offset(paddingLeft, zeroY),
                        end = Offset(widthPx - paddingRight, zeroY),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }

                if (points.size > 1) {
                    val stepX = usableWidth / (points.size - 1)
                    val path = Path()
                    val fillPath = Path()

                    val startX = paddingLeft
                    val startY = heightPx - paddingBottom - (((points[0] - minPnl) / pnlRange) * usableHeight).toFloat()
                    path.moveTo(startX, startY)
                    fillPath.moveTo(startX, heightPx - paddingBottom)
                    fillPath.lineTo(startX, startY)

                    for (i in 1 until points.size) {
                        val pX = paddingLeft + i * stepX
                        val pY = heightPx - paddingBottom - (((points[i] - minPnl) / pnlRange) * usableHeight).toFloat()
                        path.lineTo(pX, pY)
                        fillPath.lineTo(pX, pY)
                    }

                    fillPath.lineTo(paddingLeft + (points.size - 1) * stepX, heightPx - paddingBottom)
                    fillPath.close()

                    val fillBrush = Brush.verticalGradient(
                        colors = listOf(
                            (if (points.last() >= 0.0) ChartGreen else ChartRed).copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                    drawPath(path = fillPath, brush = fillBrush)

                    // Neon shadow glow line
                    drawPath(
                        path = path,
                        color = (if (points.last() >= 0.0) ChartGreen else ChartRed).copy(alpha = 0.28f),
                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                    )

                    // High intensity fiber line
                    drawPath(
                        path = path,
                        color = if (points.last() >= 0.0) ChartGreen else ChartRed,
                        style = Stroke(width = 5.6f, cap = StrokeCap.Round)
                    )

                    // Draw standard nodes conditionally to avoid screen clutter
                    if (points.size < 25) {
                        for (i in points.indices) {
                            val nodeX = paddingLeft + i * stepX
                            val nodeY = heightPx - paddingBottom - (((points[i] - minPnl) / pnlRange) * usableHeight).toFloat()
                            drawCircle(
                                color = Color.White,
                                radius = 4.5f,
                                center = Offset(nodeX, nodeY)
                            )
                            drawCircle(
                                color = if (points[i] >= 0.0) ChartGreen else ChartRed,
                                radius = 2.5f,
                                center = Offset(nodeX, nodeY)
                            )
                        }
                    } else {
                        // Always draw first and last points cleanly
                        listOf(0, points.size - 1).forEach { idx ->
                            val nodeX = paddingLeft + idx * stepX
                            val nodeY = heightPx - paddingBottom - (((points[idx] - minPnl) / pnlRange) * usableHeight).toFloat()
                            drawCircle(
                                color = Color.White,
                                radius = 5f,
                                center = Offset(nodeX, nodeY)
                            )
                            drawCircle(
                                color = if (points[idx] >= 0.0) ChartGreen else ChartRed,
                                radius = 3f,
                                center = Offset(nodeX, nodeY)
                            )
                        }
                    }

                    // 3. Draw highlighted index highlights
                    activeNodeIndex?.let { selectedIdx ->
                        val highlightX = paddingLeft + selectedIdx * stepX
                        val highlightY = heightPx - paddingBottom - (((points[selectedIdx] - minPnl) / pnlRange) * usableHeight).toFloat()

                        // Vertical tracking line
                        drawLine(
                            color = NeonBlue.copy(alpha = 0.6f),
                            start = Offset(highlightX, paddingTop),
                            end = Offset(highlightX, heightPx - paddingBottom),
                            strokeWidth = 3f
                        )

                        // Highlight node larger
                        drawCircle(
                            color = NeonBlue.copy(alpha = 0.4f),
                            radius = 16f,
                            center = Offset(highlightX, highlightY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 8f,
                            center = Offset(highlightX, highlightY)
                        )
                        drawCircle(
                            color = if (points[selectedIdx] >= 0.0) ChartGreen else ChartRed,
                            radius = 5f,
                            center = Offset(highlightX, highlightY)
                        )
                    }
                }
            }

            // 4. Hover Tooltip Overlay
            activeNodeIndex?.let { selectedIdx ->
                val pnlValue = points[selectedIdx]
                val trade = if (selectedIdx > 0 && selectedIdx - 1 < closedTrades.size) closedTrades[selectedIdx - 1] else null
                val isWin = pnlValue >= 0.0

                val labelText = if (trade != null) {
                    "${trade.symbol}: ${formatCurrency(trade.pnl)} (Cum. ${formatCurrency(pnlValue)})"
                } else {
                    "Start Portfolio PnL: $0.00"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardGray),
                        border = BorderStroke(1.dp, if (isWin) ChartGreen else ChartRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.shadow(8.dp, RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isWin) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isWin) ChartGreen else ChartRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (selectedIdx > 0) "Trade #$selectedIdx | $labelText" else labelText,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LedgerTab(
    trades: List<TradeEntity>,
    onDeleteTrade: (TradeEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") }
    var filterTradeType by remember { mutableStateOf("ALL") }

    val filteredTrades = trades.filter { trade ->
        val matchesQuery = trade.symbol.contains(searchQuery, ignoreCase = true) ||
                trade.setup.contains(searchQuery, ignoreCase = true) ||
                trade.notes.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (filterType) {
            "OPEN" -> trade.status == "Open"
            "CLOSED" -> trade.status == "Closed"
            "BUY" -> trade.side == "Long"
            "SELL" -> trade.side == "Short"
            else -> true
        }

        val matchesTradeType = when (filterTradeType) {
            "SWING" -> trade.tradeType.equals("Swing", ignoreCase = true)
            "INTRADAY" -> trade.tradeType.equals("Intraday", ignoreCase = true)
            "POSITIONAL" -> trade.tradeType.equals("Positional", ignoreCase = true)
            else -> true
        }

        matchesQuery && matchesFilter && matchesTradeType
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CarbonBlack)
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Symbol, Setup, Notes...", color = SoftText) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = SoftText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = ChartGreen,
                    unfocusedBorderColor = DividerGray,
                    focusedContainerColor = SlateBackground,
                    unfocusedContainerColor = SlateBackground
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ledger_search_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-status layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL", "OPEN", "CLOSED", "BUY", "SELL").forEach { label ->
                    val isSelected = filterType == label
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ChartGreen else SlateBackground)
                            .clickable { filterType = label }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
                            color = if (isSelected) Color.Black else SoftText,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Trade Duration/Type layout requested: Swing, Intraday, Positional
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TYPE: ",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                    color = SoftText,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL", "SWING", "INTRADAY", "POSITIONAL").forEach { label ->
                        val isSelected = filterTradeType == label
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) NeonBlue else SlateBackground)
                                .clickable { filterTradeType = label }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                                color = if (isSelected) Color.Black else SoftText,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        val context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredTrades.size} Trades Listed",
                style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = SoftText
            )
            
            Button(
                onClick = {
                    if (trades.isEmpty()) {
                        Toast.makeText(context, "No trades to export", Toast.LENGTH_SHORT).show()
                    } else {
                        val uri = downloadTradeLogsExcel(context, trades)
                        if (uri != null) {
                            Toast.makeText(context, "Exported successfully to Downloads/TradingJournal", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Failed to export logs", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChartGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp).testTag("export_excel_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share, 
                    contentDescription = "Export Excel",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DOWNLOAD EXCEL",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    color = Color.Black
                )
            }
        }

        if (filteredTrades.isEmpty()) {
            EmptyStateView(
                icon = Icons.Filled.Info,
                title = "No matching trades found",
                subtitle = "Try altering your key search string or selecting another ledger status tab filter."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("ledger_trade_list")
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredTrades, key = { it.id }) { trade ->
                    TradeCard(trade = trade, onDelete = { onDeleteTrade(trade) })
                }
            }
        }
    }
}

@Composable
fun TradeCard(trade: TradeEntity, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .border(
                width = 1.dp,
                color = DividerGray.copy(alpha = 0.35f),
                shape = RoundedCornerShape(24.dp)
            )
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(24.dp))
            .testTag("trade_card_${trade.id}"),
        colors = CardDefaults.cardColors(containerColor = CardGray),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = trade.symbol,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (trade.side == "Long") NeonBlue.copy(alpha = 0.2f) else AccentOrange.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = trade.side.uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black),
                                color = if (trade.side == "Long") NeonBlue else AccentOrange
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Opened " + SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(trade.entryDate)),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = SoftText
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (trade.status == "Open") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "ACTIVE OPEN",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                                color = NeonBlue
                            )
                        }
                    } else {
                        Text(
                            text = formatCurrency(trade.pnl),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (trade.pnl >= 0) ChartGreen else ChartRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SubCardBackground)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🎯 Setup: ${trade.setup}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SubCardBackground)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🧠 Emotion: ${trade.emotion}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = SoftText
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SubCardBackground)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "⏳ Type: ${trade.tradeType}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = NeonBlue
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = DividerGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            LabelValueRow(label = "Entry Price:", value = formatPrice(trade.entryPrice))
                            LabelValueRow(label = "Exit Price:", value = trade.exitPrice?.let { formatPrice(it) } ?: "N/A")
                            LabelValueRow(label = "Quantity:", value = trade.quantity.toString())
                            LabelValueRow(label = "Fees & Comm:", value = formatCurrency(-trade.commissions))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            LabelValueRow(label = "Stop Loss:", value = trade.stopLoss?.let { formatPrice(it) } ?: "None")
                            LabelValueRow(label = "Take Profit:", value = trade.takeProfit?.let { formatPrice(it) } ?: "None")
                            LabelValueRow(label = "Planned R:R:", value = String.format(Locale.US, "%.2f", trade.targetRiskReward))
                            LabelValueRow(label = "Actual R-Mult:", value = String.format(Locale.US, "%.2f", trade.actualRMultiple))
                        }
                    }

                    if (trade.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateBackground, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "TRADING THESIS / NOTES:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SoftText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = trade.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }

                    if (!trade.imageUri.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column {
                            Text(
                                text = "ATTACHED CHART SETUP SCREENSHOT:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = SoftText,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(trade.imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Chart Setup Screenshot",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, DividerGray, RoundedCornerShape(8.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.testTag("delete_trade_${trade.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Log",
                                tint = ChartRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LabelValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = SoftText)
        Text(text = value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
    }
}

@Composable
fun GeminiAuraBackground(
    isDark: Boolean,
    accentColorName: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidGlowMesh")
    
    // Master timers for smooth liquid wave swaying
    val masterPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * kotlin.math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "masterPhase"
    )

    val alternatePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * kotlin.math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alternatePhase"
    )

    // Vibrant themed color arrays for liquid waves matching the user choices with 5 distinct colors (more than 3 colors requested!)
    val colors = when (accentColorName) {
        "Classic Ice" -> {
            if (isDark) {
                listOf(
                    Color(0xFF00B4DB), // Vibrant Ice Cyan
                    Color(0xFF00D2FF), // Vivid Glow Cyan
                    Color(0xFF1E40AF), // Royal Indigo Deep
                    Color(0xFF6366F1), // Electric Violet
                    Color(0xFFD946EF)  // Intense Pink Aurora
                )
            } else {
                listOf(
                    Color(0xFF93C5FD), // Soft Sky Blue
                    Color(0xFFC7D2FE), // Cool Lavender
                    Color(0xFFE0F2FE), // Glacier Mint
                    Color(0xFFDBEAFE), // Lavender Slate
                    Color(0xFFFBCFE8)  // Pastel Candy
                )
            }
        }
        "Emerald Mint" -> {
            if (isDark) {
                listOf(
                    Color(0xFF10B981), // Cyber Emerald Green
                    Color(0xFF34D399), // Neon Seafoam Green
                    Color(0xFF0D9488), // Oceanic Teal
                    Color(0xFF059669), // Shadow Mint
                    Color(0xFF84CC16)  // Citrus Lime Pop
                )
            } else {
                listOf(
                    Color(0xFFA7F3D0), // Soft Mint Greens
                    Color(0xFFD1FAE5), // Sage Chalk Green
                    Color(0xFF6EE7B7), // Minty Lime
                    Color(0xFFA7F3D0), // Serene Spring
                    Color(0xFFF3F4F6)  // Light Frost
                )
            }
        }
        "Sunset Fire" -> {
            if (isDark) {
                listOf(
                    Color(0xFFFF9F1C), // Sunfire Gold
                    Color(0xFFF97316), // Vivid Coral Orange
                    Color(0xFFF43F5E), // Intense Sunset Rose
                    Color(0xFFEC4899), // Shimmering Rose
                    Color(0xFFEF4444)  // Intense Flare Red
                )
            } else {
                listOf(
                    Color(0xFFFDE68A), // Cream Honey
                    Color(0xFFFED7AA), // Soft Apricot
                    Color(0xFFFECDD3), // Blush Peony
                    Color(0xFFFFE4E6), // Sweet Sunset Sky
                    Color(0xFFFEE2E2)  // Soft Rose water
                )
            }
        }
        "Retro Candy" -> {
            if (isDark) {
                listOf(
                    Color(0xFFEC4899), // Neon Bubblegum Pink
                    Color(0xFFD946EF), // Fuchsia Laser Glow
                    Color(0xFF8B5CF6), // Royal Ultraviolet
                    Color(0xFFF43F5E), // Bright Crimson Rose
                    Color(0xFF22D3EE)  // Electric Cyan pop
                )
            } else {
                listOf(
                    Color(0xFFFBCFE8), // Pastel Cotton Candy
                    Color(0xFFFCE7F3), // Sugar Pink Shimmer
                    Color(0xFFDDD6FE), // Lucid Lavender Orchid
                    Color(0xFFFEE2E2), // Cherry Milk
                    Color(0xFFE0F2FE)  // Powder Blue
                )
            }
        }
        "Electric Orchid" -> {
            if (isDark) {
                listOf(
                    Color(0xFF8B5CF6), // Laser Violet Purple
                    Color(0xFFD946EF), // Cyber Fuchsia Orchid
                    Color(0xFF4F46E5), // Neon Royal Indigo
                    Color(0xFF7E22CE), // Vibrant Plum Purple
                    Color(0xFF06B6D4)  // Electro Cyan Bright
                )
            } else {
                listOf(
                    Color(0xFFE9D5FF), // Dreamy Pastel Orchid
                    Color(0xFFDDD6FE), // Lavender Sky Petal
                    Color(0xFFFCE7F3), // Velvet Shimmer Rose
                    Color(0xFFF3E8FF), // Delicate Lilac Blue
                    Color(0xFFEFF6FF)  // Fresh Mist White
                )
            }
        }
        else -> {
            if (isDark) {
                listOf(
                    Color(0xFF00B4DB),
                    Color(0xFF00D2FF),
                    Color(0xFF1E40AF),
                    Color(0xFF6366F1),
                    Color(0xFFD946EF)
                )
            } else {
                listOf(
                    Color(0xFF93C5FD),
                    Color(0xFFC7D2FE),
                    Color(0xFFE0F2FE),
                    Color(0xFFDBEAFE),
                    Color(0xFFFBCFE8)
                )
            }
        }
    }

    Box(
        modifier = modifier
            .background(currentPaletteState.background)
            .drawBehind {
                val width = size.width
                val height = size.height

                val waveColors = colors

                // Glow Core 1: Dynamic Top-anchored high-vibrancy fluid pool (using waveColors[0])
                val c1x = width * (0.50f + 0.22f * sin((masterPhase * 1.1f + 1.5f).toDouble()).toFloat())
                val c1y = height * (0.18f + 0.08f * cos((alternatePhase * 0.8f + 0.5f).toDouble()).toFloat())
                val c1Radius = width * (0.85f + 0.15f * sin((masterPhase * 0.7f).toDouble()).toFloat())
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            waveColors[0].copy(alpha = if (isDark) 0.28f else 0.44f),
                            waveColors[1].copy(alpha = if (isDark) 0.14f else 0.28f),
                            Color.Transparent
                        ),
                        center = Offset(c1x, c1y),
                        radius = c1Radius
                    ),
                    center = Offset(c1x, c1y),
                    radius = c1Radius
                )

                // Glow Core 2: Dynamic Upper-mid Left fluid swirl (using waveColors[1])
                val c2x = width * (0.22f + 0.18f * cos((masterPhase * 0.9f).toDouble()).toFloat())
                val c2y = height * (0.38f + 0.12f * sin((alternatePhase * 1.1f + 2.0f).toDouble()).toFloat())
                val c2Radius = width * (0.80f + 0.12f * cos((alternatePhase * 0.9f).toDouble()).toFloat())
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            waveColors[1].copy(alpha = if (isDark) 0.24f else 0.38f),
                            waveColors[2].copy(alpha = if (isDark) 0.11f else 0.24f),
                            Color.Transparent
                        ),
                        center = Offset(c2x, c2y),
                        radius = c2Radius
                    ),
                    center = Offset(c2x, c2y),
                    radius = c2Radius
                )

                // Glow Core 3: Rolling Center-right fluid swell (using waveColors[2])
                val c3x = width * (0.78f + 0.16f * sin((masterPhase * 1.3f).toDouble()).toFloat())
                val c3y = height * (0.54f + 0.10f * cos((alternatePhase * 1.2f + 1.0f).toDouble()).toFloat())
                val c3Radius = width * (0.85f + 0.15f * sin((masterPhase * 0.8f + 3.0f).toDouble()).toFloat())
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            waveColors[2].copy(alpha = if (isDark) 0.24f else 0.38f),
                            waveColors[3].copy(alpha = if (isDark) 0.10f else 0.22f),
                            Color.Transparent
                        ),
                        center = Offset(c3x, c3y),
                        radius = c3Radius
                    ),
                    center = Offset(c3x, c3y),
                    radius = c3Radius
                )

                // Glow Core 4: Broad lower-background swell keeping background dynamic (using waveColors[3])
                val c4x = width * (0.45f + 0.25f * cos((alternatePhase * 0.7f + 3.5f).toDouble()).toFloat())
                val c4y = height * (0.72f + 0.11f * sin((masterPhase * 1.0f + 0.8f).toDouble()).toFloat())
                val c4Radius = width * (0.95f + 0.20f * cos((alternatePhase * 1.1f).toDouble()).toFloat())
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            waveColors[3].copy(alpha = if (isDark) 0.20f else 0.34f),
                            waveColors[4].copy(alpha = if (isDark) 0.08f else 0.18f),
                            Color.Transparent
                        ),
                        center = Offset(c4x, c4y),
                        radius = c4Radius
                    ),
                    center = Offset(c4x, c4y),
                    radius = c4Radius
                )

                // Glow Core 5: Bottom Far Right deep accent drift (using waveColors[4])
                val c5x = width * (0.80f + 0.15f * sin((masterPhase * 0.8f).toDouble()).toFloat())
                val c5y = height * (0.85f + 0.10f * cos((alternatePhase * 0.9f).toDouble()).toFloat())
                val c5Radius = width * (0.75f + 0.10f * sin((masterPhase * 1.2f).toDouble()).toFloat())
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            waveColors[4].copy(alpha = if (isDark) 0.16f else 0.32f),
                            waveColors[0].copy(alpha = if (isDark) 0.05f else 0.14f),
                            Color.Transparent
                        ),
                        center = Offset(c5x, c5y),
                        radius = c5Radius
                    ),
                    center = Offset(c5x, c5y),
                    radius = c5Radius
                )

                // Very subtle ambient full-screen overlay mask guaranteeing text visibility while letting the gorgeous multi-color flow breathe over the ENTIRE full screen
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            currentPaletteState.background.copy(alpha = if (isDark) 0.38f else 0.15f),
                            currentPaletteState.background.copy(alpha = if (isDark) 0.24f else 0.08f),
                            currentPaletteState.background.copy(alpha = if (isDark) 0.42f else 0.20f)
                        ),
                        startY = 0f,
                        endY = height
                    )
                )
            }
    ) {
        content()
    }
}

@Composable
fun AiCoachTab(
    insights: String,
    isGenerating: Boolean,
    onGenerate: () -> Unit,
    screenshotInsights: String,
    isAnalyzingScreenshot: Boolean,
    onAnalyzeScreenshot: (Bitmap, String?) -> Unit,
    onClearScreenshotInsights: () -> Unit
) {
    val context = LocalContext.current
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var customUserPrompt by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                selectedBitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            selectedImageUri = null
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required to snap screenshots", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Behavioral Coach Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x3C1B1D20)),
                border = BorderStroke(0.8.dp, Color(0x33FFFFFF)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0x1F7E22CE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Mindset Coach",
                            tint = AccentOrange,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "BEHAVIORAL RISK & QUANT AUDIT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scans past trades to diagnose emotional leaks, target stop loss behavior, and formulate tactical playbook shifts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onGenerate,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isGenerating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_insights_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
                        } else {
                            Text(
                                "RUN RISK PROFILE AUDIT",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Coach output
            if (insights.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x50232529)),
                    border = BorderStroke(0.8.dp, Color(0x22FFFFFF)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "PSYCHOLOGICAL STATS OVERVIEW",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp),
                                color = AccentOrange
                            )
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "AI Actioned",
                                tint = AccentOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = insights,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp, fontFamily = FontFamily.SansSerif),
                            color = Color.White
                        )
                    }
                }
            }

            // GEMINI VISION CHART ANALYSIS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x3C1B1D20)),
                border = BorderStroke(0.8.dp, Color(0x33FFFFFF)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0x1F06B6D4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Chart Scanner Icon",
                            tint = NeonBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "GEMINI CHART SETUP VISION SCANNER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Upload a screenshot of your active trading setup to inspect candle trends, support confluences, entry accuracy, and stop spacing using Gemini Vision.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedBitmap == null) {
                        // Image Upload Buttons Side-by-Side
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    galleryLauncher.launch("image/*")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, DividerGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Upload from gallery", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "GET GALLERY PHOTO", 
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = {
                                    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                        cameraLauncher.launch(null)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, DividerGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Capture with Camera", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "SNAP WITH CAMERA", 
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        // Display selected Image preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, NeonBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = selectedBitmap,
                                contentDescription = "Active chart setup scan preview",
                                modifier = Modifier.fillMaxSize()
                            )

                            // Quick clean overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .clickable {
                                        selectedBitmap = null
                                        selectedImageUri = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear selected image", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Query input
                        OutlinedTextField(
                            value = customUserPrompt,
                            onValueChange = { customUserPrompt = it },
                            placeholder = { Text("Ask Gemini Vision anything specific (e.g., 'Is this structure ready for breakout?')", color = SoftText, fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = DividerGray,
                                focusedContainerColor = SlateBackground,
                                unfocusedContainerColor = SlateBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val finalPrompt = customUserPrompt.trim().ifEmpty { null }
                                    selectedBitmap?.let { bitmap ->
                                        onAnalyzeScreenshot(bitmap, finalPrompt)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isAnalyzingScreenshot,
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(48.dp)
                            ) {
                                if (isAnalyzingScreenshot) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "ANALYZE SETUP CONFLUENCE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                        color = Color.Black
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    selectedBitmap = null
                                    selectedImageUri = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E33)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(
                                    "CLEAR",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Gemini Vision Analysis Output
            if (screenshotInsights.isNotEmpty()) {
                Card(
                     modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 60.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF070709)),
                    border = BorderStroke(1.2.dp, NeonBlue.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = NeonBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "GEMINI CHART VISION REPORT",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp),
                                    color = NeonBlue
                                )
                            }
                            IconButton(onClick = onClearScreenshotInsights, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear analysis history",
                                    tint = SoftText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = screenshotInsights,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp, fontFamily = FontFamily.Monospace),
                            color = Color.White
                        )
                    }
                }
            } else {
                if (selectedBitmap == null) {
                    // Default Tips List Card when nothing is scanned
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 60.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x221B1D20)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "🧠 Playbook Entry Strategy Hacks",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            PsychologyTipItem(
                                title = "Wait for Candle Close Confirmations:",
                                body = "Never enter a trend breakout trade on an active candlestick wick. Wait for the H1/H4 body to settle entirely outside of structural consolidations."
                            )
                            PsychologyTipItem(
                                title = "Average True Range (ATR) Stop Spacing:",
                                body = "Calibrate stop losses by subtracting 1.5x of the instrument’s ATR from your structural swing low to survive noise wicks."
                            )
                            PsychologyTipItem(
                                title = "The 2-R Loss Threshold Shield:",
                                body = "If you realize more than 2 full R-multiple unit losses in a single day, lock your execution terminal entirely for 12 hours."
                            )
                            PsychologyTipItem(
                                title = "Cognitive Breathing Buffer:",
                                body = "Take 3 physiological sighs (double inhale, long exhale) before checking chart ticks immediately post-exit."
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun PsychologyTipItem(title: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AccentOrange)
        Text(text = body, style = MaterialTheme.typography.bodySmall, color = SoftText)
    }
}

@Composable
fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DividerGray,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = SoftText,
            textAlign = TextAlign.Center
        )
    }
}

// Log dialog with form fields to prevent jumping layouts
@Composable
fun LogTradeDialog(
    customSetups: List<String>,
    onAddCustomSetup: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (
        symbol: String,
        side: String,
        status: String,
        entryPrice: Double,
        exitPrice: Double?,
        quantity: Double,
        commissions: Double,
        stopLoss: Double?,
        takeProfit: Double?,
        setup: String,
        emotion: String,
        notes: String,
        imageUri: String?,
        tradeType: String
    ) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var side by remember { mutableStateOf("Long") }
    var status by remember { mutableStateOf("Closed") }
    var entryPrice by remember { mutableStateOf("") }
    var exitPrice by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var commissions by remember { mutableStateOf("") }
    var stopLoss by remember { mutableStateOf("") }
    var takeProfit by remember { mutableStateOf("") }
    var setup by remember { mutableStateOf("Support Bounce") }
    var emotion by remember { mutableStateOf("Disciplined") }
    var notes by remember { mutableStateOf("") }
    var imageUriState by remember { mutableStateOf<String?>(null) }
    var tradeTypeState by remember { mutableStateOf("Swing") }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val path = saveBitmapToInternalStorage(context, bitmap)
            if (path != null) {
                imageUriState = path
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required to snap screenshots", Toast.LENGTH_SHORT).show()
        }
    }

    var errors by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardGray,
        confirmButton = {
            Button(
                onClick = {
                    if (symbol.isEmpty()) {
                        errors = "Ticker Symbol cannot be empty."
                        return@Button
                    }
                    val ep = entryPrice.toDoubleOrNull()
                    if (ep == null || ep <= 0) {
                        errors = "Valid positive Entry Price is required."
                        return@Button
                    }
                    val qty = quantity.toDoubleOrNull()
                    if (qty == null || qty <= 0) {
                        errors = "Valid positive Position Quantity is required."
                        return@Button
                    }
                    val comm = commissions.toDoubleOrNull() ?: 0.0

                    var xp: Double? = null
                    if (status == "Closed") {
                        val parsedXp = exitPrice.toDoubleOrNull()
                        if (parsedXp == null || parsedXp <= 0) {
                            errors = "Exit Price is required for Closed trades."
                            return@Button
                        }
                        xp = parsedXp
                    }

                    val sl = stopLoss.toDoubleOrNull()
                    val tp = takeProfit.toDoubleOrNull()

                    onSave(
                        symbol, side, status, ep, xp, qty, comm, sl, tp, setup, emotion, notes, imageUriState, tradeTypeState
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChartGreen),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("dialog_save_button")
            ) {
                Text("SAVE TRANSACTION", fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = FontFamily.Monospace)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = SoftText, fontFamily = FontFamily.Monospace)
            }
        },
        title = {
            Text(
                "LOG TRANSACTION LOG",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errors != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ChartRed.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(width = 1.dp, color = ChartRed, shape = RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(text = errors!!, style = MaterialTheme.typography.bodySmall, color = ChartRed)
                    }
                }

                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = { Text("Ticker Symbol (e.g. BTCUSD, AAPL)") },
                    singleLine = true,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("add_trade_symbol_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Long", "Short").forEach { option ->
                        val isSel = side == option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSel) {
                                        if (option == "Long") NeonBlue else AccentOrange
                                    } else DividerGray
                                )
                                .clickable { side = option }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.Black else SoftText,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Closed", "Open").forEach { option ->
                        val isSel = status == option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) ChartGreen else DividerGray)
                                .clickable { status = option }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.Black else SoftText,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = entryPrice,
                        onValueChange = { entryPrice = it },
                        label = { Text("Entry Price ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f).testTag("add_trade_entry_price_input")
                    )
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f).testTag("add_trade_qty_input")
                    )
                }

                if (status == "Closed") {
                    OutlinedTextField(
                        value = exitPrice,
                        onValueChange = { exitPrice = it },
                        label = { Text("Exit Price ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("add_trade_exit_price_input")
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stopLoss,
                        onValueChange = { stopLoss = it },
                        label = { Text("Stop Loss ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = takeProfit,
                        onValueChange = { takeProfit = it },
                        label = { Text("Take Profit ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = commissions,
                    onValueChange = { commissions = it },
                    label = { Text("Commissions & Broker Fees ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                var showAddMiniDialog by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎯 Select Playbook Setup:", style = MaterialTheme.typography.labelSmall, color = SoftText)
                    IconButton(
                        onClick = { showAddMiniDialog = true },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add custom setup",
                            tint = NeonBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                if (showAddMiniDialog) {
                    var newOptionName by remember { mutableStateOf("") }
                    AlertDialog(
                        onDismissRequest = { showAddMiniDialog = false },
                        containerColor = CardGray,
                        title = { Text("QUICK ADD SETUP", style = MaterialTheme.typography.titleMedium, color = TextWhite) },
                        text = {
                            OutlinedTextField(
                                value = newOptionName,
                                onValueChange = { newOptionName = it },
                                placeholder = { Text("e.g. Pivot Break", color = SoftText) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = NeonBlue,
                                    unfocusedBorderColor = DividerGray
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val cleanName = newOptionName.trim()
                                    if (cleanName.isNotEmpty()) {
                                        onAddCustomSetup(cleanName)
                                        setup = cleanName
                                    }
                                    showAddMiniDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ChartGreen),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("ADD", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddMiniDialog = false }) {
                                Text("CANCEL", color = SoftText)
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    val defaultSetups = listOf("Support Bounce", "Bull Flag", "Resistance Breakout", "EMA Crossover", "Mean Reversion", "Impulse Chase")
                    val allSetupsList = defaultSetups + customSetups
                    allSetupsList.forEach { setupOption ->
                        val isSel = setup == setupOption
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) ChartGreen else DividerGray)
                                .clickable { setup = setupOption }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = setupOption, style = MaterialTheme.typography.labelSmall, color = if (isSel) Color.Black else SoftText)
                        }
                    }
                }

                Text("🧠 Select Logging Psych Emotion State:", style = MaterialTheme.typography.labelSmall, color = SoftText)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf("Disciplined", "FOMO", "Greedy", "Anxious", "Impatient", "Calm & Patient").forEach { emotionOption ->
                        val isSel = emotion == emotionOption
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) AccentOrange else DividerGray)
                                .clickable { emotion = emotionOption }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = emotionOption, style = MaterialTheme.typography.labelSmall, color = if (isSel) Color.Black else SoftText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("⏳ Select Trade Type:", style = MaterialTheme.typography.labelSmall, color = SoftText)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Swing", "Intraday", "Positional").forEach { typeOption ->
                        val isSel = tradeTypeState == typeOption
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) NeonBlue else DividerGray)
                                .clickable { tradeTypeState = typeOption }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = typeOption.uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.Black else SoftText,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("📸 Attach Setup Screenshot:", style = MaterialTheme.typography.labelSmall, color = SoftText)
                if (imageUriState == null) {
                    Button(
                        onClick = {
                            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(null)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DividerGray),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("attach_screenshot_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Capture setup", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "SNAP CHART SCREENSHOT", 
                            color = Color.White, 
                            fontFamily = FontFamily.Monospace, 
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateBackground, RoundedCornerShape(8.dp))
                            .border(1.dp, DividerGray, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUriState)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Chart Setup Screenshot",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, DividerGray, RoundedCornerShape(6.dp))
                            )
                            
                            Column {
                                Text("trading_setup.jpg", style = MaterialTheme.typography.labelSmall, color = Color.White, maxLines = 1)
                                Text("Image attached successfully", style = MaterialTheme.typography.bodySmall, color = ChartGreen)
                            }
                        }
                        
                        IconButton(
                            onClick = { imageUriState = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove attached photo", tint = ChartRed)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Trading Thesis & Reminders") },
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                )
            }
        }
    )
}

@Composable
fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = ChartGreen,
    unfocusedLabelColor = SoftText,
    focusedBorderColor = ChartGreen,
    unfocusedBorderColor = DividerGray,
    focusedContainerColor = SlateBackground,
    unfocusedContainerColor = SlateBackground
)

// Currency formatted calculations
fun formatCurrency(amount: Double): String {
    val locale = when (currentCurrencyCode) {
        "EUR" -> Locale.FRANCE
        "GBP" -> Locale.UK
        "INR" -> Locale("en", "IN")
        "JPY" -> Locale.JAPAN
        "CAD" -> Locale.CANADA
        "AUD" -> Locale("en", "AU")
        else -> Locale.US
    }
    val formatter = java.text.NumberFormat.getCurrencyInstance(locale)
    return formatter.format(amount)
}

fun formatPrice(amount: Double): String {
    val symbol = when (currentCurrencyCode) {
        "EUR" -> "€"
        "GBP" -> "£"
        "INR" -> "₹"
        "JPY" -> "¥"
        "CAD" -> "C$"
        "AUD" -> "A$"
        else -> "$"
    }
    return String.format(Locale.US, "%s%.2f", symbol, amount)
}

// Save captured camera photo to internal app files directory
fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
    val file = File(context.filesDir, "setup_${System.currentTimeMillis()}.jpg")
    return try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        file.absolutePath
    } catch (e: Exception) {
        Log.e("SaveBitmap", "Failed to save screenshot", e)
        null
    }
}

// Export custom trade logs to modern, universally compatible CSV format (opens natively in Microsoft Excel / Google Sheets)
fun downloadTradeLogsExcel(context: Context, trades: List<TradeEntity>): Uri? {
    val filename = "trade_logs_${System.currentTimeMillis()}.csv"
    val contentValues = ContentValues().apply {
         put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
         put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
             put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/TradingJournal")
         }
    }
    
    val resolver = context.contentResolver
    // On Android 10+, write to collective Downloads folder safely without needing runtime permission
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues) ?: return null
    
    return try {
        resolver.openOutputStream(uri)?.use { outputStream ->
            java.io.OutputStreamWriter(outputStream).use { writer ->
                // Write CSV headers cleanly
                writer.write("Trade ID,Symbol,Side,Status,Entry Price,Exit Price,Quantity,Commissions,PnL,Entry Date,Exit Date,Stop Loss,Take Profit,Setup,Emotion,Trade Type,Notes\n")
                // Loop and write individual trade items
                trades.forEach { trade ->
                    val entryDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(trade.entryDate))
                    val exitDateStr = trade.exitDate?.let { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(it)) } ?: "N/A"
                    val exitPriceStr = trade.exitPrice?.toString() ?: "N/A"
                    val slStr = trade.stopLoss?.toString() ?: "N/A"
                    val tpStr = trade.takeProfit?.toString() ?: "N/A"
                    
                    val cleanNotes = trade.notes.replace("\"", "\"\"").replace("\n", " ")
                    val cleanSetup = trade.setup.replace("\"", "\"\"")
                    val cleanTradeType = trade.tradeType.replace("\"", "\"\"")
                    
                    writer.write(
                        "${trade.id},\"${trade.symbol}\",\"${trade.side}\",\"${trade.status}\",${trade.entryPrice},${exitPriceStr},${trade.quantity},${trade.commissions},${trade.pnl},\"$entryDateStr\",\"$exitDateStr\",$slStr,$tpStr,\"$cleanSetup\",\"${trade.emotion}\",\"$cleanTradeType\",\"$cleanNotes\"\n"
                    )
                }
            }
        }
        uri
    } catch (e: Exception) {
         Log.e("ExcelExport", "Failed to export trade logs to CSV", e)
         null
    }
}

@Composable
fun WatchlistTab(viewModel: TradeViewModel) {
    val watchlists by viewModel.watchlistsState.collectAsStateWithLifecycle()
    val aiResponse by viewModel.watchlistAiAnalysis.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzingWatchlist.collectAsStateWithLifecycle()

    var selectedWatchlistId by remember { mutableStateOf<Long?>(null) }
    val currentWatchlist = watchlists.find { it.id == selectedWatchlistId } ?: watchlists.firstOrNull()

    // Sync selected id with available watchlists if it gets deleted or on launch
    LaunchedEffect(watchlists) {
        if (selectedWatchlistId == null || watchlists.none { it.id == selectedWatchlistId }) {
            selectedWatchlistId = watchlists.firstOrNull()?.id
        }
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var editNameValue by remember { mutableStateOf("") }
    var newWatchlistName by remember { mutableStateOf("") }
    
    var symbolInput by remember { mutableStateOf("") }
    var selectedSymbolForScan by remember { mutableStateOf("") }

    // State for thematic generator
    var showAiSuggesterDialog by remember { mutableStateOf(false) }
    var aiThemeInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Watchlist Selection & Management Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardGray.copy(alpha = 0.85f)),
            border = BorderStroke(1.dp, DividerGray.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WATCHLIST MANAGER",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = NeonBlue
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showAiSuggesterDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .background(NeonBlue.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "AI Suggestions",
                                tint = NeonBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ChartGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                "W+",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }

                if (watchlists.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = SoftText.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                "No Watchlists Formed",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = SoftText
                            )
                            Text(
                                "Tap NEW or use AI suggestions to initiate.",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftText.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    // Watchlist Dropdown / Tab Row selection
                    ScrollableTabRow(
                        selectedTabIndex = watchlists.indexOfFirst { it.id == (currentWatchlist?.id ?: -1L) }.coerceAtLeast(0),
                        containerColor = Color.Transparent,
                        contentColor = NeonBlue,
                        edgePadding = 0.dp,
                        divider = {},
                        indicator = {}
                    ) {
                        watchlists.forEach { wl ->
                            val isSelected = wl.id == currentWatchlist?.id
                            Tab(
                                selected = isSelected,
                                onClick = { selectedWatchlistId = wl.id },
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonBlue.copy(alpha = 0.15f) else Color.Transparent)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) NeonBlue.copy(alpha = 0.5f) else DividerGray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = wl.name.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = if (isSelected) TextWhite else SoftText
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Watchlist Detail Section
        currentWatchlist?.let { wl ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardGray.copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, DividerGray.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = wl.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextWhite
                            )

                            IconButton(
                                onClick = {
                                    editNameValue = wl.name
                                    showEditNameDialog = true
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit Watchlist Name",
                                    tint = SoftText,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteWatchlist(wl) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Watchlist",
                                tint = ChartRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Symbols Add Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = symbolInput,
                            onValueChange = { symbolInput = it },
                            placeholder = { Text("E.g. BTC/USD or NVDA", color = SoftText.copy(alpha = 0.6f), fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = DividerGray,
                                focusedContainerColor = SlateBackground.copy(alpha = 0.7f),
                                unfocusedContainerColor = SlateBackground.copy(alpha = 0.7f),
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        Button(
                            onClick = {
                                if (symbolInput.isNotBlank()) {
                                    val clean = symbolInput.trim().uppercase()
                                    val currentSymbols = if (wl.symbols.isBlank()) {
                                        emptyList()
                                    } else {
                                        wl.symbols.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    }
                                    if (!currentSymbols.contains(clean)) {
                                        val updated = (currentSymbols + clean).joinToString(", ")
                                        viewModel.updateWatchlist(wl.copy(symbols = updated))
                                    }
                                    symbolInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Ticker",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Listed Symbol Tags
                    val symbolsList = if (wl.symbols.isBlank()) {
                        emptyList()
                    } else {
                        wl.symbols.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    }

                    if (symbolsList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Your watchlist is empty. Add a symbol above.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftText,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "ASSETS IN WATCHLIST (TAP SPARKLE ICON FOR AI ANALYSIS):",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                ),
                                color = SoftText
                            )

                            symbolsList.forEach { sym ->
                                val isScanningCurrent = isAnalyzing && selectedSymbolForScan == sym
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = SlateBackground.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isScanningCurrent) NeonBlue else DividerGray.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Left: Stock ticker symbol
                                        Text(
                                            text = sym,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = TextWhite,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Right: Row actions ending with the Gemini AI sparkle star
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val updatedSymbols = symbolsList.filter { it != sym }.joinToString(", ")
                                                    viewModel.updateWatchlist(wl.copy(symbols = updatedSymbols))
                                                    if (selectedSymbolForScan == sym) {
                                                        selectedSymbolForScan = ""
                                                    }
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Remove Symbol",
                                                    tint = ChartRed.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    selectedSymbolForScan = sym
                                                    viewModel.analyzeWatchlistSymbol(sym)
                                                },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(NeonBlue.copy(alpha = 0.15f), CircleShape)
                                            ) {
                                                if (isScanningCurrent) {
                                                    CircularProgressIndicator(
                                                        color = NeonBlue,
                                                        modifier = Modifier.size(18.dp),
                                                        strokeWidth = 2.dp
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Filled.Star,
                                                        contentDescription = "Gemini AI technical scan",
                                                        tint = NeonBlue,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (aiResponse.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateBackground.copy(alpha = 0.90f)),
                border = BorderStroke(1.5.dp, NeonBlue.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = NeonBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "GEMINI MARKET BRIEFING",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextWhite
                            )
                        }

                        IconButton(
                            onClick = { viewModel.clearWatchlistAnalysis() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear report",
                                tint = SoftText,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = DividerGray.copy(alpha = 0.5f))

                    SelectionContainer {
                        Text(
                            text = aiResponse,
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 18.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextWhite
                        )
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = CardGray,
                title = {
                    Text(
                        "CREATE NEW WATCHLIST",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                        color = TextWhite
                    )
                },
                text = {
                    OutlinedTextField(
                        value = newWatchlistName,
                        onValueChange = { newWatchlistName = it },
                        label = { Text("Watchlist Name", color = SoftText) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = DividerGray,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newWatchlistName.isNotBlank()) {
                                viewModel.addWatchlist(newWatchlistName)
                                newWatchlistName = ""
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                    ) {
                        Text("Save", color = Color.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel", color = SoftText)
                    }
                }
            )
        }

        if (showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                containerColor = CardGray,
                title = {
                    Text(
                        "RENAME WATCHLIST",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                        color = TextWhite
                    )
                },
                text = {
                    OutlinedTextField(
                        value = editNameValue,
                        onValueChange = { editNameValue = it },
                        label = { Text("New Name", color = SoftText) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = DividerGray,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editNameValue.isNotBlank() && currentWatchlist != null) {
                                viewModel.updateWatchlist(currentWatchlist.copy(name = editNameValue))
                                showEditNameDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                    ) {
                        Text("Rename", color = Color.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) {
                        Text("Cancel", color = SoftText)
                    }
                }
            )
        }

        if (showAiSuggesterDialog) {
            AlertDialog(
                onDismissRequest = { showAiSuggesterDialog = false },
                containerColor = CardGray,
                title = {
                    Text(
                        "AI THEMATIC WATCHLIST CO-PILOT",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                        color = NeonBlue
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Enter a strategic concept or market theme, and Gemini AI will scan ideas and suggest potential high-growth assets to watch.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftText
                        )

                        OutlinedTextField(
                            value = aiThemeInput,
                            onValueChange = { aiThemeInput = it },
                            placeholder = { Text("E.g. Hot AI Trends, Layer-2 Crypto Scale", color = SoftText.copy(alpha = 0.5f), fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = DividerGray,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        if (aiResponse.isNotBlank() && !isAnalyzing) {
                            val lines = aiResponse.split("\n")
                            val parsedTickers = lines.flatMap { line ->
                                val matches = Regex("[A-Z\\d\\-/]{2,10}").findAll(line)
                                matches.map { it.value }
                            }.filter { it != "USD" && it != "AI" && it != "GEMINI" && it != "CO-PILOT" && it.length > 1 }.distinct()

                            if (parsedTickers.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = {
                                        val themeName = aiThemeInput.ifBlank { "AI Suggester" }
                                        viewModel.addWatchlist(
                                            name = themeName.uppercase(),
                                            symbols = parsedTickers.joinToString(", ")
                                        )
                                        showAiSuggesterDialog = false
                                        viewModel.clearWatchlistAnalysis()
                                        aiThemeInput = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ChartGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("CREATE WATCHLIST FROM AI OUTPUT", color = Color.Black, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (aiThemeInput.isNotBlank()) {
                                viewModel.getWatchlistSuggestions(aiThemeInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp))
                        } else {
                            Text("DRAFT WITH AI", color = Color.Black, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAiSuggesterDialog = false
                        viewModel.clearWatchlistAnalysis()
                    }) {
                        Text("Close", color = SoftText)
                    }
                }
            )
        }
    }
}
