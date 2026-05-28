package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TradeDashboard
import com.example.ui.TradeViewModel
import com.example.ui.theme.MyApplicationTheme

import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
  private var viewModel: TradeViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    var initError: Throwable? = null
    try {
      viewModel = ViewModelProvider(this)[TradeViewModel::class.java]
    } catch (t: Throwable) {
      Log.e("MainActivity", "Failed to initialize ViewModel", t)
      initError = t
    }

    setContent {
      MyApplicationTheme {
        var appError by remember { mutableStateOf(initError) }

        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          val currentError = appError
          if (currentError != null) {
            CrashFallbackScreen(error = currentError) {
              try {
                viewModel = ViewModelProvider(this@MainActivity)[TradeViewModel::class.java]
                appError = null
              } catch (t: Throwable) {
                appError = t
              }
            }
          } else {
            viewModel?.let {
              TradeDashboard(viewModel = it)
            } ?: run {
              Text("Initializing Trading Journal ViewModel...")
            }
          }
        }
      }
    }
  }
}

@Composable
fun CrashFallbackScreen(error: Throwable, onReset: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF131314))
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = "⚠️",
        fontSize = 64.sp
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "APPLICATION CRASH PREVENTED",
        color = Color(0xFFFFB4AB),
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        fontSize = 18.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "An unexpected exception was intercepted by the error boundary:",
        color = Color(0xFFC4C6D0),
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        modifier = Modifier.padding(horizontal = 8.dp)
      )
      Spacer(modifier = Modifier.height(16.dp))
      
      // Error trace box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF212327))
          .padding(16.dp)
      ) {
        val stackTraceString = Log.getStackTraceString(error)
        Text(
          text = stackTraceString.take(1500),
          color = Color(0xFFFFB4AB),
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp
        )
      }
      
      Spacer(modifier = Modifier.height(24.dp))
      
      Button(
        onClick = onReset,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB4F1BE)),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(
          text = "CLEAR & RETRY JOURNAL",
          color = Color.Black,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}
