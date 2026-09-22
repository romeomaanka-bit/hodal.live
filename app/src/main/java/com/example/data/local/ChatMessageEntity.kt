package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val recipientId: String, // "room" for room messages, or userId for 1-on-1 private
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean,
    val isGift: Boolean = false,
    val giftName: String? = null,
    val giftCoinValue: Int = 0
)
