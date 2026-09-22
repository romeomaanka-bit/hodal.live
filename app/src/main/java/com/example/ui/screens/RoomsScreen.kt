package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.VoiceRoomEntity
import com.example.model.FrameType
import com.example.ui.components.HodalAvatar
import com.example.ui.theme.HodalAmber
import com.example.ui.theme.HodalDarkCard
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange
import com.example.ui.theme.HodalRoyalBlue

@Composable
fun RoomsScreen(
    rooms: List<VoiceRoomEntity>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onRoomClick: (VoiceRoomEntity) -> Unit,
    onCreateRoomClick: () -> Unit
) {
    val categories = listOf("Popular", "Mine", "Ludo", "CP", "Singing")
    val filterChips = listOf(
        Pair("Ranking", Icons.Default.EmojiEvents),
        Pair("Hodal", null),
        Pair("CP Couple", Icons.Default.Favorite),
        Pair("Ludo Play", Icons.Default.SportsEsports)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B132B))
            .testTag("rooms_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header: Title & Subtitle (Screenshot 3 style)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Voice Chat Rooms",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "High-quality friendships worldwide",
                                color = Color.LightGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        // App mini branding
                        Image(
                            painter = painterResource(id = R.drawable.ic_hodal_logo),
                            contentDescription = "Hodal",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            // Room Support Golden Banner (+999999) from Screenshot 3
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF9A3412),
                                    Color(0xFFEA580C),
                                    HodalAmber,
                                    HodalGold
                                )
                            )
                        )
                        .border(1.5.dp, HodalGold, RoundedCornerShape(18.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "👑 Room Support Event",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                text = "Daily party boost ranking rewards",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        }

                        // +999999 Gold pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.45f))
                                .border(1.dp, HodalGold, RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🪙 +999999",
                                color = HodalGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Navigation Tabs (Mine / Popular / etc.)
            item {
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                    containerColor = Color.Transparent,
                    contentColor = HodalOrange,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        val index = categories.indexOf(selectedCategory).coerceAtLeast(0)
                        if (index < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = HodalOrange,
                                height = 3.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Tab(
                            selected = isSelected,
                            onClick = { onCategorySelected(category) },
                            text = {
                                Text(
                                    text = category,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                            }
                        )
                    }
                }
            }

            // Quick Filter Chips (Screenshot 3: Ranking, Hapi/Hodal, CP, etc.)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterChips) { chip ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(HodalDarkCard)
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                .clickable {
                                    if (chip.first.contains("CP")) onCategorySelected("CP")
                                    else if (chip.first.contains("Ludo")) onCategorySelected("Ludo")
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            if (chip.second != null) {
                                Icon(
                                    imageVector = chip.second!!,
                                    contentDescription = chip.first,
                                    tint = HodalGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = chip.first,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Room Cards Grid / List
            items(rooms) { room ->
                RoomCardItem(
                    room = room,
                    onClick = { onRoomClick(room) }
                )
            }
        }

        // Floating Action Button to Create a Room
        FloatingActionButton(
            onClick = onCreateRoomClick,
            containerColor = HodalOrange,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 84.dp, end = 20.dp)
                .testTag("create_room_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Room")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Live",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun RoomCardItem(
    room: VoiceRoomEntity,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1E293B),
                        Color(0xFF162033)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(HodalGold.copy(alpha = 0.4f), Color.Transparent)),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("room_card_${room.roomId}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Host Avatar with frame (Screenshot 3 style)
            val frame = when (room.frameType) {
                "Crown" -> FrameType.ROYAL_CROWN
                "Wings" -> FrameType.GOLDEN_WINGS
                "Fire" -> FrameType.FIRE_PHOENIX
                "Diamond" -> FrameType.DIAMOND_HEART
                else -> FrameType.DEFAULT
            }

            HodalAvatar(
                name = room.hostName,
                avatarEmoji = if (room.hostName.contains("Pooja")) "👩‍🦰"
                else if (room.hostName.contains("Farhan")) "🧔"
                else if (room.hostName.contains("Rugged")) "🦁"
                else "👸",
                frameType = frame,
                isSpeaking = true,
                size = 56.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title and Flag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = room.countryFlag,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = room.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Host: ${room.hostName}",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Tag and Listeners
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(HodalRoyalBlue.copy(alpha = 0.4f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = room.category,
                            color = HodalGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Active Sound Wave",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${room.listenersCount} listeners",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Join Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(HodalOrange, HodalAmber)
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Join",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
