package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.ui.components.ParticipantVoiceStatus
import com.example.ui.components.VoiceChatRoom
import com.example.ui.components.VoiceParticipant
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VoiceChatRoomTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun voiceChatRoom_displaysParticipantsAndTogglesMute() {
        var isMuted = true
        var toggleClicked = false

        val testParticipants = listOf(
            VoiceParticipant(
                id = "p1",
                name = "Amina Star",
                status = ParticipantVoiceStatus.SPEAKING,
                isHost = true
            ),
            VoiceParticipant(
                id = "p2",
                name = "Khadar DJ",
                status = ParticipantVoiceStatus.MUTED
            ),
            VoiceParticipant(
                id = "p3",
                name = "Sahra Fan",
                status = ParticipantVoiceStatus.LISTENING
            )
        )

        composeTestRule.setContent {
            var localMuted by remember { mutableStateOf(isMuted) }
            VoiceChatRoom(
                participants = testParticipants,
                isMuted = localMuted,
                onToggleMute = {
                    localMuted = !localMuted
                    toggleClicked = true
                },
                roomTitle = "Somali Beats Party"
            )
        }

        // Verify room and participants are displayed
        composeTestRule.onNodeWithTag("voice_chat_room").assertIsDisplayed()
        composeTestRule.onNodeWithText("Somali Beats Party").assertIsDisplayed()
        composeTestRule.onNodeWithTag("participant_item_p1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("participant_item_p2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("participant_item_p3").assertIsDisplayed()

        // Verify status chips
        composeTestRule.onNodeWithText("Speaking").assertIsDisplayed()
        composeTestRule.onNodeWithText("Muted").assertIsDisplayed()
        composeTestRule.onNodeWithText("Listening").assertIsDisplayed()

        // Verify mute toggle button
        composeTestRule.onNodeWithTag("mute_unmute_toggle_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("mute_unmute_toggle_button").performClick()
        assertTrue(toggleClicked)
    }
}
