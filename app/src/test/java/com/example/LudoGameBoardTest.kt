package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.ui.components.BoardColor
import com.example.ui.components.LudoGameBoard
import com.example.ui.components.createDefaultLudoPieces
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LudoGameBoardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun ludoGameBoard_rendersBoardPiecesAndDiceRoll() {
        var rollClicked = false
        val pieces = createDefaultLudoPieces()

        composeTestRule.setContent {
            LudoGameBoard(
                pieces = pieces,
                currentTurn = BoardColor.RED,
                diceValue = 6,
                isRolling = false,
                onRollDice = { rollClicked = true }
            )
        }

        // Verify board card container & canvas are rendered
        composeTestRule.onNodeWithTag("ludo_game_board_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ludo_board_canvas_container").assertIsDisplayed()

        // Verify interactive dice is displayed
        composeTestRule.onNodeWithTag("ludo_dice_interactive").assertIsDisplayed()

        // Verify roll dice action button works
        composeTestRule.onNodeWithTag("roll_dice_action_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("roll_dice_action_button").performClick()
        assertTrue(rollClicked)
    }
}
