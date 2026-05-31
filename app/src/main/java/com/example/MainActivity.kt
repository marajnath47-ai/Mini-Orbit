package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BrowserScreen
import com.example.ui.BrowserViewModel
import com.example.ui.theme.MyApplicationTheme

class BrowserViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrowserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BrowserViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
  private val fatalErrorState = mutableStateOf<String?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Intercept uncaught exceptions cleanly
    val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      android.util.Log.e("MainActivity", "Caught uncaught exception:", throwable)
      val errorMsg = throwable.localizedMessage ?: throwable.message ?: "Unknown fatal error"
      
      if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
        // Main thread crashed: we must terminate cleanly to prevent a zombie ANR / broken input channel
        defaultHandler?.uncaughtException(thread, throwable)
      } else {
        // Background thread crashed: main thread is intact, we can safely show the custom error state
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
          fatalErrorState.value = errorMsg
        }
      }
    }

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          val errorMsg = fatalErrorState.value
          if (errorMsg != null) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
              ) {
                Column(
                  modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Fatal Error Recovery",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(56.dp)
                  )
                  Spacer(modifier = Modifier.height(16.dp))
                  Text(
                    text = "System Recovery Mode",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  Text(
                    text = "Orbit Web Browser intercepted a system-level component error. This can happen if the device's system WebView fails to respond.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                  )
                  Spacer(modifier = Modifier.height(16.dp))
                  Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                  ) {
                    Text(
                      text = "Technical info: $errorMsg",
                      style = MaterialTheme.typography.bodySmall,
                      modifier = Modifier.padding(12.dp),
                      textAlign = TextAlign.Start
                    )
                  }
                  Spacer(modifier = Modifier.height(24.dp))
                  Button(
                    onClick = { fatalErrorState.value = null },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.error
                    )
                  ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restart Session")
                  }
                }
              }
            }
          } else {
            val viewModel: BrowserViewModel = viewModel(
                factory = BrowserViewModelFactory(application)
            )
            BrowserScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}
