package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LudoPlayer
import com.example.ui.components.BoardColor
import com.example.ui.components.LudoGameBoard
import com.example.ui.components.LudoPiece
import com.example.ui.components.rememberDefaultPieces
import com.example.ui.theme.ActiveSpeakerPulse
import com.example.ui.theme.HodalAmber
import com.example.ui.theme.HodalDarkCard
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange

@Composable
fun LudoGameScreen(
    players: List<LudoPlayer>,
    currentDice: Int,
    isRolling: Boolean,
    collisionNotice: String?,
    onRollDice: () -> Unit,
    onBack: () -> Unit
) {
    val activePlayer = players.find { it.isTurn } ?: players.firstOrNull()
    val activeColor = when (activePlayer?.colorName?.lowercase()) {
        "blue" -> BoardColor.BLUE
        "green" -> BoardColor.GREEN
        "yellow" -> BoardColor.YELLOW
        else -> BoardColor.RED
    }

    val pieces = rememberDefaultPieces()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF0D324D),
                        Color(0xFF0F172A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("ludo_game_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: "Play Ludo Together"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Play Ludo Together 🎲",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Quickly find teammates to play games with",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Collision Alert Banner
            if (collisionNotice != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFDC2626), HodalOrange, HodalAmber)
                            )
                        )
                        .border(1.dp, HodalGold, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = collisionNotice,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Top Two Players (Blue & Red)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (players.isNotEmpty()) {
                    LudoPlayerVoiceCard(players[0])
                }
                if (players.size > 1) {
                    LudoPlayerVoiceCard(players[1])
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ludo Game Board Component with interactive pieces and dice mechanism
            LudoGameBoard(
                pieces = pieces,
                currentTurn = activeColor,
                onPieceClick = { clickedPiece ->
                    val idx = pieces.indexOfFirst { it.id == clickedPiece.id }
                    if (idx != -1) {
                        val currentStep = pieces[idx].step
                        val nextStep = if (currentStep == -1) 0 else (currentStep + currentDice).coerceAtMost(56)
                        pieces[idx] = pieces[idx].copy(step = nextStep)
                    }
                },
                diceValue = currentDice,
                isRolling = isRolling,
                onRollDice = onRollDice,
                statusMessage = collisionNotice
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Two Players (Green & Yellow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (players.size > 2) {
                    LudoPlayerVoiceCard(players[2])
                }
                if (players.size > 3) {
                    LudoPlayerVoiceCard(players[3])
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LudoPlayerVoiceCard(player: LudoPlayer) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (player.isTurn) HodalDarkCard else Color(0xFF161F30)
            )
            .border(
                1.5.dp,
                if (player.isTurn) HodalGold else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        // Player Avatar with voice mic indicator (Screenshot 7 style)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(38.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(player.colorHex))
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = player.avatarEmoji, fontSize = 16.sp)
            }

            // Green mic icon
            if (player.isMicOn) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(ActiveSpeakerPulse),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic On",
                        tint = Color.White,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = player.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "${player.colorName} • ${player.score} pts",
                color = Color.LightGray,
                fontSize = 10.sp
            )
        }
    }
}
