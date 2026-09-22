package com.example.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GiftItem
import com.example.ui.theme.HodalAmber
import com.example.ui.theme.HodalDarkCard
import com.example.ui.theme.HodalDarkSurface
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange
import com.example.ui.theme.HodalRose

val sampleGifts = listOf(
    GiftItem(
        id = "gift_lion_tiger",
        name = "Lion & Tiger Duel",
        coinPrice = 50000,
        iconRes = R.drawable.ic_gift_lion_tiger,
        isLuxurySpecial = true,
        description = "Full-screen luxury duel effect with crown & lightning",
        glowColor = 0xFFFFD700
    ),
    GiftItem(
        id = "gift_castle",
        name = "Royal Castle",
        coinPrice = 20000,
        emoji = "🏰",
        description = "Fairytale castle entrance effect",
        glowColor = 0xFFFF80DF
    ),
    GiftItem(
        id = "gift_crown",
        name = "Imperial Crown",
        coinPrice = 9999,
        emoji = "👑",
        description = "Royal glory for the room host",
        glowColor = 0xFFFFD700
    ),
    GiftItem(
        id = "gift_rocket",
        name = "Super Rocket",
        coinPrice = 5000,
        emoji = "🚀",
        description = "Blast the room with cosmic energy",
        glowColor = 0xFF00E5FF
    ),
    GiftItem(
        id = "gift_diamond",
        name = "Diamond Ring",
        coinPrice = 2500,
        emoji = "💍",
        description = "Shining romantic commitment",
        glowColor = 0xFF60A5FA
    ),
    GiftItem(
        id = "gift_crystal",
        name = "Crystal Ball",
        coinPrice = 1000,
        emoji = "🔮",
        description = "Magical fortune teller",
        glowColor = 0xFFC084FC
    ),
    GiftItem(
        id = "gift_hodal_love",
        name = "Hodal Heart",
        coinPrice = 520,
        emoji = "💖",
        description = "Heartwarming Hodal cheer",
        glowColor = 0xFFF43F5E
    ),
    GiftItem(
        id = "gift_rose",
        name = "Romantic Rose",
        coinPrice = 99,
        emoji = "🌹",
        description = "Classic gesture of friendship",
        glowColor = 0xFFE11D48
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftDialog(
    userCoins: Long,
    onDismiss: () -> Unit,
    onSendGift: (GiftItem, Int) -> Unit,
    onRecharge: () -> Unit
) {
    var selectedGift by remember { mutableStateOf(sampleGifts[0]) }
    var selectedCombo by remember { mutableIntStateOf(1) }
    val comboOptions = listOf(1, 10, 66, 100)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = HodalDarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .testTag("gift_dialog_sheet")
        ) {
            // Header with Coin balance & Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Coins chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(HodalDarkCard)
                        .border(1.dp, HodalGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "🪙", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$userCoins",
                        color = HodalGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(HodalOrange)
                            .clickable { onRecharge() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Recharge Coins",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Text(
                    text = "GIFT EFFECTS",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.LightGray
                    )
                }
            }

            // Gifts Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                items(sampleGifts) { gift ->
                    val isSelected = gift.id == selectedGift.id
                    val borderModifier = if (isSelected) {
                        Modifier.border(
                            2.dp,
                            Brush.linearGradient(listOf(HodalGold, HodalOrange)),
                            RoundedCornerShape(14.dp)
                        )
                    } else {
                        Modifier.border(
                            1.dp,
                            Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(14.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) HodalDarkCard else Color(0xFF161F30)
                            )
                            .then(borderModifier)
                            .clickable { selectedGift = gift }
                            .padding(8.dp)
                            .testTag("gift_item_${gift.id}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(46.dp)
                        ) {
                            if (gift.iconRes != null) {
                                Image(
                                    painter = painterResource(id = gift.iconRes),
                                    contentDescription = gift.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, HodalGold, CircleShape)
                                )
                            } else {
                                Text(
                                    text = gift.emoji,
                                    fontSize = 30.sp
                                )
                            }

                            if (gift.isLuxurySpecial) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(HodalRose)
                                        .padding(horizontal = 3.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "VIP",
                                        color = Color.White,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = gift.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "🪙 ${gift.coinPrice}",
                            color = HodalGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Combo selection and Send Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Combo pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    comboOptions.forEach { count ->
                        val isComboSelected = selectedCombo == count
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isComboSelected) HodalOrange else HodalDarkCard
                                )
                                .border(
                                    1.dp,
                                    if (isComboSelected) HodalGold else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedCombo = count }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "x$count",
                                color = if (isComboSelected) Color.White else Color.LightGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Send Button
                Button(
                    onClick = {
                        onSendGift(selectedGift, selectedCombo)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HodalOrange
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("send_gift_button")
                ) {
                    Text(
                        text = "Send (${selectedGift.coinPrice * selectedCombo} 🪙)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
