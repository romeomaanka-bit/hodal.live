package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FrameType
import com.example.model.PrivateConversation
import com.example.ui.components.HodalAvatar
import com.example.ui.theme.ActiveSpeakerPulse
import com.example.ui.theme.HodalDarkCard
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange

val sampleConversations = listOf(
    PrivateConversation(
        userId = "sufake",
        name = "sufake",
        avatarEmoji = "👩‍🎤",
        level = 8,
        status = "Online",
        lastMessage = "I'm hosting a singing and dancing party in the room. Want to join?",
        lastTime = "Just now",
        unreadCount = 1,
        countryFlag = "🇸🇴"
    ),
    PrivateConversation(
        userId = "pooja",
        name = "Pooja ❤️",
        avatarEmoji = "👩‍🦰",
        level = 18,
        status = "In Room",
        lastMessage = "Thank you so much for the Lion & Tiger gift! 🦁",
        lastTime = "5m ago",
        unreadCount = 0,
        countryFlag = "🇮🇳"
    ),
    PrivateConversation(
        userId = "rugged",
        name = "Rugged King",
        avatarEmoji = "🦁",
        level = 14,
        status = "Playing Ludo",
        lastMessage = "Rematch on Ludo board anytime, bro! 🎲",
        lastTime = "1h ago",
        unreadCount = 0,
        countryFlag = "🇦🇪"
    ),
    PrivateConversation(
        userId = "hodal_official",
        name = "Hodal Official Team",
        avatarEmoji = "👑",
        level = 99,
        status = "Official",
        lastMessage = "Welcome to Hodal.live! Claim your 5,000 daily coins.",
        lastTime = "Yesterday",
        unreadCount = 0,
        countryFlag = "🌍"
    )
)

@Composable
fun ConversationsScreen(
    onConversationClick: (PrivateConversation) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("conversations_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Private Chats",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        // Privacy Lock Badge (Screenshot 4 style)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(HodalDarkCard)
                                .border(1.dp, HodalGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Privacy",
                                tint = HodalGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Privacy Protected",
                                color = HodalGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "Encrypted 1-on-1 messaging & stickers",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            items(sampleConversations) { convo ->
                ConversationItemRow(
                    convo = convo,
                    onClick = { onConversationClick(convo) }
                )
            }
        }
    }
}

@Composable
fun ConversationItemRow(
    convo: PrivateConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("conversation_row_${convo.userId}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        HodalAvatar(
            name = convo.name,
            avatarEmoji = convo.avatarEmoji,
            frameType = if (convo.level > 10) FrameType.ROYAL_CROWN else FrameType.DEFAULT,
            size = 50.dp,
            level = convo.level
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = convo.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = convo.countryFlag, fontSize = 13.sp)
                }

                Text(
                    text = convo.lastTime,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = convo.lastMessage,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                if (convo.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${convo.unreadCount}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
