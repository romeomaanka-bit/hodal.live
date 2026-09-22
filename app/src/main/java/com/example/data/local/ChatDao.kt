package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE recipientId = :recipientId ORDER BY timestamp ASC")
    fun getMessagesForRecipient(recipientId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE (recipientId = :user1 AND senderId = :user2) OR (recipientId = :user2 AND senderId = :user1) OR recipientId = :user2 ORDER BY timestamp ASC")
    fun getConversationMessages(user1: String, user2: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("DELETE FROM chat_messages WHERE recipientId = :recipientId")
    suspend fun clearMessages(recipientId: String)
}
