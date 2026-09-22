package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FrameType
import com.example.ui.theme.ActiveSpeakerPulse
import com.example.ui.theme.HodalAmber
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange
import com.example.ui.theme.HodalRose
import com.example.ui.theme.HodalRoyalBlue
import com.example.ui.theme.NeonCyan

@Composable
fun HodalAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    avatarEmoji: String = "👑",
    frameType: FrameType = FrameType.DEFAULT,
    isSpeaking: Boolean = false,
    isMuted: Boolean = false,
    isLocked: Boolean = false,
    level: Int? = null,
    showCrown: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.14f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size + 14.dp)
    ) {
        // Speaking pulse glow
        if (isSpeaking) {
            Box(
                modifier = Modifier
                    .size(size + 12.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                ActiveSpeakerPulse.copy(alpha = 0.8f),
                                ActiveSpeakerPulse.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Frame Border styling
        val frameBrush = when (frameType) {
            FrameType.GOLDEN_WINGS -> Brush.sweepGradient(
                listOf(HodalGold, HodalOrange, Color.Yellow, HodalGold)
            )
            FrameType.ROYAL_CROWN -> Brush.sweepGradient(
                listOf(HodalGold, Color.White, HodalGold, HodalOrange)
            )
            FrameType.DIAMOND_HEART -> Brush.sweepGradient(
                listOf(HodalRose, Color.White, HodalRose, Color(0xFFC084FC))
            )
            FrameType.FIRE_PHOENIX -> Brush.sweepGradient(
                listOf(HodalOrange, Color.Red, HodalGold, HodalOrange)
            )
            FrameType.VIP_STAR -> Brush.sweepGradient(
                listOf(NeonCyan, Color.White, HodalRoyalBlue, NeonCyan)
            )
            FrameType.DEFAULT -> Brush.linearGradient(
                listOf(Color(0xFF64748B), Color(0xFF334155))
            )
        }

        // Outer Frame Ring
        Box(
            modifier = Modifier
                .size(size + 4.dp)
                .clip(CircleShape)
                .background(frameBrush)
                .padding(2.5.dp),
            contentAlignment = Alignment.Center
        ) {
            // Inner Avatar
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Seat",
                        tint = Color.LightGray,
                        modifier = Modifier.size(size * 0.45f)
                    )
                } else {
                    Text(
                        text = avatarEmoji,
                        fontSize = (size.value * 0.42f).sp
                    )
                }
            }
        }

        // Crown on top if Host or Royal
        if (showCrown || frameType == FrameType.ROYAL_CROWN || frameType == FrameType.GOLDEN_WINGS) {
            Text(
                text = "👑",
                fontSize = (size.value * 0.35f).sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-size.value * 0.22f).dp)
            )
        }

        // Mic status badge bottom-right
        if (isMuted) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.9f))
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MicOff,
                    contentDescription = "Muted",
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        } else if (isSpeaking) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(ActiveSpeakerPulse)
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Speaking",
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        // Level pill bottom-center if present
        if (level != null && level > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(HodalOrange, HodalAmber)
                        )
                    )
                    .border(0.7.dp, Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp, vertical = 0.5.dp)
            ) {
                Text(
                    text = "Lv.$level",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
