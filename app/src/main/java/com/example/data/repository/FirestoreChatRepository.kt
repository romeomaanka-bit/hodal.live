package com.example.data.repository

import android.util.Log
import com.example.data.local.ChatMessageEntity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Interface for real-time private messaging operations using Cloud Firestore.
 */
interface IFirestoreChatRepository {
    fun observeMessages(currentUserId: String, otherUserId: String): Flow<List<ChatMessageEntity>>
    suspend fun sendPrivateMessage(message: ChatMessageEntity): Result<Unit>
    fun getChannelId(userId1: String, userId2: String): String
}

/**
 * Concrete implementation of [IFirestoreChatRepository] using Firebase Firestore
 * to facilitate real-time chat between users in the Hodal.live application.
 */
class FirestoreChatRepository(
    private val firestore: FirebaseFirestore? = UserRepository.getInitializedFirestoreInstance()
) : IFirestoreChatRepository {

    companion object {
        private const val TAG = "FirestoreChatRepository"
        private const val CHATS_COLLECTION = "chats"
        private const val MESSAGES_COLLECTION = "messages"
    }

    /**
     * Computes a deterministic bidirectional chat channel identifier for two users.
     */
    override fun getChannelId(userId1: String, userId2: String): String {
        return if (userId1 <= userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
    }

    /**
     * Observes real-time messages in a private 1-on-1 chat channel via Firestore snapshot listeners.
     */
    override fun observeMessages(
        currentUserId: String,
        otherUserId: String
    ): Flow<List<ChatMessageEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            Log.w(TAG, "Firestore instance not available for observeMessages, falling back.")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val channelId = getChannelId(currentUserId, otherUserId)
        val messagesRef = db.collection(CHATS_COLLECTION)
            .document(channelId)
            .collection(MESSAGES_COLLECTION)
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val registration = messagesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "SnapshotListener failed for channel $channelId: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val messages = snapshot.documents.mapNotNull { doc ->
                    mapDocumentToChatMessage(doc, currentUserId)
                }
                trySend(messages)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose { registration.remove() }
    }

    /**
     * Sends a private message to Firestore, writing to both the messages subcollection
     * and updating the parent chat metadata document.
     */
    override suspend fun sendPrivateMessage(message: ChatMessageEntity): Result<Unit> {
        val db = firestore ?: return Result.failure(
            IllegalStateException("Firestore instance is not initialized")
        )

        return try {
            val channelId = getChannelId(message.senderId, message.recipientId)
            val channelDocRef = db.collection(CHATS_COLLECTION).document(channelId)
            val messageDocRef = channelDocRef.collection(MESSAGES_COLLECTION).document()

            val messageData = mapOf(
                "id" to messageDocRef.id,
                "senderId" to message.senderId,
                "senderName" to message.senderName,
                "senderAvatar" to message.senderAvatar,
                "recipientId" to message.recipientId,
                "messageText" to message.messageText,
                "timestamp" to message.timestamp,
                "serverTimestamp" to FieldValue.serverTimestamp(),
                "isGift" to message.isGift,
                "giftName" to (message.giftName ?: ""),
                "giftCoinValue" to message.giftCoinValue
            )

            // Update parent conversation metadata
            val conversationMetadata = mapOf(
                "channelId" to channelId,
                "participants" to listOf(message.senderId, message.recipientId),
                "lastMessage" to message.messageText,
                "lastSenderId" to message.senderId,
                "lastSenderName" to message.senderName,
                "lastTimestamp" to message.timestamp,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            // Write both in a batch or sequential
            channelDocRef.set(conversationMetadata, SetOptions.merge()).await()
            messageDocRef.set(messageData).await()

            Log.d(TAG, "Successfully sent message to Firestore in channel: $channelId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending private message to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun mapDocumentToChatMessage(
        doc: DocumentSnapshot,
        currentUserId: String
    ): ChatMessageEntity? {
        val text = doc.getString("messageText") ?: return null
        val senderId = doc.getString("senderId") ?: ""
        val recipientId = doc.getString("recipientId") ?: ""
        val senderName = doc.getString("senderName") ?: "User"
        val senderAvatar = doc.getString("senderAvatar") ?: "👤"
        val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
        val isGift = doc.getBoolean("isGift") ?: false
        val giftName = doc.getString("giftName")
        val giftCoinValue = (doc.getLong("giftCoinValue") ?: 0L).toInt()

        return ChatMessageEntity(
            id = doc.id.hashCode().toLong(),
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            recipientId = recipientId,
            messageText = text,
            timestamp = timestamp,
            isFromMe = senderId == currentUserId,
            isGift = isGift,
            giftName = giftName,
            giftCoinValue = giftCoinValue
        )
    }
}
