package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HodalAmber
import com.example.ui.theme.HodalDarkSurface
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange

enum class HodalNavTab {
    ROOMS,
    LUDO,
    MESSAGES,
    PROFILE
}

@Composable
fun HodalBottomNav(
    selectedTab: HodalNavTab,
    onTabSelected: (HodalNavTab) -> Unit,
    unreadMessagesCount: Int = 2
) {
    NavigationBar(
        containerColor = Color(0xFF0F172A),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .border(
                0.5.dp,
                Color.White.copy(alpha = 0.1f)
            )
            .testTag("hodal_bottom_nav")
    ) {
        // Rooms Tab
        NavigationBarItem(
            selected = selectedTab == HodalNavTab.ROOMS,
            onClick = { onTabSelected(HodalNavTab.ROOMS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Voice Rooms",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Rooms",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == HodalNavTab.ROOMS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = HodalOrange,
                selectedTextColor = HodalOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF1E293B)
            ),
            modifier = Modifier.testTag("nav_tab_rooms")
        )

        // Ludo Tab
        NavigationBarItem(
            selected = selectedTab == HodalNavTab.LUDO,
            onClick = { onTabSelected(HodalNavTab.LUDO) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = "Ludo",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Ludo",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == HodalNavTab.LUDO) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = HodalGold,
                selectedTextColor = HodalGold,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF1E293B)
            ),
            modifier = Modifier.testTag("nav_tab_ludo")
        )

        // Messages Tab
        NavigationBarItem(
            selected = selectedTab == HodalNavTab.MESSAGES,
            onClick = { onTabSelected(HodalNavTab.MESSAGES) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadMessagesCount > 0) {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text(text = "$unreadMessagesCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Messages",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "Chats",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == HodalNavTab.MESSAGES) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = HodalOrange,
                selectedTextColor = HodalOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF1E293B)
            ),
            modifier = Modifier.testTag("nav_tab_messages")
        )

        // Profile Tab
        NavigationBarItem(
            selected = selectedTab == HodalNavTab.PROFILE,
            onClick = { onTabSelected(HodalNavTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Me",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == HodalNavTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = HodalOrange,
                selectedTextColor = HodalOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF1E293B)
            ),
            modifier = Modifier.testTag("nav_tab_profile")
        )
    }
}
