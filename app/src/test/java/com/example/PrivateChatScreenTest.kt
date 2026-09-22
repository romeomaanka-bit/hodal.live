package com.example

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.data.local.ChatMessageEntity
import com.example.ui.screens.PrivateChatScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PrivateChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun privateChatScreen_rendersBubblesAndHandlesInputSend() {
        val testMessages = listOf(
            ChatMessageEntity(
                id = 1L,
                senderId = "sufake",
                senderName = "sufake",
                senderAvatar = "👩‍🎤",
                recipientId = "hodal_user_1",
                messageText = "Hey Maanka! Ready for the voice party? 🎙️",
                isFromMe = false
            ),
            ChatMessageEntity(
                id = 2L,
                senderId = "hodal_user_1",
                senderName = "Maanka King",
                senderAvatar = "👑",
                recipientId = "sufake",
                messageText = "Yes! I just sent a luxury gift to the room 🚀",
                isFromMe = true
            ),
            ChatMessageEntity(
                id = 3L,
                senderId = "hodal_user_1",
                senderName = "Maanka King",
                senderAvatar = "👑",
                recipientId = "sufake",
                messageText = "Sent Super Rocket x1",
                isFromMe = true,
                isGift = true,
                giftName = "Super Rocket",
                giftCoinValue = 50000
            )
        )

        var sentMessage: String? = null
        var backClicked = false

        composeTestRule.setContent {
            PrivateChatScreen(
                messages = testMessages,
                onSendMessage = { text -> sentMessage = text },
                onBack = { backClicked = true },
                partnerName = "sufake",
                isFirestoreLive = true
            )
        }

        // Verify root container and messages list exist
        composeTestRule.onNodeWithTag("private_chat_screen", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("chat_messages_list", useUnmergedTree = true).assertExists()

        // Verify message bubbles exist: outgoing, incoming, and gift
        composeTestRule.onNodeWithTag("message_bubble_incoming", useUnmergedTree = true).assertExists()
        composeTestRule.onAllNodesWithTag("message_bubble_outgoing", useUnmergedTree = true).assertCountEquals(2)
        composeTestRule.onNodeWithTag("message_bubble_gift", useUnmergedTree = true).assertExists()

        // Verify message input field accepts text
        composeTestRule.onNodeWithTag("chat_message_input", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("chat_message_input", useUnmergedTree = true).performTextInput("See you in the room!")

        // Verify send button is present and triggers send callback
        composeTestRule.onNodeWithTag("chat_send_button", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("chat_send_button", useUnmergedTree = true).performClick()

        assertEquals("See you in the room!", sentMessage)
    }
}
