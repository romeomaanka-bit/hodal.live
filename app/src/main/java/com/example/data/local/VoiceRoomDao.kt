package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceRoomDao {
    @Query("SELECT * FROM voice_rooms ORDER BY listenersCount DESC")
    fun getAllRooms(): Flow<List<VoiceRoomEntity>>

    @Query("SELECT * FROM voice_rooms WHERE category = :category ORDER BY listenersCount DESC")
    fun getRoomsByCategory(category: String): Flow<List<VoiceRoomEntity>>

    @Query("SELECT * FROM voice_rooms WHERE roomId = :roomId LIMIT 1")
    suspend fun getRoomById(roomId: String): VoiceRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<VoiceRoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: VoiceRoomEntity)
}
