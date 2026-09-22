package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GiftItem
import com.example.ui.theme.ActiveSpeakerPulse
import com.example.ui.theme.HodalAmber
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange
import com.example.ui.theme.HodalRose
import com.example.ui.theme.HodalRoyalBlue
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Luxury Gift overlay component that displays high-impact animated icons to represent
 * virtual gifts being sent between participants in a live voice chat room.
 *
 * @param gift The virtual gift item being sent
 * @param senderName The name of the sender participant
 * @param receiverName The name of the recipient participant
 * @param senderAvatar The avatar or emoji of the sender
 * @param receiverAvatar The avatar or emoji of the recipient
 * @param comboCount Combo count multiplier (e.g. x1, x10, x66, x99)
 * @param autoDismissDurationMs Duration before auto-dismissal
 * @param onDismiss Callback invoked when the overlay is dismissed
 */
@Composable
fun LuxuryGiftOverlay(
    gift: GiftItem?,
    senderName: String,
    receiverName: String,
    senderAvatar: String = "🧔",
    receiverAvatar: String = "👩‍🦰",
    comboCount: Int = 1,
    autoDismissDurationMs: Long = 4500L,
    onDismiss: () -> Unit
) {
    if (gift == null) return

    val entryScaleAnim = remember { Animatable(1f) }

    LaunchedEffect(gift) {
        entryScaleAnim.snapTo(0.2f)
        entryScaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(450, easing = FastOutSlowInEasing)
        )
        delay(autoDismissDurationMs)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
            .testTag("luxury_gift_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Top Close Button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 28.dp, end = 20.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                .testTag("close_gift_overlay_button")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Gift Animation",
                tint = Color.White
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .scale(entryScaleAnim.value)
        ) {
            // 1. Delivery Trajectory Banner: Sender -> Animated Flying Gift Icon -> Receiver
            GiftDeliveryTrajectoryBanner(
                senderName = senderName,
                senderAvatar = senderAvatar,
                receiverName = receiverName,
                receiverAvatar = receiverAvatar,
                giftEmoji = gift.emoji
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. VIP Announcement Ribbon
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                HodalOrange.copy(alpha = 0.95f),
                                HodalRose.copy(alpha = 0.95f),
                                HodalAmber.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .border(2.dp, HodalGold, RoundedCornerShape(24.dp))
                    .padding(horizontal = 20.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "⚡ LUXURY GIFT EFFECT ⚡",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sender & Receiver announcement line
            Text(
                text = "$senderName sent ${gift.name} to $receiverName!",
                color = HodalGold,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Central Animated Gift Display with orbiting particle icons
            AnimatedLuxuryGiftCenterpiece(gift = gift)

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Combo Multiplier Badge and Coin Price
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                GiftComboBadge(comboCount = comboCount)

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.2.dp, HodalGold, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "🪙 ${gift.coinPrice} Coins",
                        color = HodalGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Room Audio Waveform Visualizer Footer
            LiveRoomAudioVisualizerFooter()
        }
    }
}

/**
 * Animated Trajectory Banner showing the virtual gift traveling from the sender to the receiver.
 */
@Composable
fun GiftDeliveryTrajectoryBanner(
    senderName: String,
    senderAvatar: String,
    receiverName: String,
    receiverAvatar: String,
    giftEmoji: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flight_path")
    val flightProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flight_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.88f))
            .border(1.5.dp, HodalGold.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("gift_sender_receiver_pill"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sender Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HodalOrange.copy(alpha = 0.35f))
                        .border(1.5.dp, HodalOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = senderAvatar, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = senderName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "Sender",
                        color = HodalOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Central Animated Flight Channel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Background Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.Center)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    HodalOrange.copy(alpha = 0.3f),
                                    HodalGold,
                                    NeonCyan.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                // Flying Gift Icon along the path
                val xOffsetDp = (flightProgress * 90).dp
                val yOffsetDp = (sin(flightProgress * Math.PI.toFloat()) * -10).dp

                Box(
                    modifier = Modifier
                        .offset(x = xOffsetDp, y = yOffsetDp)
                        .align(Alignment.CenterStart)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = giftEmoji,
                        fontSize = 18.sp,
                        modifier = Modifier.scale(1f + 0.2f * sin(flightProgress * Math.PI.toFloat()))
                    )
                }

                // Sparkle particle trail
                Text(
                    text = "✨",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .offset(x = (xOffsetDp - 14.dp).coerceAtLeast(0.dp), y = yOffsetDp + 2.dp)
                        .alpha(1f - flightProgress)
                )
            }

            // Receiver Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = receiverName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "Receiver",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.35f))
                        .border(1.5.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = receiverAvatar, fontSize = 20.sp)
                }
            }
        }
    }
}

/**
 * Centerpiece showing the main virtual gift with rotating celestial rays,
 * customized animation effects, and orbiting particle icons.
 */
@Composable
fun AnimatedLuxuryGiftCenterpiece(gift: GiftItem) {
    val spinTransition = rememberInfiniteTransition(label = "celestial_spin")
    val sunburstAngle by spinTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunburst_angle"
    )

    val bounceTransition = rememberInfiniteTransition(label = "icon_hover")
    val hoverOffset by bounceTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hover_offset"
    )

    Box(
        modifier = Modifier
            .size(280.dp)
            .testTag("luxury_gift_animated_icon"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Rotating Celestial Sunburst Rays behind the gift
        Box(
            modifier = Modifier
                .size(270.dp)
                .rotate(sunburstAngle)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            HodalGold.copy(alpha = 0.45f),
                            Color.Transparent,
                            NeonCyan.copy(alpha = 0.45f),
                            Color.Transparent,
                            HodalOrange.copy(alpha = 0.45f),
                            Color.Transparent,
                            HodalRose.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. Multi-Icon Orbiting & Floating Particle System
        FloatingGiftParticles()

        // 3. Central Gift Icon Representation (Image or specialized Animated Icon)
        Box(
            modifier = Modifier
                .offset { IntOffset(0, hoverOffset.toInt()) }
                .size(210.dp),
            contentAlignment = Alignment.Center
        ) {
            if (gift.iconRes != null) {
                // High-fidelity graphic with glowing border
                Image(
                    painter = painterResource(id = gift.iconRes),
                    contentDescription = gift.name,
                    modifier = Modifier
                        .size(190.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(3.dp, HodalGold, RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Dedicated Animated Icon Presentation
                SpecificGiftIconVisualizer(gift = gift)
            }

            // Floating Royal Crown badge bouncing on top
            Text(
                text = "👑",
                fontSize = 42.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp)
                    .scale(1.15f)
            )
        }
    }
}

/**
 * Dedicated visualizer for virtual gifts using animated icons (Sports Car, Rocket, Castle, Crown, Diamond, Rose, etc.)
 */
@Composable
fun SpecificGiftIconVisualizer(gift: GiftItem) {
    val infiniteTransition = rememberInfiniteTransition(label = "specific_gift_anim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val wobbleAngle by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble_angle"
    )

    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(gift.glowColor).copy(alpha = 0.45f),
                        Color(0xFF0F172A).copy(alpha = 0.85f)
                    )
                )
            )
            .border(2.5.dp, Color(gift.glowColor), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Rocket 🚀 - Cosmic launch animation
            gift.emoji == "🚀" || gift.id.contains("rocket", ignoreCase = true) -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🚀",
                        fontSize = 82.sp,
                        modifier = Modifier
                            .rotate(-35f)
                            .scale(pulseScale)
                    )
                    Text(
                        text = "🔥💨",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .scale(pulseScale)
                            .offset(y = (-6).dp)
                    )
                }
            }
            // Sports Car 🏎️ - Nitro speed animation
            gift.emoji == "🏎️" || gift.id.contains("car", ignoreCase = true) -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🏎️",
                        fontSize = 86.sp,
                        modifier = Modifier
                            .rotate(wobbleAngle)
                            .scale(pulseScale)
                    )
                    Text(
                        text = "💨✨⚡",
                        fontSize = 20.sp,
                        modifier = Modifier.offset(x = 10.dp)
                    )
                }
            }
            // Imperial Crown 👑 - Sparkling diamond glow
            gift.emoji == "👑" || gift.id.contains("crown", ignoreCase = true) -> {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "👑",
                        fontSize = 88.sp,
                        modifier = Modifier
                            .scale(pulseScale)
                            .rotate(wobbleAngle * 0.5f)
                    )
                    Text(
                        text = "💎",
                        fontSize = 28.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = (-8).dp)
                    )
                }
            }
            // Castle 🏰 - Fairytale fireworks
            gift.emoji == "🏰" || gift.id.contains("castle", ignoreCase = true) -> {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🏰",
                        fontSize = 86.sp,
                        modifier = Modifier.scale(pulseScale)
                    )
                    Text(
                        text = "🎆",
                        fontSize = 28.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = 6.dp, y = 2.dp)
                    )
                    Text(
                        text = "✨",
                        fontSize = 26.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-6).dp, y = 2.dp)
                    )
                }
            }
            // Diamond Ring 💍 - Radiant facets
            gift.emoji == "💍" || gift.id.contains("diamond", ignoreCase = true) -> {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "💍",
                        fontSize = 86.sp,
                        modifier = Modifier
                            .rotate(wobbleAngle)
                            .scale(pulseScale)
                    )
                    Text(
                        text = "💖",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-10).dp, y = (-10).dp)
                    )
                }
            }
            // Default / Other Gifts
            else -> {
                Text(
                    text = gift.emoji,
                    fontSize = 88.sp,
                    modifier = Modifier
                        .scale(pulseScale)
                        .rotate(wobbleAngle)
                )
            }
        }
    }
}

/**
 * Animated floating particle system that surrounds the gift with celebratory icons
 * (stars, coins, sparkles, hearts, ribbons).
 */
@Composable
fun FloatingGiftParticles() {
    val transition = rememberInfiniteTransition(label = "particles")
    val angleRad by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )

    val particles = listOf(
        Pair("✨", 110f),
        Pair("🪙", 118f),
        Pair("⭐", 125f),
        Pair("💖", 112f),
        Pair("🎉", 120f),
        Pair("💎", 115f)
    )

    particles.forEachIndexed { index, (symbol, radius) ->
        val itemAngle = angleRad + (index * (2 * Math.PI / particles.size).toFloat())
        val xOffset = (radius * cos(itemAngle)).dp
        val yOffset = (radius * sin(itemAngle)).dp

        Text(
            text = symbol,
            fontSize = (18 + (index % 3) * 4).sp,
            modifier = Modifier
                .offset(x = xOffset, y = yOffset)
                .alpha(0.85f + 0.15f * sin(itemAngle))
        )
    }
}

/**
 * Combo Multiplier badge with punchy scaling animation.
 */
@Composable
fun GiftComboBadge(comboCount: Int) {
    val comboTransition = rememberInfiniteTransition(label = "combo_bounce")
    val bounceScale by comboTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_scale"
    )

    val displayCount = if (comboCount > 1) comboCount else 66

    Box(
        modifier = Modifier
            .scale(bounceScale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFF0055), Color(0xFFFF5500), Color(0xFFFFCC00))
                )
            )
            .border(1.5.dp, Color.White, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .testTag("gift_combo_tag")
    ) {
        Text(
            text = "COMBO x$displayCount",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}

/**
 * Animated audio visualizer wave bars representing real-time voice cheering in the chat room.
 */
@Composable
fun LiveRoomAudioVisualizerFooter() {
    val transition = rememberInfiniteTransition(label = "audio_visualizer")
    val bar1 by transition.animateFloat(
        initialValue = 6f, targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 16f, targetValue = 7f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 8f, targetValue = 22f,
        animationSpec = infiniteRepeatable(tween(280, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b3"
    )
    val bar4 by transition.animateFloat(
        initialValue = 20f, targetValue = 9f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b4"
    )
    val bar5 by transition.animateFloat(
        initialValue = 10f, targetValue = 17f,
        animationSpec = infiniteRepeatable(tween(320, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b5"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔊 LIVE ROOM CELEBRATION ",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        listOf(bar1, bar2, bar3, bar4, bar5).forEach { barHeight ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .width(3.dp)
                    .height(barHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(HodalGold)
            )
        }
    }
}
