package com.example.ui

import android.graphics.Bitmap
import com.example.R
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Helper to decode search queries
fun decodeUrlQuery(url: String): String {
    return try {
        val queryPart = url.substringAfter("q=", "")
        if (queryPart.isNotEmpty()) {
            java.net.URLDecoder.decode(queryPart.substringBefore("&"), "UTF-8")
        } else {
            "Orbit Browser"
        }
    } catch (e: Exception) {
        "Search Query"
    }
}

// Get mock titles for tab items
fun getMockTitleForUrl(url: String): String {
    return when {
        url.isEmpty() -> "Home Hub"
        url.startsWith("orbit://privacy") -> "Orbit Core - Privacy Policy"
        url.startsWith("orbit://admob") -> "Orbit Setup - AdMob Monetization Suite"
        url.startsWith("orbit://screenshots") -> "Orbit Studio - Play Store Assets"
        url.startsWith("orbit://splash") -> "Orbit Premium - Splash Screen Customizer"
        url.contains("google.com/search") -> "Search: " + decodeUrlQuery(url)
        url.contains("google.com") -> "Google Sandbox"
        url.contains("wikipedia.org") -> "Wikipedia Portal"
        url.contains("youtube.com") -> "Watch Video - YouTube"
        url.contains("github.com") -> "GitHub System"
        url.contains("reddit.com") -> "Reddit Hub"
        else -> {
            val domain = url.substringAfter("://", "").substringBefore("/")
            domain.ifEmpty { url }.replace("www.", "")
        }
    }
}

@Composable
fun MockWebBrowserSandbox(
    url: String,
    viewModel: BrowserViewModel,
    onPageInfoUpdated: (String) -> Unit
) {
    var isLoadingPage by remember { mutableStateOf(false) }
    var previousUrl by remember { mutableStateOf("") }

    // Simulating rich browser page load state delays natively
    LaunchedEffect(url) {
        if (url != previousUrl) {
            isLoadingPage = true
            previousUrl = url
            // Emit progress updates to feed overall loading bar
            viewModel.updateWebPageState(
                url = url,
                title = getMockTitleForUrl(url),
                canGoBack = true,
                canGoForward = false,
                isLoading = true,
                progress = 25
            )
            delay(150)
            viewModel.updateWebPageState(
                url = url,
                title = getMockTitleForUrl(url),
                canGoBack = true,
                canGoForward = false,
                isLoading = true,
                progress = 65
            )
            delay(200)
            
            val finalTitle = getMockTitleForUrl(url)
            viewModel.updateWebPageState(
                url = url,
                title = finalTitle,
                canGoBack = true,
                canGoForward = false,
                isLoading = false,
                progress = 100
            )
            onPageInfoUpdated(finalTitle)
            isLoadingPage = false
        }
    }

    if (isLoadingPage) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading Secure Page...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    } else {
        // Core mock rendering dispatcher
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                url.startsWith("orbit://privacy") -> {
                    MockPrivacyPolicyPage(viewModel = viewModel)
                }
                url.startsWith("orbit://admob") -> {
                    MockAdMobPage(viewModel = viewModel)
                }
                url.startsWith("orbit://screenshots") -> {
                    MockPlayStoreScreenshotsPage(viewModel = viewModel)
                }
                url.startsWith("orbit://splash") -> {
                    MockPremiumSplashPage(viewModel = viewModel)
                }
                url.contains("google.com/search") -> {
                    MockGoogleSearchResults(url = url, viewModel = viewModel)
                }
                url.contains("google.com") -> {
                    MockGoogleHome(viewModel = viewModel)
                }
                url.contains("wikipedia.org") -> {
                    MockWikipediaPage(url = url, viewModel = viewModel)
                }
                url.contains("youtube.com") -> {
                    MockYouTubePage(url = url, viewModel = viewModel)
                }
                url.contains("github.com") -> {
                    MockGitHubPage(url = url, viewModel = viewModel)
                }
                url.contains("reddit.com") -> {
                    MockRedditPage(url = url, viewModel = viewModel)
                }
                else -> {
                    MockGenericPage(url = url, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MockGoogleHome(viewModel: BrowserViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            // Branded colorful title layout
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val googleColors = listOf(
                    Color(0xFF4285F4), Color(0xFFEA4335), Color(0xFFFBBC05),
                    Color(0xFF4285F4), Color(0xFF34A853), Color(0xFFEA4335)
                )
                val letters = listOf("G", "o", "o", "g", "l", "e")
                letters.forEachIndexed { i, char ->
                    Text(
                        text = char,
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                        color = googleColors[i % googleColors.size]
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Sandbox Search Mode",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            // Elegant search card layout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search or type URL") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotBlank()) {
                                    viewModel.loadUrl("https://www.google.com/search?q=${java.net.URLEncoder.encode(searchQuery, "UTF-8")}")
                                }
                            }
                        )
                    )
                    
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.loadUrl("https://www.google.com/search?q=${java.net.URLEncoder.encode(searchQuery, "UTF-8")}")
                    }
                },
                enabled = searchQuery.isNotBlank(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Google Search")
            }
        }
    }
}

@Composable
fun MockGoogleSearchResults(url: String, viewModel: BrowserViewModel) {
    val query = decodeUrlQuery(url)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Query header info
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Results for query:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = query,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        
        // Smart Widgets depending on query text
        if (query.contains("weather", ignoreCase = true)) {
            item {
                WeatherCardWidget()
            }
        }
        
        if (query.contains("calc", ignoreCase = true) || query.any { it.isDigit() } && (query.contains("+") || query.contains("-") || query.contains("*") || query.contains("/"))) {
            item {
                MiniCalculatorWidget()
            }
        }

        // Standard mock search results list
        item {
            Text(
                text = "Web Search Results",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        val results = getMockResultsForQuery(query)
        items(results) { res ->
            SearchResultCard(res = res, onCardClick = { viewModel.loadUrl(res.url) })
        }
    }
}

@Composable
fun WeatherCardWidget() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Bengaluru, Karnataka",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Partly Cloudy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = "28°C",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherForecastDay("Mon", "27°", Icons.Default.Cloud)
                WeatherForecastDay("Tue", "29°", Icons.Default.WbSunny)
                WeatherForecastDay("Wed", "28°", Icons.Default.Thunderstorm)
                WeatherForecastDay("Thu", "26°", Icons.Default.Grain)
                WeatherForecastDay("Fri", "30°", Icons.Default.WbSunny)
            }
        }
    }
}

@Composable
fun WeatherForecastDay(day: String, temp: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = day, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Icon(imageVector = icon, contentDescription = day, modifier = Modifier.size(24.dp).padding(vertical = 2.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(text = temp, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun MiniCalculatorWidget() {
    var stateExpr by remember { mutableStateOf("") }
    var stateResult by remember { mutableStateOf("") }

    val buildClick: (String) -> Unit = { value ->
        if (value == "=") {
            try {
                val clean = stateExpr.replace("x", "*").replace("÷", "/")
                val valueCalculated = evaluateSimpleMath(clean)
                stateResult = valueCalculated.toString()
            } catch (e: Exception) {
                stateResult = "Math Error"
            }
        } else if (value == "C") {
            stateExpr = ""
            stateResult = ""
        } else {
            stateExpr += value
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Interactive Smart Calculator",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Screen
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stateExpr.ifEmpty { "0" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (stateResult.isNotEmpty()) {
                        Text(
                            text = "= $stateResult",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Grid of buttons
            val keys = listOf(
                listOf("7", "8", "9", "÷"),
                listOf("4", "5", "6", "x"),
                listOf("1", "2", "3", "-"),
                listOf("C", "0", "=", "+")
            )
            
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { op ->
                        Button(
                            onClick = { buildClick(op) },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = if (op == "=" || op == "C") {
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            } else {
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = op, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

// Simple expression runner
fun evaluateSimpleMath(expr: String): Double {
    val clean = expr.trim()
    if (clean.isEmpty()) return 0.0
    
    // Quick parse of single operation
    val opIdx = clean.indexOfAny(charArrayOf('+', '-', '*', '/'))
    if (opIdx == -1) return clean.toDoubleOrNull() ?: 0.0
    
    val op = clean[opIdx]
    val arg1 = clean.substring(0, opIdx).trim().toDoubleOrNull() ?: 0.0
    val arg2 = clean.substring(opIdx + 1).trim().toDoubleOrNull() ?: 0.0
    
    return when (op) {
        '+' -> arg1 + arg2
        '-' -> arg1 - arg2
        '*' -> arg1 * arg2
        '/' -> if (arg2 != 0.0) arg1 / arg2 else 0.0
        else -> 0.0
    }
}

data class MockResult(val title: String, val url: String, val snippet: String)

fun getMockResultsForQuery(query: String): List<MockResult> {
    return listOf(
        MockResult(
            title = "$query - Wikipedia, the free encyclopedia",
            url = "https://en.wikipedia.org/wiki/${java.net.URLEncoder.encode(query, "UTF-8")}",
            snippet = "Learn everything about $query on Wikipedia. Read summary details, historic contexts, and expert classifications comprehensively."
        ),
        MockResult(
            title = "What is $query? - Technical Explainer",
            url = "https://github.com/topics/${java.net.URLEncoder.encode(query.lowercase(), "UTF-8")}",
            snippet = "Explore source codes, libraries, and discussions on $query. Find stars, contributors, and detailed build configurations in open source."
        ),
        MockResult(
            title = "Trending videos matching \"$query\"",
            url = "https://youtube.com",
            snippet = "Watch popular video analysis, step by step tutorials, animated workflows, and real-time streams centering around $query."
        ),
        MockResult(
            title = "Discussion on r/$query - Reddit",
            url = "https://reddit.com",
            snippet = "Join the Reddit community to chat, share opinions, post feeds, and upvote deep-dives discussing $query thread ideas."
        )
    )
}

@Composable
fun SearchResultCard(res: MockResult, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = res.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = res.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = res.snippet,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MockWikipediaPage(url: String, viewModel: BrowserViewModel) {
    val term = url.substringAfter("/wiki/", "").replace("_", " ").ifEmpty { "Web Browser Engine" }
    val cleanTerm = java.net.URLDecoder.decode(term, "UTF-8")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)  // Classic Wikipedia high-contrast white look
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Logo and Wiki Headers
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Wiki",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "WIKIPEDIA",
                    style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp, fontFamily = FontFamily.Serif),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "The Free Encyclopedia",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                    color = Color.Gray
                )
            }
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)
        
        Text(
            text = cleanTerm,
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
        
        // Wikipedia Info Box Layout (traditional side-table style)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            border = CardDefaults.outlinedCardBorder(),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Quick Summary: $cleanTerm",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                WikiInfoRow("Developer", "Orbit Sandbox Labs")
                WikiInfoRow("Initial release", "May 2026")
                WikiInfoRow("Engine type", "Kotlin JVM Sandbox")
                WikiInfoRow("Active users", "Millions worldwide")
            }
        }

        Text(
            text = "From Wikipedia, the free encyclopedia, this sandbox engine allows you to securely navigate, learn, and bookmark sites smoothly. $cleanTerm is widely classified as an open standard protocol utilizing beautiful Material Design 3 spacing and interactive layouts.",
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
            color = Color.DarkGray,
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Features & Specifications",
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Text(
            text = "1. Completely offline persistent bookmarks and cache integration.\n" +
                   "2. Clean UI elements adhering to fluid Material Design.\n" +
                   "3. Secure sandbox wrapper protecting users against browser exploits.",
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
            color = Color.DarkGray,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.resetToHome() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Go to Browser Homepage", color = Color.White)
        }
    }
}

@Composable
fun WikiInfoRow(label: String, valText: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = valText, style = MaterialTheme.typography.bodySmall, color = Color.Black)
    }
}

@Composable
fun MockYouTubePage(url: String, viewModel: BrowserViewModel) {
    var playingVideoTitle by remember { mutableStateOf<String?>(null) }
    var isVideoPlaying by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Premium cinematic Dark YouTube feel
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "YT Logo",
                tint = Color.Red,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "YouTube Sandbox",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        if (playingVideoTitle != null) {
            // Video Player Simulator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                if (isVideoPlaying) {
                    CircularProgressIndicator(color = Color.Red)
                }
                IconButton(onClick = { isVideoPlaying = !isVideoPlaying }) {
                    Icon(
                        imageVector = if (isVideoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Controls",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = playingVideoTitle ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Orbit Systems Labs • 85K views • 2 hours ago",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { playingVideoTitle = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Back to Videos list")
                }
            }
        } else {
            // Videos Grid List
            val mockVideos = listOf(
                "Kotlin Jetpack Compose Mastery Course",
                "Building a Custom Browser inside Android in 2026",
                "How to simulate a full WebView inside sandbox environments",
                "Orbit Browser Launch Trailer - Fast, Fluid, Elegant"
            )
            
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(mockVideos) { video ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { playingVideoTitle = video },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                    ) {
                        Column {
                            // Thumbnail Gradient Spacer
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFF0055), Color(0xFFFFCC00))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Y", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = video,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Orbit Channel • 108K views • 4 days ago",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
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

@Composable
fun MockGitHubPage(url: String, viewModel: BrowserViewModel) {
    var stars by remember { mutableStateOf(2048) }
    var userStarred by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117)) // Authentic GitHub Dark code theme
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "GitHub Code",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "aistudio / orbit-browser",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Repository Meta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (userStarred) {
                        stars--
                        userStarred = false
                    } else {
                        stars++
                        userStarred = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (userStarred) Color(0xFF238636) else Color(0xFF21262D)
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (userStarred) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Star",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Star ($stars)", color = Color.White)
            }
            
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.ForkLeft, contentDescription = "Fork", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Fork (142)", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Files preview card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Source Files",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                GithubFileRow("app/src/main", "Added WebView catch-guards", "2 hours ago")
                GithubFileRow("gradle/libs.versions.toml", "Aligned platform dependencies", "Yesterday")
                GithubFileRow("README.md", "Documentation for sandbox fallback", "3 days ago")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Readme Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "README.md",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "# Orbit System Browser\n\nA lightweight, beautiful, & ultra-fast Android web browser implemented built securely in modern Jetpack Compose.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun GithubFileRow(name: String, commitMsg: String, date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = "Folder",
                tint = Color(0xFF58A6FF),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name, color = Color(0xFF58A6FF), style = MaterialTheme.typography.bodySmall)
        }
        Text(text = commitMsg, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
        Text(text = date, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun MockRedditPage(url: String, viewModel: BrowserViewModel) {
    val posts = remember {
        mutableStateListOf(
            RedditPost("r/kotlin", "Jetpack Compose 1.8 is officially released!", 512),
            RedditPost("r/android", "The ultimate guide on supporting offline sandbox mode correctly", 214),
            RedditPost("r/programming", "Why VM runtimes sometimes lack WebView engines & how to build custom fallbacks", 143),
            RedditPost("r/materialdesign", "Exploring the beauty of dynamic primary colors in Material 3", 89)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1B)) // Reddit Dark layout
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Forum,
                contentDescription = "Reddit",
                tint = Color(0xFFFF4500),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Reddit Sandbox",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(posts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF262627))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = post.subreddit,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF4500),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Upvote Up counters
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { post.upvotes++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "Up",
                                        tint = Color.LightGray
                                    )
                                }
                                Text(
                                    text = post.upvotes.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                IconButton(
                                    onClick = { post.upvotes-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "Down",
                                        tint = Color.LightGray
                                    )
                                }
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.ChatBubble, contentDescription = "Chat", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("42 Reviews", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

class RedditPost(val subreddit: String, val title: String, upvotesStart: Int) {
    var upvotes by mutableStateOf(upvotesStart)
}

@Composable
fun MockGenericPage(url: String, viewModel: BrowserViewModel) {
    val cleanUrl = url.replace("https://", "").replace("http://", "").substringBefore("/")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Global",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = cleanUrl.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Orbit Web Simulation Engine",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Secure Sandbox Sandbox Portal",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Orbit Web Explorer sandbox protects your identity by running outside the native renderer process. The specified page '$url' is resolved within the local system sandbox successfully.\n\nYou can scroll contents, write search, or add this address to your bookmarked collection.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.resetToHome() },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Back to Hub")
        }
    }
}

// ==========================================
// = PREMIUM CREATOR & BUILDER COVETED PAGES =
// ==========================================

@Composable
fun MockPremiumSplashPage(viewModel: BrowserViewModel) {
    val currentStyle by viewModel.splashStyle.collectAsState()
    val currentDelay by viewModel.splashDelay.collectAsState()
    
    val styles = listOf(
        "Cosmic Celestial Pulsar" to "Concentric glowing orbit rings revolving with particle physics.",
        "Particle Space Warp" to "Distant speed stars generating forward warp vectors from the screen center.",
        "Gradient Shimmer Flow" to "Liquid twilight color bands cascading across an obsidian canvas."
    )

    val delays = listOf(
        1500L to "Fast (1.5s)",
        2200L to "Balanced (2.2s)",
        3500L to "Immersive (3.5s)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Splash Premium Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Premium Splash Animation Engine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Configure native startup visual physics styles dynamically.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Animation Physics Style",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        styles.forEach { (name, desc) ->
            val isSelected = currentStyle == name
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { viewModel.updateSplashSettings(name, currentDelay) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.updateSplashSettings(name, currentDelay) }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Startup Intermission Delay",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            delays.forEach { (duration, label) ->
                val isSelected = currentDelay == duration
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.updateSplashSettings(currentStyle, duration) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = { viewModel.triggerSplashPreview() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simulate Live Splash Screen Preview", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { viewModel.resetToHome() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Return to Hub Dashboard", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MockAdMobPage(viewModel: BrowserViewModel) {
    val appId by viewModel.admobAppId.collectAsState()
    val bannerId by viewModel.admobBannerUnitId.collectAsState()
    val interstitialId by viewModel.admobInterstitialUnitId.collectAsState()
    
    val estEarnings by viewModel.admobTotalEarnings.collectAsState()
    val requests by viewModel.admobAdRequests.collectAsState()
    val impressions by viewModel.admobImpressions.collectAsState()
    val clicks by viewModel.admobClicks.collectAsState()
    val ctr by viewModel.admobCtr.collectAsState()
    
    val isBannerEnabled by viewModel.bannerEnabled.collectAsState()
    val isInterstitialEnabled by viewModel.interstitialEnabled.collectAsState()
    
    var formAppId by remember(appId) { mutableStateOf(appId) }
    var formBannerId by remember(bannerId) { mutableStateOf(bannerId) }
    var formInterstitialId by remember(interstitialId) { mutableStateOf(interstitialId) }
    
    var showInterstitialAdOverlay by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(5) }

    LaunchedEffect(showInterstitialAdOverlay) {
        if (showInterstitialAdOverlay) {
            countdown = 5
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    if (showInterstitialAdOverlay) {
        // Immersive Fullscreen Test Ad overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E2E).copy(alpha = 0.98f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = Color(0xFFFFCC00),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Google AdMob Sandbox Ad Block",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                // Simulated Banner / Product asset using a gorgeous linear gradient card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF3B82F6),
                                        Color(0xFF8B5CF6),
                                        Color(0xFFEC4899)
                                    )
                                )
                             )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = "Ad Asset",
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Orbit Pro Browser Upgrade",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bypass all tracking headers, unlock particle warp drives, and integrate sandbox engines on unlimited devices today.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (countdown > 0) {
                    Text(
                        text = "You can dismiss this simulated interstitial in $countdown seconds...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                } else {
                    Button(
                        onClick = {
                            showInterstitialAdOverlay = false
                            viewModel.simulateAdImpression("interstitial")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Ad")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Close And Credit Earnings (+$0.28)")
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Earnings Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "AdMob Earnings Icon",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AdMob Creator Statistics",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Active Sandbox",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Estimated Earnings (USD)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$${String.format("%.2f", estEarnings)}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4CAF50),
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Impressions",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "$impressions",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Clicks",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "$clicks",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CTR",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${String.format("%.2f", ctr)}%",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Placements settings
            Text(
                text = "Configure Active Placements",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "Banner Ad Placements", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Show smart header ads on dashboard", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = isBannerEnabled, onCheckedChange = { viewModel.toggleBannerEnabled() })
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "Interstitial Ad Placements", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Show fullscreen interstitial ads on web transitions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = isInterstitialEnabled, onCheckedChange = { viewModel.toggleInterstitialEnabled() })
                    }
                }
            }

            // Live Ad Placements simulator
            Text(
                text = "Interactive Live Ad Sandbox Simulator",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.simulateAdImpression("banner") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Banner Impression", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = { showInterstitialAdOverlay = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Launch Interstitial", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Ad Unit IDs
            Text(
                text = "Publisher Credentials Form",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = formAppId,
                onValueChange = { formAppId = it },
                label = { Text("AdMob Application ID") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = formBannerId,
                onValueChange = { formBannerId = it },
                label = { Text("Banner Ad Unit ID") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = formInterstitialId,
                onValueChange = { formInterstitialId = it },
                label = { Text("Interstitial Ad Unit ID") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            Button(
                onClick = {
                    viewModel.updateAdMobConfig(formAppId, formBannerId, formInterstitialId)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Publisher Ad Units Configuration", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.resetMockEarnings() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Reset Simulation Balances", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.resetToHome() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Back to Hub Dashboard")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MockPlayStoreScreenshotsPage(viewModel: BrowserViewModel) {
    var sloganText by remember { mutableStateOf("Speed Redefined") }
    var activeBgGradient by remember { mutableStateOf("Deep Cosmic Midnight") }
    
    val backgroundGradients = listOf(
        "Deep Cosmic Midnight" to Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))),
        "Teal Matrix Plasma" to Brush.verticalGradient(listOf(Color(0xFF064E3B), Color(0xFF022C22), Color(0xFF064E3B))),
        "Sunset Fuchsia Pulse" to Brush.verticalGradient(listOf(Color(0xFF4C0519), Color(0xFF831843), Color(0xFF4C0519)))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Showcase Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Play Store Screenshot Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Review live promotional screenshot mockup materials.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Generated static mockups carousel
        Text(
            text = "1. Generated App Store Screenshot Assets",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card containing Screenshot 1
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(220.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.img_ps_screenshot_1_1780249147867),
                        contentDescription = "Play Store Screenshot 1",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "Orbit Splash/Tiles",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Card containing Screenshot 2
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(220.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.img_ps_screenshot_2_1780249166685),
                        contentDescription = "Play Store Screenshot 2",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "Sandbox Hub",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Text(
            text = "2. Active Interactive Mockup Generator",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = sloganText,
            onValueChange = { sloganText = it },
            label = { Text("Mockup Slogan Overlay Text") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            singleLine = true
        )

        Text(
            text = "Select Mockup Studio Background Environment",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            backgroundGradients.forEach { (name, brush) ->
                val isSelected = activeBgGradient == name
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeBgGradient = name },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.substringBefore(" "),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Realtime interactive generated mockup device frame
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            val currentBrush = backgroundGradients.first { it.first == activeBgGradient }.second
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(currentBrush)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = sloganText.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simulated Phone Frame
                    Surface(
                        color = Color(0xFF1E1E2E),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(140.dp)
                            .height(200.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Notch
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Mock Browser dashboard UI elements
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Logo",
                                tint = Color(0xFF00D2FF),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Simple grid
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(3.dp)).background(Color.Red.copy(0.4f)))
                                Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(3.dp)).background(Color.Blue.copy(0.4f)))
                                Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(3.dp)).background(Color.Green.copy(0.4f)))
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Bottom Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { viewModel.resetToHome() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Return to Hub Dashboard")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MockPrivacyPolicyPage(viewModel: BrowserViewModel) {
    val sections = listOf(
        Triple(
            "1. Zero External Logs Policy",
            "Orbit Web Browser relies on a clean architectural philosophy. We do not transmit or duplicate any queries, addresses, or metadata entered inside our browser interface to any remote cloud databases. All processing is executed fully inside the active device environment sandbox.",
            Icons.Default.CloudOff
        ),
        Triple(
            "2. Sandboxed Persistent Storage",
            "All history log details and pinned bookmarks created during standard browsing sessions are compiled locally using an encrypted client-side Room database workspace. Your personal bookmarks cannot be read by other applications installed on this mobile OS.",
            Icons.Default.Storage
        ),
        Triple(
            "3. AdMob Third-Party Disclosures",
            "Google AdMob integrations are active solely under sandboxed configurations. Custom user identifiers (such as Device IDs or advertising telemetry indexes) are governed strictly under Google Publisher parameters. Our internal application does not access, collect, or store dynamic device identification records.",
            Icons.Default.VerifiedUser
        ),
        Triple(
            "4. Absolute Deletion Commands",
            "Users possess absolute authorization regarding cookie tracking directories, history caches, and saved bookmark collections. Executing clear commands inside our setting panels enforces real-time absolute database purge procedures immediately.",
            Icons.Default.DeleteForever
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Privacy Shield",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Privacy & User Security Shield",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Compliance parameters & zero-transmission declarations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        sections.forEach { (title, content, icon) ->
            Column(modifier = Modifier.padding(bottom = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Section Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    lineHeight = 22.sp
                )
            }
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 16.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Legal Compliance Declarations:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(onClick = {}, label = { Text("GDPR Certified") })
                    SuggestionChip(onClick = {}, label = { Text("CCPA Compliant") })
                    SuggestionChip(onClick = {}, label = { Text("FISA Safe") })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.resetToHome() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Return to Hub Dashboard")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
