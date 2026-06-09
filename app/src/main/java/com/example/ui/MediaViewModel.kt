package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BankCard
import com.example.data.DownloadedMedia
import com.example.data.MediaItem
import com.example.data.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MediaRepository(application)
    private val prefs = application.getSharedPreferences("animanhwa3d_prefs", Context.MODE_PRIVATE)

    // Auth & Persistent Register Session States
    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow(prefs.getString("logged_in_email", "") ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userPhone = MutableStateFlow(prefs.getString("logged_in_phone", "") ?: "")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userNickname = MutableStateFlow(prefs.getString("user_nickname", "") ?: "")
    val userNickname: StateFlow<String> = _userNickname.asStateFlow()

    private val _userLanguage = MutableStateFlow(prefs.getString("user_language", "O'zbekcha") ?: "O'zbekcha")
    val userLanguage: StateFlow<String> = _userLanguage.asStateFlow()

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId.asStateFlow()

    // Current screen coordinates
    private val _currentTab = MutableStateFlow("asosiy") // "asosiy", "qidiruv", "yuklanmalar", "profil"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Screen selection detailed items
    private val _selectedMediaItem = MutableStateFlow<MediaItem?>(null)
    val selectedMediaItem: StateFlow<MediaItem?> = _selectedMediaItem.asStateFlow()

    // Playback state triggers
    private val _playingOfflineMedia = MutableStateFlow<DownloadedMedia?>(null)
    val playingOfflineMedia: StateFlow<DownloadedMedia?> = _playingOfflineMedia.asStateFlow()

    private val _playingOnlineMedia = MutableStateFlow<MediaItem?>(null)
    val playingOnlineMedia: StateFlow<MediaItem?> = _playingOnlineMedia.asStateFlow()

    private val _onlinePlaybackTitle = MutableStateFlow<String?>(null)
    val onlinePlaybackTitle: StateFlow<String?> = _onlinePlaybackTitle.asStateFlow()

    // Profiles & themes customization preferences
    private val _darkThemeSelected = MutableStateFlow(true)
    val darkThemeSelected: StateFlow<Boolean> = _darkThemeSelected.asStateFlow()

    private val _watchTimeMinutes = MutableStateFlow(124) // Initial dummy statistical metrics
    val watchTimeMinutes: StateFlow<Int> = _watchTimeMinutes.asStateFlow()

    // VIP Integration States & Admin Configurable Pricing
    private val _isUserVip = MutableStateFlow(prefs.getBoolean("is_user_vip", false))
    val isUserVip: StateFlow<Boolean> = _isUserVip.asStateFlow()

    private val _vipExpiryDate = MutableStateFlow(prefs.getString("vip_expiry_date", "Faol emas") ?: "Faol emas")
    val vipExpiryDate: StateFlow<String> = _vipExpiryDate.asStateFlow()

    private val _vipPriceOneMonth = MutableStateFlow(prefs.getInt("price_1m", 10))
    val vipPriceOneMonth: StateFlow<Int> = _vipPriceOneMonth.asStateFlow()

    private val _vipPriceSixMonths = MutableStateFlow(prefs.getInt("price_6m", 50))
    val vipPriceSixMonths: StateFlow<Int> = _vipPriceSixMonths.asStateFlow()

    private val _vipPriceTwelveMonths = MutableStateFlow(prefs.getInt("price_12m", 90))
    val vipPriceTwelveMonths: StateFlow<Int> = _vipPriceTwelveMonths.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // Configurable Telegram Support contact via Admin Panel
    private val _supportTelegram = MutableStateFlow(prefs.getString("support_telegram", "@Animanhwa3d_Support") ?: "@Animanhwa3d_Support")
    val supportTelegram: StateFlow<String> = _supportTelegram.asStateFlow()

    // B Variant Payment: Linked bank cards (Humo, Uzcard, Visa)
    private val _linkedCards = MutableStateFlow<List<BankCard>>(emptyList())
    val linkedCards: StateFlow<List<BankCard>> = _linkedCards.asStateFlow()

    // Admin previewing standard mode
    private val _adminPreviewAsUser = MutableStateFlow(false)
    val adminPreviewAsUser: StateFlow<Boolean> = _adminPreviewAsUser.asStateFlow()

    // Rich Admin Simulated Users
    private val _simulatedUsers = MutableStateFlow(
        listOf(
            Triple("fozilbektoshaliyev@gmail.com", "Email", "VIP"),
            Triple("shaxzod99@gmail.com", "Email", "Standard"),
            Triple("+998 90 123 45 67", "Telefon", "VIP"),
            Triple("nodira_m@gmail.com", "Email", "Standard"),
            Triple("+998 99 765 43 21", "Telefon", "Standard"),
            Triple("bekzod_3d@gmail.com", "Email", "Standard"),
            Triple("+998 88 111 22 33", "Telefon", "Banned")
        )
    )
    val simulatedUsers: StateFlow<List<Triple<String, String, String>>> = _simulatedUsers.asStateFlow()

    // Dynamic Admins Configuration (Pair of Email to Role/Permission)
    private val _simulatedAdmins = MutableStateFlow(
        listOf(
            Pair("fozilbektoshaliyev@gmail.com", "To'liq ruxsat (Super Admin)"),
            Pair("temur_admin@gmail.com", "Kontent tahrirchi"),
            Pair("jasur_manager@gmail.com", "Foydalanuvchi boshqaruvchisi")
        )
    )
    val simulatedAdmins: StateFlow<List<Pair<String, String>>> = _simulatedAdmins.asStateFlow()

    // Admin management methods
    fun setAdminPreviewAsUser(preview: Boolean) {
        _adminPreviewAsUser.value = preview
    }

    fun addSimulatedUser(contact: String, type: String) {
        val current = _simulatedUsers.value.toMutableList()
        if (!current.any { it.first.lowercase() == contact.lowercase() }) {
            current.add(Triple(contact, type, "Standard"))
            _simulatedUsers.value = current
        }
    }

    fun setUserVipStatus(contact: String, status: String) {
        val current = _simulatedUsers.value.map {
            if (it.first == contact) {
                Triple(it.first, it.second, status)
            } else {
                it
            }
        }
        _simulatedUsers.value = current
    }

    fun toggleUserBanStatus(contact: String) {
        val current = _simulatedUsers.value.map {
            if (it.first == contact) {
                val nextStatus = if (it.third == "Banned") "Standard" else "Banned"
                Triple(it.first, it.second, nextStatus)
            } else {
                it
            }
        }
        _simulatedUsers.value = current
    }

    fun deleteSimulatedUser(contact: String) {
        val current = _simulatedUsers.value.filterIndexed { _, triple -> triple.first != contact }
        _simulatedUsers.value = current
    }

    fun addAdmin(email: String, role: String) {
        val current = _simulatedAdmins.value.toMutableList()
        if (!current.any { it.first.lowercase() == email.lowercase() }) {
            current.add(Pair(email, role))
            _simulatedAdmins.value = current
            // Also ensure they are in simulated users list
            addSimulatedUser(email, "Email")
        }
    }

    fun updateAdminRole(email: String, newRole: String) {
        val current = _simulatedAdmins.value.map {
            if (it.first == email) {
                Pair(it.first, newRole)
            } else {
                it
            }
        }
        _simulatedAdmins.value = current
    }

    fun deleteAdmin(email: String) {
        if (email != "fozilbektoshaliyev@gmail.com") {
            val current = _simulatedAdmins.value.filter { it.first != email }
            _simulatedAdmins.value = current
        }
    }

    // Dynamic Catalogs
    private val _dynamicMediaCatalog = MutableStateFlow<List<MediaItem>>(emptyList())
    val dynamicMediaCatalog: StateFlow<List<MediaItem>> = _dynamicMediaCatalog.asStateFlow()

    // Search and filter parameters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow<String?>(null) // "Anime", "Donghua", "Film"
    val selectedTypeFilter: StateFlow<String?> = _selectedTypeFilter.asStateFlow()

    private val _selectedGenreFilter = MutableStateFlow<String?>(null) // e.g., "Kultivatsiya", "3D"
    val selectedGenreFilter: StateFlow<String?> = _selectedGenreFilter.asStateFlow()

    // Live list of active downloads from SQLite via Room db Flow!
    val downloadedList: StateFlow<List<DownloadedMedia>> = repository.allDownloads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getOrGenerateUserId(contact: String): String {
        if (contact.isBlank()) return ""
        val key = "user_id_for_${contact.lowercase().trim()}"
        var id = prefs.getString(key, "") ?: ""
        if (id.isEmpty()) {
            id = (100000..999999).random().toString()
            prefs.edit().putString(key, id).apply()
        }
        return id
    }

    init {
        // Load custom or toggled catalog
        val rawData = repository.getMediaCatalog()
        // Determine VIP-only overrides if stored
        val customList = rawData.map { item ->
            val hasOverriddenVip = prefs.contains("media_vip_override_${item.id}")
            if (hasOverriddenVip) {
                item.copy(isVipOnly = prefs.getBoolean("media_vip_override_${item.id}", item.isVipOnly))
            } else {
                item
            }
        }
        _dynamicMediaCatalog.value = customList
        loadLinkedCards()

        // Load or generate 6-digit ID for the currently logged in user
        val currentContact = if (_userEmail.value.isNotEmpty()) _userEmail.value else _userPhone.value
        if (currentContact.isNotEmpty()) {
            _userId.value = getOrGenerateUserId(currentContact)
        }
    }

    // Reactive computation of filtered media list based on search and selected options
    val filteredMediaList: StateFlow<List<MediaItem>> = combine(
        _dynamicMediaCatalog,
        _searchQuery,
        _selectedTypeFilter,
        _selectedGenreFilter
    ) { catalog, query, typeOpt, genreOpt ->
        var list = catalog
        
        if (query.isNotEmpty()) {
            list = list.filter { 
                it.title.lowercase().contains(query.lowercase()) ||
                it.description.lowercase().contains(query.lowercase()) ||
                it.studio.lowercase().contains(query.lowercase())
            }
        }
        
        if (typeOpt != null) {
            list = list.filter { item -> item.type == typeOpt }
        }
        
        if (genreOpt != null) {
            list = list.filter { item -> item.genre.contains(genreOpt) }
        }
        
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Quick genre recommendations
    val availableGenres = listOf("3D", "Kultivatsiya", "Sarguzasht", "Jangari", "Sehrli", "Anime", "Dramatik")

    // Actions
    fun selectTab(tab: String) {
        _currentTab.value = tab
        // Clear secondary views
        _selectedMediaItem.value = null
        _playingOfflineMedia.value = null
        _playingOnlineMedia.value = null
    }

    fun selectMedia(item: MediaItem?) {
        _selectedMediaItem.value = item
    }

    fun playOffline(media: DownloadedMedia?) {
        _playingOfflineMedia.value = media
        if (media != null) {
            _watchTimeMinutes.value += (10..30).random()
        }
    }

    fun playOnline(media: MediaItem?, customTitle: String? = null) {
        _playingOnlineMedia.value = media
        _onlinePlaybackTitle.value = customTitle ?: media?.title
        if (media != null) {
            _watchTimeMinutes.value += (5..15).random()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilter(type: String?) {
        _selectedTypeFilter.value = type
    }

    fun setGenreFilter(genre: String?) {
        _selectedGenreFilter.value = genre
    }

    fun toggleTheme() {
        _darkThemeSelected.value = !_darkThemeSelected.value
    }

    fun updateLanguage(lang: String) {
        _userLanguage.value = lang
        prefs.edit().putString("user_language", lang).apply()
    }

    // Triggering Room Download Operations completely asynchronous and non-blocking
    fun startDownload(media: MediaItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startSimulatedDownload(media)
        }
    }

    fun deleteDownload(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (id.isEmpty()) {
                repository.clearDownloads()
            } else {
                repository.deleteDownload(id)
            }
        }
    }

    // Login/Register Management
    fun registerAndLogin(email: String, phone: String, rememberMe: Boolean) {
        _isLoggedIn.value = true
        val loggedEmail = if (email.isNotEmpty()) email else ""
        val loggedPhone = if (phone.isNotEmpty()) phone else ""
        _userEmail.value = loggedEmail
        _userPhone.value = loggedPhone
        
        val contactForId = if (loggedEmail.isNotEmpty()) loggedEmail else loggedPhone
        _userId.value = getOrGenerateUserId(contactForId)
        
        if (rememberMe) {
            val editor = prefs.edit()
            editor.putBoolean("is_logged_in", true)
            editor.putString("logged_in_email", loggedEmail)
            editor.putString("logged_in_phone", loggedPhone)
            editor.apply()
        }
        
        addSimulatedUser(contactForId, if (email.isNotEmpty()) "Email" else "Telefon")
    }

    fun updateUserProfile(nickname: String, email: String, phone: String) {
        _userNickname.value = nickname
        _userEmail.value = email
        _userPhone.value = phone
        
        val editor = prefs.edit()
        editor.putString("user_nickname", nickname)
        editor.putString("logged_in_email", email)
        editor.putString("logged_in_phone", phone)
        editor.apply()
    }

    fun logout() {
        _isLoggedIn.value = false
        _userEmail.value = ""
        _userPhone.value = ""
        _userNickname.value = ""
        _userId.value = ""
        _isAdminMode.value = false
        _isUserVip.value = false
        _vipExpiryDate.value = "Faol emas"
        
        val editor = prefs.edit()
        editor.putBoolean("is_logged_in", false)
        editor.putString("logged_in_email", "")
        editor.putString("logged_in_phone", "")
        editor.putString("user_nickname", "")
        editor.putBoolean("is_user_vip", false)
        editor.putString("vip_expiry_date", "Faol emas")
        editor.apply()
    }

    fun updateVipPrices(oneMonth: Int, sixMonths: Int, twelveMonths: Int) {
        _vipPriceOneMonth.value = oneMonth
        _vipPriceSixMonths.value = sixMonths
        _vipPriceTwelveMonths.value = twelveMonths
        
        prefs.edit()
            .putInt("price_1m", oneMonth)
            .putInt("price_6m", sixMonths)
            .putInt("price_12m", twelveMonths)
            .apply()
    }

    fun subscribeVip(months: Int) {
        _isUserVip.value = true
        val dateString = when (months) {
            1 -> "07-Iyul, 2026"
            6 -> "07-Dekabr, 2026"
            12 -> "07-Iyun, 2027"
            else -> "Muddatsiz"
        }
        val text = "$dateString gacha faol"
        _vipExpiryDate.value = text
        
        prefs.edit()
            .putBoolean("is_user_vip", true)
            .putString("vip_expiry_date", text)
            .apply()
    }

    fun cancelVip() {
        _isUserVip.value = false
        _vipExpiryDate.value = "Faol emas"
        
        prefs.edit()
            .putBoolean("is_user_vip", false)
            .putString("vip_expiry_date", "Faol emas")
            .apply()
    }

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    fun toggleMediaVipStatus(mediaId: String) {
        val currentCatalog = _dynamicMediaCatalog.value
        val updatedCatalog = currentCatalog.map { item ->
            if (item.id == mediaId) {
                val newStatus = !item.isVipOnly
                prefs.edit().putBoolean("media_vip_override_$mediaId", newStatus).apply()
                item.copy(isVipOnly = newStatus)
            } else {
                item
            }
        }
        _dynamicMediaCatalog.value = updatedCatalog
    }

    fun addNewMedia(
        title: String,
        description: String,
        type: String,
        studio: String,
        year: String,
        genres: List<String>,
        isVipOnly: Boolean,
        videoUrl: String? = null,
        imageUrl: String? = null
    ) {
        val newId = (System.currentTimeMillis() / 1000).toString()
        val finalVideoUrl = if (videoUrl.isNullOrEmpty()) "https://www.w3schools.com/html/mov_bbb.mp4" else videoUrl
        val finalImageUrl = if (imageUrl.isNullOrEmpty()) "https://images.unsplash.com/photo-1541562232579-512a21360020?w=600&auto=format&fit=crop&q=60" else imageUrl
        val newMedia = MediaItem(
            id = newId,
            title = title,
            description = description,
            imageUrl = finalImageUrl,
            videoUrl = finalVideoUrl,
            type = type,
            duration = "24 daqiqa",
            fileSize = "210 MB",
            rating = 4.8,
            studio = studio,
            year = year,
            genre = genres,
            isVipOnly = isVipOnly
        )
        val currentList = _dynamicMediaCatalog.value.toMutableList()
        currentList.add(0, newMedia)
        _dynamicMediaCatalog.value = currentList
    }

    fun addWatchTime(minutes: Int) {
        _watchTimeMinutes.value += minutes
    }

    fun updateSupportTelegram(newUsername: String) {
        val cleaned = if (newUsername.startsWith("@")) newUsername else "@$newUsername"
        _supportTelegram.value = cleaned
        prefs.edit().putString("support_telegram", cleaned).apply()
    }

    fun loadLinkedCards() {
        val serialized = prefs.getString("linked_cards_v1", "") ?: ""
        if (serialized.isEmpty()) {
            // Add initial default cards so the system is immediately responsive and looks rich
            val defaults = listOf(
                BankCard("1", "Uzcard", "8600 4820 9940 4921", "08/32", "FOZILBEK TOSHALIYEV", 450000.0),
                BankCard("2", "Humo", "9860 1205 3302 7733", "04/30", "FOZILBEK TOSHALIYEV", 1250000.0)
            )
            saveCardsToPrefs(defaults)
            _linkedCards.value = defaults
        } else {
            val list = mutableListOf<BankCard>()
            try {
                serialized.split("||").forEach { cardStr ->
                    if (cardStr.isNotEmpty()) {
                        val parts = cardStr.split("::")
                        if (parts.size >= 6) {
                            list.add(
                                BankCard(
                                    id = parts[0],
                                    cardType = parts[1],
                                    cardNumber = parts[2],
                                    expiry = parts[3],
                                    holderName = parts[4],
                                    balance = parts[5].toDoubleOrNull() ?: 0.0
                                )
                            )
                        }
                    }
                }
                _linkedCards.value = list
            } catch (e: Exception) {
                // Fail-safe default fallback
                _linkedCards.value = emptyList()
            }
        }
    }

    private fun saveCardsToPrefs(list: List<BankCard>) {
        val serialized = list.joinToString("||") {
            "${it.id}::${it.cardType}::${it.cardNumber}::${it.expiry}::${it.holderName}::${it.balance}"
        }
        prefs.edit().putString("linked_cards_v1", serialized).apply()
    }

    fun linkNewCard(type: String, rawNumber: String, expiry: String, holder: String) {
        val current = _linkedCards.value.toMutableList()
        // Format number to 'XXXX XXXX XXXX XXXX' chunks of 4 digits
        val cleaned = rawNumber.replace(" ", "").take(16)
        val formattedNumber = cleaned.chunked(4).joinToString(" ")
        
        val newCard = BankCard(
            id = System.currentTimeMillis().toString(),
            cardType = type,
            cardNumber = formattedNumber,
            expiry = expiry,
            holderName = holder.uppercase().trim(),
            balance = (150000..3000000).random().toDouble() // simulated starting balance
        )
        current.add(newCard)
        _linkedCards.value = current
        saveCardsToPrefs(current)
    }

    fun removeCard(id: String) {
        val current = _linkedCards.value.filter { it.id != id }
        _linkedCards.value = current
        saveCardsToPrefs(current)
    }

    fun deductCardBalance(cardId: String, amountUsd: Double): Boolean {
        // Exchange Rate: 1 USD = 12,800 UZS
        val amountUzsh = amountUsd * 12800.0
        var success = false
        val current = _linkedCards.value.map {
            if (it.id == cardId) {
                if (it.balance >= amountUzsh || it.cardType == "Visa") {
                    success = true
                    val nextBal = (it.balance - amountUzsh).coerceAtLeast(0.0)
                    it.copy(balance = nextBal)
                } else {
                    it
                }
            } else {
                it
            }
        }
        if (success) {
            _linkedCards.value = current
            saveCardsToPrefs(current)
        }
        return success
    }
}
