package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    fun getUserProfile(userId: String = "hodal_user_1"): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET coins = coins - :amount WHERE userId = :userId AND coins >= :amount")
    suspend fun deductCoins(userId: String = "hodal_user_1", amount: Long): Int

    @Query("UPDATE user_profile SET coins = coins + :amount WHERE userId = :userId")
    suspend fun addCoins(userId: String = "hodal_user_1", amount: Long)
}
