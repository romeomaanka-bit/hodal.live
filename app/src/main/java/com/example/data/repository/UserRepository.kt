package com.example.data.repository

import android.util.Log
import com.example.data.local.UserProfileEntity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * User repository interface defining operations to store and fetch user profiles.
 */
interface IUserRepository {
    suspend fun storeUserProfile(profile: UserProfileEntity): Result<Unit>
    suspend fun getUserProfile(userId: String): Result<UserProfileEntity?>
    fun observeUserProfile(userId: String): Flow<UserProfileEntity?>
    suspend fun updateWalletBalance(userId: String, coins: Long, diamonds: Long): Result<Unit>
}

/**
 * User repository class that abstracts Cloud Firestore operations for storing
 * and fetching user profiles using the initialized Firebase Firestore instance.
 */
open class UserRepository(
    private val firestore: FirebaseFirestore? = getInitializedFirestoreInstance()
) : IUserRepository {

    companion object {
        private const val TAG = "UserRepository"
        private const val USERS_COLLECTION = "users"

        fun getInitializedFirestoreInstance(): FirebaseFirestore? {
            return try {
                FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseFirestore instance not ready or initialized: ${e.message}")
                null
            }
        }
    }

    /**
     * Stores or updates a user profile in the Firestore "users" collection (`users/{userId}`).
     * Uses merge options to safely update existing attributes.
     */
    override suspend fun storeUserProfile(profile: UserProfileEntity): Result<Unit> {
        val db = firestore ?: return Result.failure(
            IllegalStateException("Firebase Firestore instance is not initialized")
        )
        return try {
            val docRef = db.collection(USERS_COLLECTION).document(profile.userId)
            val profileData = mapOf(
                "userId" to profile.userId,
                "displayName" to profile.displayName,
                "username" to profile.username,
                "hodalId" to profile.hodalId,
                "avatarUrl" to profile.avatarUrl,
                "level" to profile.level,
                "vipTier" to profile.vipTier,
                "coins" to profile.coins,
                "diamonds" to profile.diamonds,
                "country" to profile.country,
                "countryFlag" to profile.countryFlag,
                "bio" to profile.bio,
                "followersCount" to profile.followersCount,
                "followingCount" to profile.followingCount,
                "cpPartnerName" to profile.cpPartnerName,
                "activeFrame" to profile.activeFrame,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(profileData, SetOptions.merge()).await()
            Log.d(TAG, "Successfully stored user profile in Firestore for UID: ${profile.userId}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store user profile in Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Alias for [storeUserProfile] to maintain convenience.
     */
    suspend fun saveUserProfile(profile: UserProfileEntity): Result<Unit> = storeUserProfile(profile)

    /**
     * Fetches a user profile document from Firestore (`users/{userId}`) once.
     */
    override suspend fun getUserProfile(userId: String): Result<UserProfileEntity?> {
        val db = firestore ?: return Result.failure(
            IllegalStateException("Firebase Firestore instance is not initialized")
        )
        return try {
            val snapshot = db.collection(USERS_COLLECTION).document(userId).get().await()
            if (snapshot.exists()) {
                val userProfile = mapDocumentToProfile(snapshot)
                Log.d(TAG, "Successfully fetched user profile from Firestore for UID: $userId")
                Result.success(userProfile)
            } else {
                Log.d(TAG, "No user profile document found in Firestore for UID: $userId")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch user profile from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Alias for [getUserProfile].
     */
    suspend fun getUserProfileOnce(userId: String): Result<UserProfileEntity?> = getUserProfile(userId)

    /**
     * Observes real-time updates for a specific user profile document in Firestore.
     */
    override fun observeUserProfile(userId: String): Flow<UserProfileEntity?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = db.collection(USERS_COLLECTION).document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore snapshot listener error for user $userId: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(mapDocumentToProfile(snapshot))
                } else {
                    trySend(null)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Updates coins and diamonds wallet balances in Firestore.
     */
    override suspend fun updateWalletBalance(
        userId: String,
        coins: Long,
        diamonds: Long
    ): Result<Unit> {
        val db = firestore ?: return Result.failure(
            IllegalStateException("Firebase Firestore instance is not initialized")
        )
        return try {
            db.collection(USERS_COLLECTION).document(userId).update(
                mapOf(
                    "coins" to coins,
                    "diamonds" to diamonds,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Log.d(TAG, "Successfully updated wallet balance in Firestore for UID: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update wallet balance in Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun mapDocumentToProfile(doc: DocumentSnapshot): UserProfileEntity {
        return UserProfileEntity(
            userId = doc.getString("userId") ?: doc.id,
            displayName = doc.getString("displayName") ?: "Maanka King",
            username = doc.getString("username") ?: "maanka_live",
            hodalId = doc.getString("hodalId") ?: "9827419",
            avatarUrl = doc.getString("avatarUrl") ?: "",
            level = (doc.getLong("level") ?: 12L).toInt(),
            vipTier = (doc.getLong("vipTier") ?: 3L).toInt(),
            coins = doc.getLong("coins") ?: 1250000L,
            diamonds = doc.getLong("diamonds") ?: 48500L,
            country = doc.getString("country") ?: "Somalia",
            countryFlag = doc.getString("countryFlag") ?: "🇸🇴",
            bio = doc.getString("bio") ?: "Welcome to my Hodal.live party! 🎲✨",
            followersCount = (doc.getLong("followersCount") ?: 3420L).toInt(),
            followingCount = (doc.getLong("followingCount") ?: 180L).toInt(),
            cpPartnerName = doc.getString("cpPartnerName"),
            activeFrame = doc.getString("activeFrame") ?: "Crown"
        )
    }
}
