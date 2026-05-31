package com.example.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Bookmark
import com.example.data.HistoryItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val isSplashActive by viewModel.isSplashActive.collectAsStateWithLifecycle()
    
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = isSplashActive,
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(400))
            },
            label = "splash_transition"
        ) { splash ->
            if (splash) {
                SplashScreen(viewModel = viewModel)
            } else {
                BrowserMainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SplashScreen(viewModel: BrowserViewModel) {
    val style by viewModel.splashStyle.collectAsStateWithLifecycle()
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19)) // Premium Midnight Dark Space Background
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Render custom background physics based on premium style selection
        when (style) {
            "Particle Space Warp" -> {
                val starFraction by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1800, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "warp_fraction"
                )

                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = this.center
                    for (i in 0..60) {
                        val angle = (i * 137.5) * (java.lang.Math.PI / 180f)
                        val speedFactor = 0.15f + (i % 6) * 0.15f
                        val rawDistFraction = (starFraction * speedFactor) % 1f
                        val dist = (size.width * 0.6f) * rawDistFraction
                        val x = center.x + java.lang.Math.cos(angle).toFloat() * dist
                        val y = center.y + java.lang.Math.sin(angle).toFloat() * dist
                        val radius = 1.5.dp.toPx() + (rawDistFraction * 3.5.dp.toPx())
                        val alpha = 0.2f + rawDistFraction * 0.8f
                        drawCircle(
                            color = if (i % 2 == 0) Color(0xFF00D2FF).copy(alpha = alpha) else Color(0xFF9D4EDD).copy(alpha = alpha),
                            radius = radius,
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                    }
                }
            }
            "Gradient Shimmer Flow" -> {
                val shimmerX by infiniteTransition.animateFloat(
                    initialValue = -800f,
                    targetValue = 1800f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "shimmer_x"
                )
                
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0B0F19),
                                Color(0xFF131D35),
                                Color(0xFF0B0F19)
                            ),
                            start = androidx.compose.ui.geometry.Offset(shimmerX, 0f),
                            end = androidx.compose.ui.geometry.Offset(shimmerX + 600f, size.height)
                        )
                    )
                }
            }
            else -> {
                // Default: Cosmic Celestial Pulsar background ring glows
                val glowAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.1f,
                    targetValue = 0.35f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "glow_alpha"
                )

                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = this.center
                    drawCircle(
                        color = Color(0xFF00D2FF),
                        radius = 180.dp.toPx() * pulseScale,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(20f, 40f),
                                rotationAngle * 0.5f
                            )
                        ),
                        alpha = glowAlpha
                    )
                    drawCircle(
                        color = Color(0xFF9D4EDD),
                        radius = 120.dp.toPx() / pulseScale,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(10f, 20f),
                                -rotationAngle
                            )
                        ),
                        alpha = glowAlpha * 0.8f
                    )
                }
            }
        }

        // Foreground brand container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            val brandGradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF00F2FE),
                    Color(0xFF4FACFE),
                    Color(0xFF00D2FF),
                    Color(0xFF9D4EDD)
                )
            )

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(brandGradient)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size((92 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0B0F19)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Orbit Browser Icon",
                        tint = Color(0xFF00D2FF),
                        modifier = Modifier
                            .size(52.dp)
                            .graphicsLayer(rotationZ = if (style == "Cosmic Celestial Pulsar") rotationAngle * 0.2f else 0f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Orbit Web Browser",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.5.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = Color(0xFF1E293B).copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = style,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF00D2FF),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Color(0xFF00D2FF),
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserMainContent(viewModel: BrowserViewModel) {
    val currentUrl by viewModel.currentUrl.collectAsStateWithLifecycle()
    val addressBarText by viewModel.addressBarText.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val loadingProgress by viewModel.loadingProgress.collectAsStateWithLifecycle()
    val canGoBack by viewModel.canGoBack.collectAsStateWithLifecycle()
    val canGoForward by viewModel.canGoForward.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val useWebViewEngine by viewModel.useWebViewEngine.collectAsStateWithLifecycle()
    
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBookmarksSheet by remember { mutableStateOf(false) }
    
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    
    // Web page context metadata (helps with bookmark toggles)
    var currentPageTitle by remember { mutableStateOf("") }
    
    // Intercept back actions
    if (currentUrl != null) {
        BackHandler {
            if (canGoBack && webViewInstance?.canGoBack() == true) {
                webViewInstance?.goBack()
            } else {
                viewModel.resetToHome()
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                BrowserTopBar(
                    addressBarText = addressBarText,
                    onAddressChanged = { viewModel.setAddressText(it) },
                    onNavigate = { viewModel.loadUrl(addressBarText) },
                    onHomeClick = { viewModel.resetToHome() },
                    currentUrl = currentUrl,
                    isBookmarked = bookmarks.any { it.url == currentUrl },
                    onToggleBookmark = {
                        currentUrl?.let { url ->
                            viewModel.toggleBookmark(url, currentPageTitle)
                        }
                    },
                    webView = webViewInstance
                )
                
                if (isLoading) {
                    LinearProgressIndicator(
                        progress = { loadingProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                } else {
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        },
        bottomBar = {
            BrowserBottomBar(
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                onBackClick = { webViewInstance?.goBack() },
                onForwardClick = { webViewInstance?.goForward() },
                onHomeClick = { viewModel.resetToHome() },
                onBookmarksClick = { showBookmarksSheet = true },
                onRefreshClick = { webViewInstance?.reload() },
                currentUrl = currentUrl
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentUrl == null,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                },
                label = "dashboard_vs_webview"
            ) { isHome ->
                if (isHome) {
                    HomeDashboard(
                        onSelectUrl = { viewModel.loadFromDashboard(it) },
                        bookmarks = bookmarks,
                        onDeleteBookmark = { viewModel.deleteBookmark(it.id) },
                        historyState = viewModel.history,
                        onDeleteHistory = { viewModel.deleteHistoryItem(it.id) },
                        useWebViewEngine = useWebViewEngine,
                        onEngineChange = { viewModel.setUseWebViewEngine(it) }
                    )
                } else {
                    WebViewContainer(
                        url = currentUrl ?: "",
                        viewModel = viewModel,
                        onWebViewCreated = { webViewInstance = it },
                        onPageInfoUpdated = { title ->
                            currentPageTitle = title
                        }
                    )
                }
            }
        }
    }

    // Bookmarks and History Sheets
    if (showBookmarksSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBookmarksSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxHeight(0.85f).testTag("bookmarks_bottom_sheet")
        ) {
            BookmarksAndHistorySheetContent(
                viewModel = viewModel,
                onDismiss = { showBookmarksSheet = false },
                onLoadUrl = { url ->
                    viewModel.loadFromDashboard(url)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBookmarksSheet = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun BrowserTopBar(
    addressBarText: String,
    onAddressChanged: (String) -> Unit,
    onNavigate: () -> Unit,
    onHomeClick: () -> Unit,
    currentUrl: String?,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    webView: WebView?
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth().testTag("browser_top_bar")
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant brand text or back to dashboard
            IconButton(
                onClick = onHomeClick,
                modifier = Modifier.testTag("home_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home dashboard",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Textfield Address Bar
            TextField(
                value = addressBarText,
                onValueChange = onAddressChanged,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("address_bar_textfield"),
                placeholder = {
                    Text(
                        text = "Search or type URL",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        onNavigate()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(24.dp),
                leadingIcon = {
                    val isHttps = currentUrl?.startsWith("https://") == true
                    Icon(
                        imageVector = if (isHttps) Icons.Default.Lock else Icons.Default.Language,
                        contentDescription = "Security lock",
                        tint = if (isHttps) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (addressBarText.isNotEmpty()) {
                        IconButton(
                            onClick = { onAddressChanged("") },
                            modifier = Modifier.size(24.dp).testTag("clear_address_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear input",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Action: Bookmark Page (Only visible when viewport is viewing web pages)
            if (currentUrl != null) {
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("bookmark_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        onNavigate()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.testTag("search_go_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Go / Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun BrowserBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onHomeClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onRefreshClick: () -> Unit,
    currentUrl: String?
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("browser_bottom_bar")
    ) {
        NavigationBar(
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = false,
                onClick = onBackClick,
                enabled = canGoBack,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                },
                modifier = Modifier.testTag("nav_back")
            )

            NavigationBarItem(
                selected = false,
                onClick = onForwardClick,
                enabled = canGoForward,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward"
                    )
                },
                modifier = Modifier.testTag("nav_forward")
            )

            NavigationBarItem(
                selected = currentUrl == null,
                onClick = onHomeClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = "Home Hub"
                    )
                },
                label = { Text("Hub", fontSize = 10.sp) },
                modifier = Modifier.testTag("nav_home")
            )

            NavigationBarItem(
                selected = false,
                onClick = onRefreshClick,
                enabled = currentUrl != null,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload"
                    )
                },
                modifier = Modifier.testTag("nav_reload")
            )

            NavigationBarItem(
                selected = false,
                onClick = onBookmarksClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Bookmarks,
                        contentDescription = "Saved"
                    )
                },
                label = { Text("Saved", fontSize = 10.sp) },
                modifier = Modifier.testTag("nav_bookmarks")
            )
        }
    }
}

@Composable
fun HomeDashboard(
    onSelectUrl: (String) -> Unit,
    bookmarks: List<Bookmark>,
    onDeleteBookmark: (Bookmark) -> Unit,
    historyState: kotlinx.coroutines.flow.StateFlow<List<HistoryItem>>,
    onDeleteHistory: (HistoryItem) -> Unit,
    useWebViewEngine: Boolean,
    onEngineChange: (Boolean) -> Unit
) {
    val history by historyState.collectAsStateWithLifecycle()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Welcome Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Orbit Browser Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Orbit",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Lightweight browsing dashboard",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Quick Launch Grid Title
        item {
            Text(
                text = "Quick Tiles",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Quick Launch Row/Grid
        item {
            val sites = listOf(
                ShortcutSite("Google", "https://www.google.com", Icons.Default.Search, Color(0xFF4285F4)),
                ShortcutSite("Wikipedia", "https://www.wikipedia.org", Icons.AutoMirrored.Filled.MenuBook, Color(0xFF6C757D)),
                ShortcutSite("YouTube", "https://www.youtube.com", Icons.Default.PlayArrow, Color(0xFFFF0000)),
                ShortcutSite("GitHub", "https://www.github.com", Icons.Default.Code, Color(0xFF24292E)),
                ShortcutSite("Reddit", "https://www.reddit.com", Icons.Default.Forum, Color(0xFFFF4500)),
                ShortcutSite("StackOverflow", "https://stackoverflow.com", Icons.Default.Grid3x3, Color(0xFFF48024))
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sites) { site ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onSelectUrl(site.url) }
                            .testTag("shortcut_tile_${site.name.lowercase()}"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(site.accentColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = site.icon,
                                    contentDescription = site.name,
                                    tint = site.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = site.name,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Bookmarks preview heading
        item {
            Text(
                text = "Pinned Bookmarks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (bookmarks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = "No bookmarks",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No saved bookmarks yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bookmark sites while browsing to pin them here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(bookmarks.take(4)) { bookmark ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .clickable { onSelectUrl(bookmark.url) }
                        .testTag("bookmark_item_dashboard_${bookmark.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bookmark.title.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bookmark.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = bookmark.url,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(
                            onClick = { onDeleteBookmark(bookmark) },
                            modifier = Modifier.testTag("dashboard_unbookmark_${bookmark.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Unpin",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // History list heading
        item {
            Text(
                text = "Recently Visited",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (history.isEmpty()) {
            item {
                Text(
                    text = "No history log yet. Start browsing to see your activity.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            }
        } else {
            items(history.take(4)) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectUrl(item.url) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History Item icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.url,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(
                        onClick = { onDeleteHistory(item) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete from history",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Creator settings title
        item {
            Text(
                text = "Developer & Monetization Suite",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Customize & Monetize Orbit",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Build brand credentials, configure active ad unit placements, preview Play Store screenshot assets, and view compliance documents.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Active Navigation Engine",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toggle between high-fidelity local emulation vs native webview. Emulators require sandbox to avoid package crashes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onEngineChange(false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!useWebViewEngine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (!useWebViewEngine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Sandbox Simulator",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sandbox Engine", style = MaterialTheme.typography.labelMedium)
                        }
                        
                        Button(
                            onClick = { onEngineChange(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (useWebViewEngine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (useWebViewEngine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Web,
                                contentDescription = "Native WebView",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Native WebView", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Card 1: Splash Customizer
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectUrl("orbit://splash") },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Splash Customizer",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Splash Spec",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Card 2: AdMob Earnings
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectUrl("orbit://admob") },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "AdMob Earnings",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "AdMob Earning",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Card 3: Play Store Graphics Showcase
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectUrl("orbit://screenshots") },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Play Store Screenshots",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Store Assets",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Card 4: Privacy Policy
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectUrl("orbit://privacy") },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Privacy Policy",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Privacy Spec",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    url: String,
    viewModel: BrowserViewModel,
    onWebViewCreated: (WebView?) -> Unit,
    onPageInfoUpdated: (title: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var isWebViewSupported by remember { mutableStateOf(true) }
    val useWebViewEngine by viewModel.useWebViewEngine.collectAsStateWithLifecycle()
    
    // Remember custom Webview instance safely using a Result wrapper to catch exceptions without composition side effects
    val webViewResult = remember(context, useWebViewEngine) {
        if (!useWebViewEngine) {
            Result.failure<WebView>(IllegalStateException("Native WebView is in simulation mode (Sandbox Enabled)."))
        } else {
            try {
                // Check if WebView packages are available on API 26+
                val hasWebViewPackage = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    android.webkit.WebView.getCurrentWebViewPackage() != null
                } else {
                    true
                }
                if (!hasWebViewPackage) {
                    Result.failure<WebView>(IllegalStateException("System WebView package is disabled or not available."))
                } else {
                    val wv = WebView(context).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    
                    // Critical Secure settings
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            url?.let {
                                viewModel.updateWebPageState(
                                    url = it,
                                    title = view?.title ?: "",
                                    canGoBack = view?.canGoBack() ?: false,
                                    canGoForward = view?.canGoForward() ?: false,
                                    isLoading = true,
                                    progress = 10
                                )
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            url?.let {
                                val title = view?.title ?: ""
                                viewModel.updateWebPageState(
                                    url = it,
                                    title = title,
                                    canGoBack = view?.canGoBack() ?: false,
                                    canGoForward = view?.canGoForward() ?: false,
                                    isLoading = false,
                                    progress = 100
                                )
                                onPageInfoUpdated(title)
                            }
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                            // Safe browsing override
                            return false
                        }
                    }
                    
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            view?.url?.let { currentLoc ->
                                viewModel.updateWebPageState(
                                    url = currentLoc,
                                    title = view.title ?: "",
                                    canGoBack = view.canGoBack(),
                                    canGoForward = view.canGoForward(),
                                    isLoading = newProgress < 100,
                                    progress = newProgress
                                )
                            }
                        }

                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            super.onReceivedTitle(view, title)
                            title?.let { onPageInfoUpdated(it) }
                        }
                    }
                }
                Result.success(wv)
            }
        } catch (e: Throwable) {
            Result.failure<WebView>(e)
        }
    }
}

    val webView = webViewResult.getOrNull()

    LaunchedEffect(webViewResult) {
        if (webViewResult.isFailure) {
            isWebViewSupported = false
        } else {
            isWebViewSupported = true
            webView?.let {
                onWebViewCreated(it)
                it.loadUrl(url)
            }
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView?.let {
                try {
                    (it.parent as? android.view.ViewGroup)?.removeView(it)
                    it.stopLoading()
                    it.destroy()
                } catch (e: java.lang.Exception) {
                    // Ignore cleanUp failures safely
                }
            }
            onWebViewCreated(null)
        }
    }

    if (!useWebViewEngine || !isWebViewSupported || webView == null) {
        MockWebBrowserSandbox(
            url = url,
            viewModel = viewModel,
            onPageInfoUpdated = onPageInfoUpdated
        )
    } else {
        // Monitor external triggers (when address bar updates or tiles are clicked)
        LaunchedEffect(viewModel.navigationTrigger) {
            viewModel.navigationTrigger.collectLatest { freshUrl ->
                if (webView.url != freshUrl) {
                    webView.loadUrl(freshUrl)
                }
            }
        }

        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize()
                .testTag("webview_viewport")
        )
    }
}

@Composable
fun BookmarksAndHistorySheetContent(
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit,
    onLoadUrl: (String) -> Unit
) {
    var activeTabIsBookmarks by remember { mutableStateOf(true) }
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Selector bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FilledTonalIconTabButton(
                text = "Bookmarks (${bookmarks.size})",
                icon = Icons.Default.Star,
                selected = activeTabIsBookmarks,
                onClick = { activeTabIsBookmarks = true },
                modifier = Modifier.weight(1f).testTag("tab_bookmarks")
            )
            Spacer(modifier = Modifier.width(12.dp))
            FilledTonalIconTabButton(
                text = "History (${history.size})",
                icon = Icons.Default.History,
                selected = !activeTabIsBookmarks,
                onClick = { activeTabIsBookmarks = false },
                modifier = Modifier.weight(1f).testTag("tab_history")
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(8.dp))

        if (activeTabIsBookmarks) {
            // Bookmarks Manager view
            if (bookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Bookmarks,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No saved bookmarks",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(bookmarks) { bookmark ->
                        BookmarkRowItem(
                            bookmark = bookmark,
                            onClick = { onLoadUrl(bookmark.url) },
                            onDelete = { viewModel.deleteBookmark(bookmark.id) }
                        )
                    }
                }
            }
        } else {
            // History list view with clear option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Navigation history log",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                if (history.isNotEmpty()) {
                    TextButton(
                        onClick = { viewModel.clearHistory() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear all",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All")
                    }
                }
            }

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HistoryToggleOff,
                            contentDescription = "Empty History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Browsing history is clean",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(history) { item ->
                        HistoryRowItem(
                            item = item,
                            onClick = { onLoadUrl(item.url) },
                            onDelete = { viewModel.deleteHistoryItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilledTonalIconTabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = if (selected) {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(44.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun BookmarkRowItem(
    bookmark: Bookmark,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
            .testTag("bookmark_row_${bookmark.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bookmark.title.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = bookmark.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_bookmark_${bookmark.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete bookmark",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun HistoryRowItem(
    item: HistoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = "HistoryIcon",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp).testTag("delete_history_item_${item.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

data class ShortcutSite(
    val name: String,
    val url: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accentColor: Color
)
