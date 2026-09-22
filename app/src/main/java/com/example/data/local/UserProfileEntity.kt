package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val userId: String = "hodal_user_1",
    val displayName: String = "Maanka King",
    val username: String = "maanka_live",
    val hodalId: String = "9827419",
    val avatarUrl: String = "",
    val level: Int = 12,
    val vipTier: Int = 3,
    val coins: Long = 1250000L,
    val diamonds: Long = 48500L,
    val country: String = "Somalia",
    val countryFlag: String = "🇸🇴",
    val bio: String = "Welcome to my Hodal.live party! Join mic & let's play Ludo 🎲✨",
    val followersCount: Int = 3420,
    val followingCount: Int = 180,
    val cpPartnerName: String? = "Pooja ❤️",
    val activeFrame: String = "Crown" // Crown, Wings, Diamond, Fire
)
