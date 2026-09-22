package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoiceSeat
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange

@Composable
fun VoiceSeatItem(
    seat: VoiceSeat,
    onSeatClick: (VoiceSeat) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onSeatClick(seat) }
            .padding(4.dp)
            .testTag("voice_seat_${seat.seatIndex}")
    ) {
        if (seat.occupantName != null) {
            // Occupied Seat
            HodalAvatar(
                name = seat.occupantName,
                avatarEmoji = seat.occupantAvatar ?: "🎙️",
                frameType = seat.frameType,
                isSpeaking = seat.isSpeaking,
                isMuted = seat.isMuted,
                level = seat.level,
                showCrown = seat.isHost,
                size = if (seat.isHost) 54.dp else 46.dp
            )
        } else {
            // Empty Seat with (+) button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Empty Seat",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = seat.occupantName ?: seat.title.ifEmpty { "Seat ${seat.seatIndex + 1}" },
            color = if (seat.isHost) HodalGold else Color.White,
            fontWeight = if (seat.isHost) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
