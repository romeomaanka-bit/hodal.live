package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.HodalAuthManager
import com.example.data.auth.HodalAuthState
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.VoiceRoomEntity
import com.example.data.repository.HodalRepository
import com.example.model.FrameType
import com.example.model.GiftItem
import com.example.model.LudoPlayer
import com.example.model.ReportTargetType
import com.example.model.UserReport
import com.example.model.VoiceSeat
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class HodalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HodalRepository
    private val authManager = HodalAuthManager(application)

    val authState: StateFlow<HodalAuthState> = authManager.authState
    val currentFirebaseUser: StateFlow<FirebaseUser?> = authManager.currentUser

    init {
        val database = AppDatabase.getDatabase(application)
        repository = HodalRepository(database)
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
        viewModelScope.launch {
            repository.observeFirestoreChatMessages("hodal_user_1", "sufake").collect { firestoreMessages ->
                if (firestoreMessages.isNotEmpty()) {
                    repository.saveIncomingFirestoreMessages(firestoreMessages)
                }
            }
        }
        viewModelScope.launch {
            authManager.currentUser.collect { user ->
                if (user != null) {
                    _isLoggedIn.value = true
                    repository.syncFirebaseUserProfile(
                        firebaseUid = user.uid,
                        displayName = user.displayName,
                        email = user.email
                    )
                }
            }
        }
        startSimulatedVoiceChatter()
    }

    // Auth State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // User Profile
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Room Exploration Categories
    private val _selectedCategory = MutableStateFlow("Popular")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val roomsList: StateFlow<List<VoiceRoomEntity>> = _selectedCategory
        .flatMapLatest { cat -> repository.getRoomsByCategory(cat) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Active Room
    private val _activeRoom = MutableStateFlow<VoiceRoomEntity?>(null)
    val activeRoom: StateFlow<VoiceRoomEntity?> = _activeRoom.asStateFlow()

    // 8 Voice Seats in the Castle Party Room (Screenshot 6 style)
    private val _partySeats = MutableStateFlow<List<VoiceSeat>>(
        listOf(
            VoiceSeat(0, "user_101", "Pooja ❤️", "👩‍🦰", FrameType.ROYAL_CROWN, isSpeaking = true, isHost = true, level = 18, title = "Host"),
            VoiceSeat(1, "user_102", "Farhan", "🧔", FrameType.FIRE_PHOENIX, isSpeaking = false, level = 12, title = "No.1"),
            VoiceSeat(2, "user_103", "VIP Nắm", "👸", FrameType.GOLDEN_WINGS, isSpeaking = false, level = 15, title = "VIP Nắm"),
            VoiceSeat(3, "user_104", "Rabbit", "🐰", FrameType.DIAMOND_HEART, isSpeaking = false, level = 8, title = "Rabbit"),
            VoiceSeat(4, "user_105", "Rugged", "🦁", FrameType.VIP_STAR, isSpeaking = false, level = 14, title = "No.5"),
            VoiceSeat(5, null, null, null, FrameType.DEFAULT, title = "No.6"),
            VoiceSeat(6, null, null, null, FrameType.DEFAULT, title = "No.7"),
            VoiceSeat(7, null, null, null, FrameType.DEFAULT, title = "No.8")
        )
    )
    val partySeats: StateFlow<List<VoiceSeat>> = _partySeats.asStateFlow()

    // Current room messages
    val roomMessages: StateFlow<List<ChatMessageEntity>> = repository.getChatMessages("room_101")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 1-on-1 Private Messages with Sufake (Screenshot 4 style, backed by Room & Cloud Firestore)
    val privateMessagesWithSufake: StateFlow<List<ChatMessageEntity>> = repository.getPrivateChatMessages("hodal_user_1", "sufake")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Mic Status
    private val _isMicMuted = MutableStateFlow(false)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    // Luxury Gift Animation Overlay State (Screenshot 5)
    private val _activeGiftEffect = MutableStateFlow<GiftItem?>(null)
    val activeGiftEffect: StateFlow<GiftItem?> = _activeGiftEffect.asStateFlow()

    private val _giftSenderName = MutableStateFlow("Maanka King")
    val giftSenderName: StateFlow<String> = _giftSenderName.asStateFlow()

    private val _giftReceiverName = MutableStateFlow("Pooja ❤️")
    val giftReceiverName: StateFlow<String> = _giftReceiverName.asStateFlow()

    private val _giftComboCount = MutableStateFlow(66)
    val giftComboCount: StateFlow<Int> = _giftComboCount.asStateFlow()

    // Ludo Interactive Game State (Screenshots 7, 8, 9)
    private val _ludoPlayers = MutableStateFlow(
        listOf(
            LudoPlayer(0, "Maanka", 0xFF2563EB, "Blue", "🧔", isTurn = true, pawnsOnBoard = 2, pawnsFinished = 1, score = 340),
            LudoPlayer(1, "Pooja", 0xFFEF4444, "Red", "👩‍🦰", isTurn = false, pawnsOnBoard = 1, pawnsFinished = 0, score = 210),
            LudoPlayer(2, "Rugged", 0xFF10B981, "Green", "🦁", isTurn = false, pawnsOnBoard = 2, pawnsFinished = 0, score = 190),
            LudoPlayer(3, "Ayla", 0xFFF59E0B, "Yellow", "👸", isTurn = false, pawnsOnBoard = 1, pawnsFinished = 2, score = 420)
        )
    )
    val ludoPlayers: StateFlow<List<LudoPlayer>> = _ludoPlayers.asStateFlow()

    private val _currentDiceValue = MutableStateFlow(6)
    val currentDiceValue: StateFlow<Int> = _currentDiceValue.asStateFlow()

    private val _isDiceRolling = MutableStateFlow(false)
    val isDiceRolling: StateFlow<Boolean> = _isDiceRolling.asStateFlow()

    private val _ludoCollisionNotice = MutableStateFlow<String?>("POW! 💥 Blue captured Red's pawn!")
    val ludoCollisionNotice: StateFlow<String?> = _ludoCollisionNotice.asStateFlow()

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun enterRoom(room: VoiceRoomEntity) {
        _activeRoom.value = room
    }

    fun leaveRoom() {
        _activeRoom.value = null
    }

    fun toggleMic() {
        _isMicMuted.value = !_isMicMuted.value
        // Update user's seat if seated
        val currentList = _partySeats.value.toMutableList()
        val userSeatIdx = currentList.indexOfFirst { it.occupantId == "hodal_user_1" }
        if (userSeatIdx != -1) {
            val s = currentList[userSeatIdx]
            currentList[userSeatIdx] = s.copy(
                isMuted = _isMicMuted.value,
                isSpeaking = !_isMicMuted.value
            )
            _partySeats.value = currentList
        }
    }

    fun takeOrLeaveSeat(seatIndex: Int) {
        val currentList = _partySeats.value.toMutableList()
        val existingSeatIdx = currentList.indexOfFirst { it.occupantId == "hodal_user_1" }

        if (existingSeatIdx == seatIndex) {
            // Leave seat
            currentList[seatIndex] = currentList[seatIndex].copy(
                occupantId = null,
                occupantName = null,
                occupantAvatar = null,
                isSpeaking = false,
                isMuted = false
            )
        } else {
            // If already on another seat, clear it
            if (existingSeatIdx != -1) {
                currentList[existingSeatIdx] = currentList[existingSeatIdx].copy(
                    occupantId = null,
                    occupantName = null,
                    occupantAvatar = null,
                    isSpeaking = false
                )
            }
            // Take this seat
            val profile = userProfile.value
            currentList[seatIndex] = currentList[seatIndex].copy(
                occupantId = "hodal_user_1",
                occupantName = profile?.displayName ?: "Maanka King",
                occupantAvatar = "👑",
                frameType = FrameType.ROYAL_CROWN,
                isSpeaking = !_isMicMuted.value,
                isMuted = _isMicMuted.value,
                level = profile?.level ?: 12
            )
        }
        _partySeats.value = currentList
    }

    fun sendRoomMessage(text: String) {
        if (text.isBlank()) return
        val profile = userProfile.value
        viewModelScope.launch {
            repository.sendChatMessage(
                ChatMessageEntity(
                    senderId = "hodal_user_1",
                    senderName = profile?.displayName ?: "Maanka King",
                    senderAvatar = "👑",
                    recipientId = "room_101",
                    messageText = text.trim(),
                    isFromMe = true
                )
            )
        }
    }

    fun sendPrivateMessage(recipientId: String, text: String) {
        if (text.isBlank()) return
        val profile = userProfile.value
        val myId = profile?.userId ?: "hodal_user_1"
        viewModelScope.launch {
            repository.sendPrivateChatMessage(
                ChatMessageEntity(
                    senderId = myId,
                    senderName = profile?.displayName ?: "Maanka King",
                    senderAvatar = "👑",
                    recipientId = recipientId,
                    messageText = text.trim(),
                    isFromMe = true
                )
            )

            // Simulate quick friendly reply after 1.5 seconds
            delay(1500)
            val reply = when (Random.nextInt(4)) {
                0 -> "Awesome! Let's party in Hodal voice room 💖"
                1 -> "Ready for a Ludo rematch? Roll the dice! 🎲"
                2 -> "Thank you! Hodal.live is so much fun today ✨"
                else -> "Got your message! See you on stage 🎙️👑"
            }
            repository.sendChatMessage(
                ChatMessageEntity(
                    senderId = recipientId,
                    senderName = "sufake",
                    senderAvatar = "👩‍🎤",
                    recipientId = recipientId,
                    messageText = reply,
                    isFromMe = false
                )
            )
        }
    }

    private val _reportSubmissionStatus = MutableStateFlow<String?>(null)
    val reportSubmissionStatus: StateFlow<String?> = _reportSubmissionStatus.asStateFlow()

    fun submitReport(
        reportedUserId: String,
        reportedUserName: String,
        targetType: ReportTargetType,
        targetContent: String? = null,
        reason: String,
        additionalDetails: String = "",
        onComplete: (Boolean) -> Unit = {}
    ) {
        val current = userProfile.value
        val reporterId = current?.userId ?: "hodal_user_1"
        val reporterName = current?.displayName ?: "Maanka King"
        val report = UserReport(
            reporterId = reporterId,
            reporterName = reporterName,
            reportedUserId = reportedUserId,
            reportedUserName = reportedUserName,
            targetType = targetType,
            targetContent = targetContent,
            reason = reason,
            additionalDetails = additionalDetails
        )

        viewModelScope.launch {
            val result = repository.submitReport(report)
            _reportSubmissionStatus.value = "Report for $reportedUserName submitted to Firestore"
            onComplete(result.isSuccess)
        }
    }

    fun clearReportStatus() {
        _reportSubmissionStatus.value = null
    }

    fun triggerSendGift(gift: GiftItem, combo: Int) {
        val totalCost = gift.coinPrice * combo
        val currentCoins = userProfile.value?.coins ?: 0L
        if (currentCoins < totalCost) {
            // Add automatic bonus coins so the user can enjoy the gift effect!
            viewModelScope.launch {
                repository.addRewardCoins(totalCost + 100000)
                executeGift(gift, combo)
            }
        } else {
            executeGift(gift, combo)
        }
    }

    private fun executeGift(gift: GiftItem, combo: Int) {
        viewModelScope.launch {
            repository.sendGift(gift.coinPrice * combo)
            _giftSenderName.value = userProfile.value?.displayName ?: "Maanka King"
            _giftReceiverName.value = _activeRoom.value?.hostName ?: "Pooja ❤️"
            _giftComboCount.value = combo
            _activeGiftEffect.value = gift

            // Also post in room chat
            repository.sendChatMessage(
                ChatMessageEntity(
                    senderId = "hodal_user_1",
                    senderName = userProfile.value?.displayName ?: "Maanka King",
                    senderAvatar = "👑",
                    recipientId = "room_101",
                    messageText = "sent ${gift.name} x$combo to ${giftReceiverName.value}!",
                    isFromMe = true,
                    isGift = true,
                    giftName = gift.name,
                    giftCoinValue = (gift.coinPrice * combo).toInt()
                )
            )
        }
    }

    fun dismissGiftEffect() {
        _activeGiftEffect.value = null
    }

    fun rollLudoDice() {
        if (_isDiceRolling.value) return
        viewModelScope.launch {
            _isDiceRolling.value = true
            // Simulate rolling animation frames
            for (i in 1..6) {
                _currentDiceValue.value = Random.nextInt(1, 7)
                delay(80)
            }
            val finalDice = Random.nextInt(1, 7)
            _currentDiceValue.value = finalDice
            _isDiceRolling.value = false

            // Move turn or trigger capture event
            val players = _ludoPlayers.value.toMutableList()
            val currentTurnIdx = players.indexOfFirst { it.isTurn }
            if (finalDice == 6) {
                _ludoCollisionNotice.value = "💥 POW! Lucky 6! ${players[currentTurnIdx].name} knocked opponent's pawn home!"
            } else {
                _ludoCollisionNotice.value = "${players[currentTurnIdx].name} rolled $finalDice and moved forward!"
                val nextTurn = (currentTurnIdx + 1) % players.size
                for (i in players.indices) {
                    players[i] = players[i].copy(isTurn = (i == nextTurn))
                }
                _ludoPlayers.value = players
            }
        }
    }

    fun rechargeCoins(amount: Long = 500000L) {
        viewModelScope.launch {
            repository.addRewardCoins(amount)
        }
    }

    fun signInWithGoogle(activityContext: Context, serverClientId: String = "") {
        authManager.signInWithGoogle(activityContext, serverClientId) { success, _ ->
            if (success) {
                _isLoggedIn.value = true
            }
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        authManager.signInWithEmail(email, pass) { success, _ ->
            if (success) {
                _isLoggedIn.value = true
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String, displayName: String) {
        authManager.signUpWithEmail(email, pass, displayName) { success, _ ->
            if (success) {
                _isLoggedIn.value = true
            }
        }
    }

    fun signInQuick(preferredName: String = "Maanka King") {
        authManager.signInQuick(preferredName) { success, _ ->
            if (success) {
                _isLoggedIn.value = true
            }
        }
    }

    fun clearAuthError() {
        authManager.clearError()
    }

    fun syncProfileToFirestore() {
        viewModelScope.launch {
            userProfile.value?.let { profile ->
                repository.saveProfileToFirestore(profile)
            }
        }
    }

    fun fetchProfileFromFirestore() {
        viewModelScope.launch {
            val uid = currentFirebaseUser.value?.uid ?: "hodal_user_1"
            repository.fetchProfileFromFirestore(uid)
        }
    }

    fun login() {
        signInQuick("Maanka King")
    }

    fun logout() {
        authManager.signOut {
            _isLoggedIn.value = false
        }
    }

    private fun startSimulatedVoiceChatter() {
        viewModelScope.launch {
            while (true) {
                delay(3000)
                val currentSeats = _partySeats.value.toMutableList()
                val occupiedIndexes = currentSeats.indices.filter {
                    currentSeats[it].occupantId != null && currentSeats[it].occupantId != "hodal_user_1"
                }
                if (occupiedIndexes.isNotEmpty()) {
                    val speakingIdx = occupiedIndexes.random()
                    for (i in currentSeats.indices) {
                        if (currentSeats[i].occupantId != "hodal_user_1") {
                            currentSeats[i] = currentSeats[i].copy(isSpeaking = (i == speakingIdx))
                        }
                    }
                    _partySeats.value = currentSeats
                }
            }
        }
    }
}
