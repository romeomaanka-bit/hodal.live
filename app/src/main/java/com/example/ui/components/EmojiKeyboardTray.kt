package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.EmojiItem
import com.example.ui.theme.HodalAmber
import com.example.ui.theme.HodalDarkCard
import com.example.ui.theme.HodalDarkSurface
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange
import com.example.ui.theme.HodalPurple

val sampleEmojiItems = listOf(
    EmojiItem("1", "😂", "Laughcry"),
    EmojiItem("2", "😄", "Laugh"),
    EmojiItem("3", "😴", "Sleep"),
    EmojiItem("4", "🥳", "Cheer"),
    EmojiItem("5", "🤗", "Embrace"),
    EmojiItem("6", "😈", "Insidious"),
    EmojiItem("7", "😘", "Kiss"),
    EmojiItem("8", "😍", "Love"),
    EmojiItem("9", "😎", "Cool"),
    EmojiItem("10", "😉", "Wink"),
    EmojiItem("11", "🤩", "Starstruck"),
    EmojiItem("12", "😜", "Playful"),
    EmojiItem("13", "🎉", "Party"),
    EmojiItem("14", "🔥", "Fire"),
    EmojiItem("15", "👑", "Crown"),
    EmojiItem("16", "🎲", "Ludo Dice")
)

@Composable
fun EmojiKeyboardTray(
    onEmojiSelected: (EmojiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(HodalDarkSurface)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(16.dp)
            .testTag("emoji_keyboard_tray")
    ) {
        // Fun Header with big cool sunglasses emoji (from screenshot 4!)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(HodalGold, HodalOrange)
                        )
                    )
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "😎", fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Hodal Expressive Stickers",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "Tap any emoji to send instantly",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grid of emojis with descriptive labels
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            items(sampleEmojiItems) { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HodalDarkCard)
                        .clickable { onEmojiSelected(item) }
                        .padding(vertical = 8.dp)
                        .testTag("emoji_item_${item.id}")
                ) {
                    Text(
                        text = item.symbol,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
