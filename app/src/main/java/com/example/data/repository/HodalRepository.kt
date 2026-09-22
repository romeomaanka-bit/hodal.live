package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.VoiceRoomEntity
import com.example.model.UserReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class HodalRepository(
    private val database: AppDatabase,
    private val userRepository: UserRepository = UserRepository(),
    private val firestoreChatRepository: FirestoreChatRepository = FirestoreChatRepository(),
    private val firestoreReportRepository: FirestoreReportRepository = FirestoreReportRepository()
) {

    private val chatDao = database.chatDao()
    private val userDao = database.userDao()
    private val voiceRoomDao = database.voiceRoomDao()

    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()
    val allRooms: Flow<List<VoiceRoomEntity>> = voiceRoomDao.getAllRooms()

    fun getRoomsByCategory(category: String): Flow<List<VoiceRoomEntity>> {
        return if (category == "All" || category == "Popular") {
            voiceRoomDao.getAllRooms()
        } else {
            voiceRoomDao.getRoomsByCategory(category)
        }
    }

    fun getChatMessages(recipientId: String): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesForRecipient(recipientId)
    }

    fun getPrivateChatMessages(currentUserId: String, otherUserId: String): Flow<List<ChatMessageEntity>> {
        return chatDao.getConversationMessages(currentUserId, otherUserId)
    }

    fun observeFirestoreChatMessages(currentUserId: String, otherUserId: String): Flow<List<ChatMessageEntity>> {
        return firestoreChatRepository.observeMessages(currentUserId, otherUserId)
    }

    suspend fun sendChatMessage(message: ChatMessageEntity) {
        chatDao.insertMessage(message)
    }

    suspend fun sendPrivateChatMessage(message: ChatMessageEntity) {
        chatDao.insertMessage(message)
        firestoreChatRepository.sendPrivateMessage(message)
    }

    suspend fun saveIncomingFirestoreMessages(messages: List<ChatMessageEntity>) {
        if (messages.isNotEmpty()) {
            chatDao.insertMessages(messages)
        }
    }

    suspend fun submitReport(report: UserReport): Result<Unit> {
        return firestoreReportRepository.submitReport(report)
    }

    suspend fun sendGift(giftCoinValue: Long): Boolean {
        val deducted = userDao.deductCoins(amount = giftCoinValue)
        if (deducted > 0) {
            userDao.getUserProfile().firstOrNull()?.let { updated ->
                userRepository.updateWalletBalance(updated.userId, updated.coins, updated.diamonds)
            }
        }
        return deducted > 0
    }

    suspend fun addRewardCoins(amount: Long) {
        userDao.addCoins(amount = amount)
        userDao.getUserProfile().firstOrNull()?.let { updated ->
            userRepository.updateWalletBalance(updated.userId, updated.coins, updated.diamonds)
        }
    }

    suspend fun syncFirebaseUserProfile(
        firebaseUid: String,
        displayName: String?,
        email: String?
    ) {
        val current = userDao.getUserProfile().firstOrNull()
        val updated = current?.copy(
            userId = if (firebaseUid.isNotBlank()) firebaseUid else current.userId,
            displayName = if (!displayName.isNullOrBlank()) displayName else current.displayName,
            hodalId = if (firebaseUid.isNotBlank()) firebaseUid.takeLast(7).uppercase() else current.hodalId
        ) ?: UserProfileEntity(
            userId = if (firebaseUid.isNotBlank()) firebaseUid else "hodal_user_1",
            displayName = displayName ?: "Hodal Star",
            username = email?.substringBefore("@") ?: "hodal_user",
            hodalId = firebaseUid.takeLast(7).uppercase(),
            level = 10,
            vipTier = 2,
            coins = 1250000L,
            diamonds = 48500L,
            country = "Somalia",
            countryFlag = "🇸🇴",
            bio = "Hodal.live Member • Firebase Verified 🔥",
            followersCount = 1200,
            followingCount = 80,
            cpPartnerName = "Pooja ❤️",
            activeFrame = "Crown"
        )
        userDao.insertOrUpdateProfile(updated)
        // Persist to Cloud Firestore via UserRepository
        userRepository.storeUserProfile(updated)
    }

    suspend fun saveProfileToFirestore(profile: UserProfileEntity): Result<Unit> {
        return userRepository.storeUserProfile(profile)
    }

    suspend fun fetchProfileFromFirestore(userId: String): UserProfileEntity? {
        val result = userRepository.getUserProfile(userId)
        val profile = result.getOrNull()
        if (profile != null) {
            userDao.insertOrUpdateProfile(profile)
        }
        return profile
    }

    suspend fun initializeSeedDataIfNeeded() {
        val existingProfile = userDao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            userDao.insertOrUpdateProfile(
                UserProfileEntity(
                    userId = "hodal_user_1",
                    displayName = "Maanka King",
                    username = "maanka_live",
                    hodalId = "9827419",
                    level = 12,
                    vipTier = 3,
                    coins = 1250000L,
                    diamonds = 48500L,
                    country = "Somalia",
                    countryFlag = "🇸🇴",
                    bio = "Hodal.live Party Host! Voice chat & Ludo champion 🎲👑",
                    followersCount = 4280,
                    followingCount = 145,
                    cpPartnerName = "Pooja ❤️",
                    activeFrame = "Crown"
                )
            )
        }

        val existingRooms = voiceRoomDao.getAllRooms().firstOrNull()
        if (existingRooms.isNullOrEmpty()) {
            val initialRooms = listOf(
                VoiceRoomEntity(
                    roomId = "room_101",
                    title = "POOJA ❤️ Live & Chill",
                    hostName = "Pooja",
                    hostAvatar = "avatar_pooja",
                    category = "Popular",
                    countryFlag = "🇮🇳",
                    countryName = "India",
                    listenersCount = 89,
                    frameType = "Crown",
                    supportScore = 1250000L,
                    roomNotice = "Welcome to Pooja's castle! Play music & relax."
                ),
                VoiceRoomEntity(
                    roomId = "room_102",
                    title = "Somali Vibes 🇸🇴 Mogadishu Party",
                    hostName = "Farhan Star",
                    hostAvatar = "avatar_somali",
                    category = "Mine",
                    countryFlag = "🇸🇴",
                    countryName = "Somalia",
                    listenersCount = 142,
                    frameType = "Fire",
                    supportScore = 999999L,
                    roomNotice = "Kusoo dhowaada qolka Hodal.live! Qosol iyo sheeko wacan."
                ),
                VoiceRoomEntity(
                    roomId = "room_103",
                    title = "RUGGED ⚡ Fast Ludo Battles",
                    hostName = "Rugged King",
                    hostAvatar = "avatar_rugged",
                    category = "Ludo",
                    countryFlag = "🇦🇪",
                    countryName = "UAE",
                    listenersCount = 54,
                    frameType = "Wings",
                    supportScore = 560000L,
                    roomNotice = "4-Player Ludo Tournament. Winner gets 50K coins!"
                ),
                VoiceRoomEntity(
                    roomId = "room_104",
                    title = "Surprise Party & Sweet CP 💕",
                    hostName = "Ayla & Noor",
                    hostAvatar = "avatar_cp",
                    category = "CP",
                    countryFlag = "🇹🇷",
                    countryName = "Turkey",
                    listenersCount = 67,
                    frameType = "Diamond",
                    supportScore = 880000L,
                    roomNotice = "Find your CP partner on Hodal.live!"
                ),
                VoiceRoomEntity(
                    roomId = "room_105",
                    title = "Global Voice Karaoke 🎤🎶",
                    hostName = "DJ Romeo",
                    hostAvatar = "avatar_dj",
                    category = "Singing",
                    countryFlag = "🇺🇸",
                    countryName = "USA",
                    listenersCount = 38,
                    frameType = "Crown",
                    supportScore = 320000L,
                    roomNotice = "Request your favorite song on mic!"
                )
            )
            voiceRoomDao.insertRooms(initialRooms)
        }

        // Add sample initial messages for private chat "sufake" (from screenshot 4)
        val sufakeMessages = chatDao.getMessagesForRecipient("sufake").firstOrNull()
        if (sufakeMessages.isNullOrEmpty()) {
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = "sufake",
                    senderName = "sufake",
                    senderAvatar = "avatar_sufake",
                    recipientId = "sufake",
                    messageText = "What are you up to?",
                    timestamp = System.currentTimeMillis() - 120000,
                    isFromMe = false
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = "sufake",
                    senderName = "sufake",
                    senderAvatar = "avatar_sufake",
                    recipientId = "sufake",
                    messageText = "I'm hosting a singing and dancing party in the room. Want to join?",
                    timestamp = System.currentTimeMillis() - 60000,
                    isFromMe = false
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = "hodal_user_1",
                    senderName = "Maanka King",
                    senderAvatar = "avatar_me",
                    recipientId = "sufake",
                    messageText = "Sure! I'm coming in now.",
                    timestamp = System.currentTimeMillis() - 20000,
                    isFromMe = true
                )
            )
        }

        // Room initial messages for room_101
        val roomMessages = chatDao.getMessagesForRecipient("room_101").firstOrNull()
        if (roomMessages.isNullOrEmpty()) {
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = "system",
                    senderName = "Hodal Official",
                    senderAvatar = "avatar_system",
                    recipientId = "room_101",
                    messageText = "🎉 Welcome to Hodal.live Castle Party Room! Please maintain respectful chat.",
                    isFromMe = false
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = "user_22",
                    senderName = "Pooja ❤️",
                    senderAvatar = "avatar_pooja",
                    recipientId = "room_101",
                    messageText = "Hello everyone! Welcome to my surprise party 👑",
                    isFromMe = false
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = "user_33",
                    senderName = "Rugged",
                    senderAvatar = "avatar_rugged",
                    recipientId = "room_101",
                    messageText = "Let's roll the Ludo dice or duel gifts! 🦁🐯",
                    isFromMe = false
                )
            )
        }
    }
}
