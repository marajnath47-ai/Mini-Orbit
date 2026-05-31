package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Bookmark
import com.example.data.BrowserDatabase
import com.example.data.BrowserRepository
import com.example.data.HistoryItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BrowserRepository

    init {
        val dao = BrowserDatabase.getDatabase(application).browserDao()
        repository = BrowserRepository(dao)
    }

    // State flows
    val bookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryItem>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUrl = MutableStateFlow<String?>(null) // null means Home Dashboard
    val currentUrl: StateFlow<String?> = _currentUrl.asStateFlow()

    private val _addressBarText = MutableStateFlow("")
    val addressBarText: StateFlow<String> = _addressBarText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0)
    val loadingProgress: StateFlow<Int> = _loadingProgress.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    private val _isSplashActive = MutableStateFlow(true)
    val isSplashActive: StateFlow<Boolean> = _isSplashActive.asStateFlow()

    // Trigger to navigate webview to a specific URL in UI
    private val _navigationTrigger = MutableSharedFlow<String>()
    val navigationTrigger: SharedFlow<String> = _navigationTrigger.asSharedFlow()

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800) // Beautiful splash screen delay
            _isSplashActive.value = false
        }
    }

    fun setAddressText(text: String) {
        _addressBarText.value = text
    }

    fun loadUrl(url: String) {
        val processedUrl = processUrl(url)
        _currentUrl.value = processedUrl
        _addressBarText.value = processedUrl
        viewModelScope.launch {
            _navigationTrigger.emit(processedUrl)
        }
    }

    fun loadFromDashboard(url: String) {
        loadUrl(url)
    }

    fun resetToHome() {
        _currentUrl.value = null
        _addressBarText.value = ""
        _isLoading.value = false
        _loadingProgress.value = 0
        _canGoBack.value = false
        _canGoForward.value = false
    }

    private fun processUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "https://www.google.com"

        // Check if looks like a URL
        val hasSpace = trimmed.contains(" ")
        val isWebAddress = !hasSpace && (
                trimmed.startsWith("http://") ||
                trimmed.startsWith("https://") ||
                trimmed.contains(".") && trimmed.indexOf(".") > 0 && trimmed.indexOf(".") < trimmed.length - 1
        )

        return if (isWebAddress) {
            if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                "https://$trimmed"
            } else {
                trimmed
            }
        } else {
            try {
                "https://www.google.com/search?q=${java.net.URLEncoder.encode(trimmed, "UTF-8")}"
            } catch (e: Exception) {
                "https://www.google.com/search?q=$trimmed"
            }
        }
    }

    // WebView State Setters
    fun updateWebPageState(
        url: String,
        title: String,
        canGoBack: Boolean,
        canGoForward: Boolean,
        isLoading: Boolean,
        progress: Int
    ) {
        _canGoBack.value = canGoBack
        _canGoForward.value = canGoForward
        _isLoading.value = isLoading
        _loadingProgress.value = progress
        
        // Only update current URL state if it matches loaded page and is not blank
        if (url.isNotEmpty() && url != "about:blank") {
            _currentUrl.value = url
            _addressBarText.value = url
            
            // Auto add to history on loading success (when progress is complete or reaches 100)
            if (progress == 100 && !isLoading) {
                addToHistory(url, title.ifEmpty() { url })
            }
        }
    }

    // Database operations
    fun toggleBookmark(url: String, title: String) {
        viewModelScope.launch {
            val isBookmarked = bookmarks.value.any { it.url == url }
            if (isBookmarked) {
                repository.removeBookmarkByUrl(url)
            } else {
                repository.addBookmark(Bookmark(url = url, title = title.ifEmpty() { url }))
            }
        }
    }

    fun addBookmarkManual(url: String, title: String) {
        viewModelScope.launch {
            val processedUrl = processUrl(url)
            repository.addBookmark(Bookmark(url = processedUrl, title = title.ifEmpty() { processedUrl }))
        }
    }

    fun deleteBookmark(id: Int) {
        viewModelScope.launch {
            repository.removeBookmarkById(id)
        }
    }

    private fun addToHistory(url: String, title: String) {
        viewModelScope.launch {
            val recent = history.value.firstOrNull()
            if (recent?.url != url) {
                repository.addHistory(HistoryItem(url = url, title = title))
            }
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.removeHistoryById(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    // --- Premium Custom Creator Suite States ---

    // Engine Customizer States
    private val _useWebViewEngine = MutableStateFlow(false) // Default to false (Simulated Sandbox) to ensure 100% stability in headless platform emulator environments
    val useWebViewEngine: StateFlow<Boolean> = _useWebViewEngine.asStateFlow()

    fun setUseWebViewEngine(enabled: Boolean) {
        _useWebViewEngine.value = enabled
    }

    // Splash Customizer States
    private val _splashStyle = MutableStateFlow("Cosmic Celestial Pulsar")
    val splashStyle: StateFlow<String> = _splashStyle.asStateFlow()

    private val _splashDelay = MutableStateFlow(2000L)
    val splashDelay: StateFlow<Long> = _splashDelay.asStateFlow()

    fun updateSplashSettings(style: String, delayMs: Long) {
        _splashStyle.value = style
        _splashDelay.value = delayMs
    }

    fun triggerSplashPreview() {
        viewModelScope.launch {
            _isSplashActive.value = true
            kotlinx.coroutines.delay(_splashDelay.value)
            _isSplashActive.value = false
        }
    }

    // AdMob Earnings Sandbox States
    private val _admobAppId = MutableStateFlow("ca-app-pub-3940256099942544~3347511713")
    val admobAppId: StateFlow<String> = _admobAppId.asStateFlow()

    private val _admobBannerUnitId = MutableStateFlow("ca-app-pub-3940256099942544/6300978111")
    val admobBannerUnitId: StateFlow<String> = _admobBannerUnitId.asStateFlow()

    private val _admobInterstitialUnitId = MutableStateFlow("ca-app-pub-3940256099942544/1033173712")
    val admobInterstitialUnitId: StateFlow<String> = _admobInterstitialUnitId.asStateFlow()

    private val _bannerEnabled = MutableStateFlow(true)
    val bannerEnabled: StateFlow<Boolean> = _bannerEnabled.asStateFlow()

    private val _interstitialEnabled = MutableStateFlow(false)
    val interstitialEnabled: StateFlow<Boolean> = _interstitialEnabled.asStateFlow()

    private val _admobAccountStatus = MutableStateFlow("Connected (Sandboxed)")
    val admobAccountStatus: StateFlow<String> = _admobAccountStatus.asStateFlow()

    private val _admobTotalEarnings = MutableStateFlow(124.50)
    val admobTotalEarnings: StateFlow<Double> = _admobTotalEarnings.asStateFlow()

    private val _admobAdRequests = MutableStateFlow(3240)
    val admobAdRequests: StateFlow<Int> = _admobAdRequests.asStateFlow()

    private val _admobImpressions = MutableStateFlow(2850)
    val admobImpressions: StateFlow<Int> = _admobImpressions.asStateFlow()

    private val _admobClicks = MutableStateFlow(142)
    val admobClicks: StateFlow<Int> = _admobClicks.asStateFlow()

    private val _admobCtr = MutableStateFlow(4.98)
    val admobCtr: StateFlow<Double> = _admobCtr.asStateFlow()

    fun updateAdMobConfig(appId: String, bannerId: String, interstitialId: String) {
        _admobAppId.value = appId.trim()
        _admobBannerUnitId.value = bannerId.trim()
        _admobInterstitialUnitId.value = interstitialId.trim()
    }

    fun toggleBannerEnabled() {
        _bannerEnabled.value = !_bannerEnabled.value
    }

    fun toggleInterstitialEnabled() {
        _interstitialEnabled.value = !_interstitialEnabled.value
    }

    fun resetMockEarnings() {
        _admobTotalEarnings.value = 0.0
        _admobAdRequests.value = 0
        _admobImpressions.value = 0
        _admobClicks.value = 0
        _admobCtr.value = 0.0
    }

    fun simulateAdImpression(adType: String) {
        viewModelScope.launch {
            _admobAdRequests.value += 1
            when (adType) {
                "banner" -> {
                    _admobImpressions.value += 1
                    _admobTotalEarnings.value += 0.05 // $0.05 per impression
                    if (Math.random() < 0.08) { // 8% CTR
                        _admobClicks.value += 1
                        _admobTotalEarnings.value += 0.45 // $0.45 per click
                    }
                }
                "interstitial" -> {
                    _admobImpressions.value += 1
                    _admobTotalEarnings.value += 0.28 // $0.28 per interstitial impression
                    if (Math.random() < 0.14) { // 14% CTR
                        _admobClicks.value += 1
                        _admobTotalEarnings.value += 0.85 // $0.85 per click
                    }
                }
            }
            if (_admobImpressions.value > 0) {
                _admobCtr.value = (_admobClicks.value.toDouble() / _admobImpressions.value.toDouble()) * 100.0
            }
        }
    }
}
