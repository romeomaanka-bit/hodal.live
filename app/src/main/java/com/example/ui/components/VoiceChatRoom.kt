package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FrameType
import com.example.ui.theme.ActiveSpeakerPulse
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange
import com.example.ui.theme.HodalRose
import com.example.ui.theme.HodalRoyalBlue
import com.example.ui.theme.NeonCyan

/**
 * Voice activity states for participants in a voice chat room.
 */
enum class ParticipantVoiceStatus {
    SPEAKING,
    MUTED,
    LISTENING,
    CONNECTING
}

/**
 * Data model representing an active participant in the voice room.
 */
data class VoiceParticipant(
    val id: String,
    val name: String,
    val avatarEmoji: String = "🎙️",
    val avatarUrl: String? = null,
    val status: ParticipantVoiceStatus = ParticipantVoiceStatus.LISTENING,
    val isHost: Boolean = false,
    val level: Int = 1,
    val frameType: FrameType = FrameType.DEFAULT,
    val role: String = if (isHost) "Host" else "Speaker"
)

/**
 * Reusable VoiceChatRoom component in Compose that displays a list of active participants
 * with their current status and a mute/unmute toggle.
 *
 * @param participants List of active users in the voice room
 * @param isMuted Current local microphone mute state
 * @param onToggleMute Callback invoked when the user toggles their mute state
 * @param modifier Modifier for styling and layout
 * @param roomTitle Title of the voice chat room
 * @param roomCategory Category or topic pill (e.g. "Gaming", "Music", "Social")
 * @param onParticipantClick Optional click callback when a participant is selected
 * @param onLeaveRoom Optional callback when leaving the room
 */
@Composable
fun VoiceChatRoom(
    participants: List<VoiceParticipant>,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier,
    roomTitle: String = "Voice Party Room",
    roomCategory: String = "Live Party",
    onParticipantClick: (VoiceParticipant) -> Unit = {},
    onLeaveRoom: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_chat_room"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1E1B4B),
                            Color(0xFF0F172A),
                            Color(0xFF090D16)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Header Section: Room Title, Status, and Participant Count
            VoiceChatRoomHeader(
                title = roomTitle,
                category = roomCategory,
                activeCount = participants.size,
                onLeaveRoom = onLeaveRoom
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Participants List Section with individual statuses
            Text(
                text = "ACTIVE PARTICIPANTS (${participants.size})",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .testTag("participants_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                if (participants.isEmpty()) {
                    item {
                        EmptyParticipantsPlaceholder()
                    }
                } else {
                    items(participants, key = { it.id }) { participant ->
                        ParticipantItemCard(
                            participant = participant,
                            onClick = { onParticipantClick(participant) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Controls Bar with Mute/Unmute Toggle
            VoiceChatRoomControls(
                isMuted = isMuted,
                onToggleMute = onToggleMute,
                onLeaveRoom = onLeaveRoom
            )
        }
    }
}

/**
 * Top header displaying room info and live indicators.
 */
@Composable
private fun VoiceChatRoomHeader(
    title: String,
    category: String,
    activeCount: Int,
    onLeaveRoom: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Live indicator dot
                PulsingLiveDot()
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LIVE",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Category Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HodalRoyalBlue.copy(alpha = 0.35f))
                        .border(1.dp, HodalRoyalBlue.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = category,
                        color = Color(0xFF93C5FD),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Active participant counter chip
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = HodalGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$activeCount",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (onLeaveRoom != null) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onLeaveRoom,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(HodalRose.copy(alpha = 0.2f))
                    .testTag("leave_room_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "Leave Voice Room",
                    tint = HodalRose,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Individual participant card row with status indicators and wave animation when speaking.
 */
@Composable
private fun ParticipantItemCard(
    participant: VoiceParticipant,
    onClick: () -> Unit
) {
    val isSpeaking = participant.status == ParticipantVoiceStatus.SPEAKING
    val isMuted = participant.status == ParticipantVoiceStatus.MUTED

    val cardBorderColor by animateColorAsState(
        targetValue = when (participant.status) {
            ParticipantVoiceStatus.SPEAKING -> ActiveSpeakerPulse
            ParticipantVoiceStatus.MUTED -> Color(0xFF475569)
            ParticipantVoiceStatus.LISTENING -> Color(0xFF1E293B)
            ParticipantVoiceStatus.CONNECTING -> HodalOrange.copy(alpha = 0.6f)
        },
        label = "participant_border"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("participant_item_${participant.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Participant Avatar with speaking ripple
            HodalAvatar(
                name = participant.name,
                avatarEmoji = participant.avatarEmoji,
                frameType = participant.frameType,
                isSpeaking = isSpeaking,
                isMuted = isMuted,
                level = participant.level,
                showCrown = participant.isHost,
                size = 46.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Name and Role
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = participant.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (participant.isHost) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(HodalGold)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "HOST",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = participant.role,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Speaking Sound Wave (if speaking) or Status Pill
            if (isSpeaking) {
                AudioWaveformVisualizer()
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Current Status Pill
            ParticipantStatusBadge(status = participant.status)
        }
    }
}

/**
 * Status badge displaying Speaking, Muted, Listening, or Connecting.
 */
@Composable
private fun ParticipantStatusBadge(status: ParticipantVoiceStatus) {
    val (bgColor, textColor, icon, label) = when (status) {
        ParticipantVoiceStatus.SPEAKING -> Quadruple(
            Color(0xFF065F46),
            Color(0xFF34D399),
            Icons.Default.Mic,
            "Speaking"
        )
        ParticipantVoiceStatus.MUTED -> Quadruple(
            HodalRose.copy(alpha = 0.2f),
            HodalRose,
            Icons.Default.MicOff,
            "Muted"
        )
        ParticipantVoiceStatus.LISTENING -> Quadruple(
            Color(0xFF0F172A),
            Color(0xFF94A3B8),
            Icons.Default.Headphones,
            "Listening"
        )
        ParticipantVoiceStatus.CONNECTING -> Quadruple(
            HodalOrange.copy(alpha = 0.2f),
            HodalOrange,
            Icons.Default.Sync,
            "Connecting"
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Bottom controls bar with the prominent Mute/Unmute microphone toggle button.
 */
@Composable
private fun VoiceChatRoomControls(
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onLeaveRoom: (() -> Unit)?
) {
    val micButtonColor by animateColorAsState(
        targetValue = if (isMuted) HodalRose else Color(0xFF10B981),
        label = "mic_button_color"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Mute / Unmute Interactive Toggle Button
            Button(
                onClick = onToggleMute,
                colors = ButtonDefaults.buttonColors(
                    containerColor = micButtonColor
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("mute_unmute_toggle_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (isMuted) "Unmute Microphone" else "Mute Microphone",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isMuted) "Unmute Mic" else "Mute Mic",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            if (onLeaveRoom != null) {
                Spacer(modifier = Modifier.width(12.dp))
                // Leave Room Button
                Button(
                    onClick = onLeaveRoom,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF334155)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("controls_leave_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Leave Voice Room",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Leave",
                            color = Color(0xFFF87171),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated sound wave visualizer bars for speaking participants.
 */
@Composable
private fun AudioWaveformVisualizer() {
    val transition = rememberInfiniteTransition(label = "audio_bars")
    val bar1Height by transition.animateFloat(
        initialValue = 6f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val bar2Height by transition.animateFloat(
        initialValue = 16f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val bar3Height by transition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(20.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(bar1Height.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF34D399))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(bar2Height.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF10B981))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(bar3Height.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF34D399))
        )
    }
}

/**
 * Pulsing live dot indicator for room status.
 */
@Composable
private fun PulsingLiveDot() {
    val transition = rememberInfiniteTransition(label = "live_pulse")
    val scale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color(0xFFEF4444))
    )
}

/**
 * Placeholder when no participants are active.
 */
@Composable
private fun EmptyParticipantsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No participants yet. Be the first to join!",
                color = Color.LightGray,
                fontSize = 13.sp
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
