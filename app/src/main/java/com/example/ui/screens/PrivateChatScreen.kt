package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.model.FrameType
import com.example.ui.components.EmojiKeyboardTray
import com.example.ui.components.HodalAvatar
import com.example.ui.theme.ActiveSpeakerPulse
import com.example.ui.theme.HodalDarkCard
import com.example.ui.theme.HodalDarkSurface
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange
import com.example.ui.theme.NeonCyan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Real-time private 1-on-1 messaging screen connected with Cloud Firestore.
 * Features live message bubbles, typing input with send action, encrypted badge,
 * and quick emoji/reaction support.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatScreen(
    messages: List<ChatMessageEntity>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    partnerName: String = "sufake",
    partnerAvatar: String = "👩‍🎤",
    partnerLevel: Int = 8,
    partnerStatus: String = "Online • Hodal Party Star",
    isFirestoreLive: Boolean = true
) {
    var textInput by remember { mutableStateOf("") }
    var isEmojiTrayVisible by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Auto-scroll to latest message whenever message count changes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B132B))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("private_chat_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Header with Partner Info, Firestore Live Indicator, and Encryption Lock
            PrivateChatHeader(
                partnerName = partnerName,
                partnerAvatar = partnerAvatar,
                partnerLevel = partnerLevel,
                partnerStatus = partnerStatus,
                isFirestoreLive = isFirestoreLive,
                onBack = onBack
            )

            // 2. Real-time Message Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .testTag("chat_messages_list"),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Encryption disclaimer header item
                item {
                    ConversationEncryptionDisclaimer()
                }

                if (messages.isEmpty()) {
                    item {
                        EmptyChatState(
                            partnerName = partnerName,
                            onQuickPrompt = { prompt ->
                                onSendMessage(prompt)
                            }
                        )
                    }
                } else {
                    items(messages, key = { it.id }) { msg ->
                        PrivateMessageBubble(
                            msg = msg,
                            partnerAvatar = partnerAvatar
                        )
                    }
                }
            }

            // 3. Bottom Message Input Bar with Send Action and Emoji Tray
            PrivateChatInputBar(
                textInput = textInput,
                onTextChanged = { textInput = it },
                onSend = {
                    if (textInput.isNotBlank()) {
                        onSendMessage(textInput.trim())
                        textInput = ""
                    }
                },
                isEmojiTrayVisible = isEmojiTrayVisible,
                onToggleEmojiTray = { isEmojiTrayVisible = !isEmojiTrayVisible },
                onQuickGift = {
                    onSendMessage("🎁 Sent a Luxury Hodal Gift!")
                }
            )

            // 4. Emoji / Sticker Drawer
            AnimatedVisibility(visible = isEmojiTrayVisible) {
                EmojiKeyboardTray(
                    onEmojiSelected = { emoji ->
                        onSendMessage(emoji.symbol)
                    }
                )
            }
        }
    }
}

/**
 * Top header displaying user details, live Firestore sync chip, and encryption status.
 */
@Composable
private fun PrivateChatHeader(
    partnerName: String,
    partnerAvatar: String,
    partnerLevel: Int,
    partnerStatus: String,
    isFirestoreLive: Boolean,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HodalDarkSurface)
            .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(0.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Partner Avatar with Frame
                HodalAvatar(
                    name = partnerName,
                    avatarEmoji = partnerAvatar,
                    frameType = FrameType.FIRE_PHOENIX,
                    size = 40.dp,
                    level = partnerLevel
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = partnerName,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HodalOrange)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Lv.$partnerLevel",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = partnerStatus,
                        color = ActiveSpeakerPulse,
                        fontSize = 11.sp
                    )
                }
            }

            // Real-time Firestore Live Indicator & Security Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isFirestoreLive) {
                    FirestoreLivePill()
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Security Encrypted Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(HodalGold.copy(alpha = 0.18f))
                        .border(1.dp, HodalGold.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Privacy Protected",
                            tint = HodalGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Encrypted",
                            color = HodalGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated live indicator showing real-time Firestore synchronization.
 */
@Composable
private fun FirestoreLivePill() {
    val transition = rememberInfiniteTransition(label = "firestore_sync")
    val dotAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF064E3B).copy(alpha = 0.6f))
            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .scale(dotAlpha)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Firestore Live",
                color = Color(0xFFA7F3D0),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Centered disclaimer explaining the end-to-end encrypted private session.
 */
@Composable
private fun ConversationEncryptionDisclaimer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                text = "🔒 Private messages in Hodal.live are real-time & encrypted",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Message Bubble component rendering both outgoing (from current user)
 * and incoming (from chat partner) messages, plus virtual gift highlights.
 */
@Composable
fun PrivateMessageBubble(
    msg: ChatMessageEntity,
    partnerAvatar: String = "👩‍🎤"
) {
    val isMe = msg.isFromMe
    val timeFormatted = remember(msg.timestamp) {
        try {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))
        } catch (e: Exception) {
            "Just now"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(if (isMe) "message_bubble_outgoing" else "message_bubble_incoming"),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe) {
            // Incoming Partner Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF334155))
                    .border(1.dp, NeonCyan.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = partnerAvatar, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            if (msg.isGift) {
                // Special Virtual Gift Message Bubble
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF78350F).copy(alpha = 0.85f),
                                    Color(0xFF451A03).copy(alpha = 0.85f)
                                )
                            )
                        )
                        .border(1.5.dp, HodalGold, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("message_bubble_gift")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = msg.messageText,
                                color = HodalGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (msg.giftCoinValue > 0) {
                                Text(
                                    text = "🪙 ${msg.giftCoinValue} Coins",
                                    color = Color(0xFFFDE68A),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            } else {
                // Standard Text Message Bubble
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = if (isMe) 18.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 18.dp
                            )
                        )
                        .background(
                            if (isMe) {
                                // Sent bubble: Emerald green gradient (Screenshot 4 style)
                                Brush.linearGradient(
                                    listOf(Color(0xFF10B981), Color(0xFF059669))
                                )
                            } else {
                                // Received bubble: Deep slate with high contrast
                                Brush.linearGradient(
                                    listOf(Color(0xFF1E293B), Color(0xFF1E293B))
                                )
                            }
                        )
                        .border(
                            width = 0.5.dp,
                            color = if (isMe) Color(0xFF34D399).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = if (isMe) 18.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 18.dp
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = msg.messageText,
                        color = Color.White,
                        fontSize = 14.5.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Timestamp and delivery double checkmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = timeFormatted,
                    color = Color.LightGray.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
                if (isMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Row {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Delivered",
                            tint = Color(0xFF6EE7B7),
                            modifier = Modifier.size(11.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Read",
                            tint = Color(0xFF6EE7B7),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom input bar featuring emoji toggle, quick gift action, text input, and send button.
 */
@Composable
private fun PrivateChatInputBar(
    textInput: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    isEmojiTrayVisible: Boolean,
    onToggleEmojiTray: () -> Unit,
    onQuickGift: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HodalDarkSurface)
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(0.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("chat_input_bar"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emoji Keyboard Toggle
        IconButton(
            onClick = onToggleEmojiTray,
            modifier = Modifier
                .size(42.dp)
                .background(HodalDarkCard, CircleShape)
                .testTag("emoji_tray_toggle_button")
        ) {
            Icon(
                imageVector = Icons.Default.Mood,
                contentDescription = "Emoji Stickers",
                tint = if (isEmojiTrayVisible) NeonCyan else HodalGold,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Quick Gift Action Button
        IconButton(
            onClick = onQuickGift,
            modifier = Modifier
                .size(42.dp)
                .background(HodalDarkCard, CircleShape)
                .testTag("chat_gift_button")
        ) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = "Send Gift",
                tint = HodalOrange,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Message Text Field
        OutlinedTextField(
            value = textInput,
            onValueChange = onTextChanged,
            placeholder = {
                Text(
                    text = "Send a private message...",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )
            },
            singleLine = false,
            maxLines = 3,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = HodalDarkCard,
                unfocusedContainerColor = HodalDarkCard,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = HodalOrange,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() }),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("chat_message_input")
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Send Button
        val canSend = textInput.isNotBlank()
        IconButton(
            onClick = onSend,
            enabled = canSend,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    if (canSend) HodalOrange else Color(0xFF334155)
                )
                .testTag("chat_send_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send Message",
                tint = if (canSend) Color.White else Color.Gray,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

/**
 * Empty state showing a warm greeting and quick response prompts when starting a new chat.
 */
@Composable
private fun EmptyChatState(
    partnerName: String,
    onQuickPrompt: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(HodalOrange.copy(alpha = 0.2f))
                .border(1.5.dp, HodalOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "💬", fontSize = 32.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Say hello to $partnerName!",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Messages are synced in real time with Cloud Firestore.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Starter Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("👋 Hello!", "🎉 Send a gift", "🎲 Play Ludo?").forEach { prompt ->
                SuggestionChip(
                    onClick = { onQuickPrompt(prompt) },
                    label = {
                        Text(
                            text = prompt,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = HodalDarkCard
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = HodalGold.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}
