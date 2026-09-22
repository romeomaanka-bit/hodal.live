package com.example.model

data class VoiceSeat(
    val seatIndex: Int,
    val occupantId: String? = null,
    val occupantName: String? = null,
    val occupantAvatar: String? = null,
    val frameType: FrameType = FrameType.DEFAULT,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val isHost: Boolean = false,
    val level: Int = 1,
    val title: String = ""
)

enum class FrameType {
    DEFAULT,
    GOLDEN_WINGS,
    ROYAL_CROWN,
    DIAMOND_HEART,
    FIRE_PHOENIX,
    VIP_STAR
}

data class GiftItem(
    val id: String,
    val name: String,
    val coinPrice: Long,
    val iconRes: Int? = null,
    val emoji: String = "🎁",
    val isLuxurySpecial: Boolean = false,
    val description: String = "",
    val glowColor: Long = 0xFFFFD700
)

data class LudoPlayer(
    val id: Int,
    val name: String,
    val colorHex: Long,
    val colorName: String,
    val avatarEmoji: String,
    val isTurn: Boolean = false,
    val pawnsOnBoard: Int = 0,
    val pawnsFinished: Int = 0,
    val score: Int = 0,
    val isMicOn: Boolean = true
)

data class EmojiItem(
    val id: String,
    val symbol: String,
    val label: String
)

data class PrivateConversation(
    val userId: String,
    val name: String,
    val avatarEmoji: String,
    val level: Int,
    val status: String,
    val lastMessage: String,
    val lastTime: String,
    val unreadCount: Int = 0,
    val countryFlag: String = "🇸🇴"
)

enum class ReportTargetType {
    CHAT_MESSAGE,
    USER_PROFILE,
    INAPPROPRIATE_BEHAVIOR
}

data class UserReport(
    val reportId: String = java.util.UUID.randomUUID().toString(),
    val reporterId: String,
    val reporterName: String,
    val reportedUserId: String,
    val reportedUserName: String,
    val targetType: ReportTargetType,
    val targetContent: String? = null,
    val reason: String,
    val additionalDetails: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING"
)

