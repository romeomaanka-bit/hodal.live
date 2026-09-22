package com.example

import com.example.data.local.UserProfileEntity
import com.example.data.repository.IUserRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryTest {

    @Test
    fun userRepository_implementsInterface() {
        val repo: IUserRepository = UserRepository(firestore = null)
        assertNotNull(repo)
    }

    @Test
    fun userRepository_handlesNullFirestoreGracefully() = runBlocking {
        val repo = UserRepository(firestore = null)
        val profile = UserProfileEntity(
            userId = "test_user_1",
            displayName = "Tester",
            username = "tester1",
            hodalId = "TEST123",
            avatarUrl = "",
            level = 1,
            vipTier = 1,
            coins = 1000L,
            diamonds = 100L,
            country = "Somalia",
            countryFlag = "🇸🇴",
            bio = "Testing",
            followersCount = 0,
            followingCount = 0,
            cpPartnerName = null,
            activeFrame = "Crown"
        )

        val storeResult = repo.storeUserProfile(profile)
        assertFalse(storeResult.isSuccess)
        assertTrue(storeResult.isFailure)

        val fetchResult = repo.getUserProfile("test_user_1")
        assertFalse(fetchResult.isSuccess)
        assertTrue(fetchResult.isFailure)
    }
}
