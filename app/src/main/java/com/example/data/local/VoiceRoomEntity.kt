package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_rooms")
data class VoiceRoomEntity(
    @PrimaryKey
    val roomId: String,
    val title: String,
    val hostName: String,
    val hostAvatar: String,
    val category: String, // "Popular", "Mine", "CP", "Ludo", "Singing"
    val countryFlag: String,
    val countryName: String,
    val listenersCount: Int,
    val isLive: Boolean = true,
    val frameType: String = "GoldenWings",
    val roomNotice: String = "Welcome! Be polite and respect speakers.",
    val supportScore: Long = 999999L
)
