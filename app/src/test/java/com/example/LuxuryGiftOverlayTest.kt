package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.model.GiftItem
import com.example.ui.components.LuxuryGiftOverlay
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LuxuryGiftOverlayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun luxuryGiftOverlay_displaysAnimatedIconsAndHandlesDismiss() {
        var dismissed = false
        val testGift = GiftItem(
            id = "gift_rocket",
            name = "Super Rocket",
            coinPrice = 5000,
            emoji = "🚀",
            description = "Blast the room with cosmic energy",
            glowColor = 0xFF00E5FF
        )

        composeTestRule.setContent {
            LuxuryGiftOverlay(
                gift = testGift,
                senderName = "Maanka King",
                receiverName = "Pooja ❤️",
                comboCount = 66,
                autoDismissDurationMs = 10000L,
                onDismiss = { dismissed = true }
            )
        }

        // Verify overlay container is displayed
        composeTestRule.onNodeWithTag("luxury_gift_overlay", useUnmergedTree = true).assertExists()

        // Verify sender and receiver trajectory delivery pill is displayed
        composeTestRule.onNodeWithTag("gift_sender_receiver_pill", useUnmergedTree = true).assertExists()

        // Verify main animated gift icon container is displayed
        composeTestRule.onNodeWithTag("luxury_gift_animated_icon", useUnmergedTree = true).assertExists()

        // Verify combo counter badge is displayed
        composeTestRule.onNodeWithTag("gift_combo_tag", useUnmergedTree = true).assertExists()

        // Verify close button triggers dismissal callback
        composeTestRule.onNodeWithTag("close_gift_overlay_button", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("close_gift_overlay_button", useUnmergedTree = true).performClick()
        assertTrue(dismissed)
    }
}
