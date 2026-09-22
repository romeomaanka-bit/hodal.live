package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HodalGold
import com.example.ui.theme.HodalOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 4 Classic Ludo Colors.
 */
enum class BoardColor(
    val primary: Color,
    val light: Color,
    val dark: Color,
    val displayName: String
) {
    RED(
        primary = Color(0xFFEF4444),
        light = Color(0xFFFCA5A5),
        dark = Color(0xFFB91C1C),
        displayName = "Red"
    ),
    GREEN(
        primary = Color(0xFF10B981),
        light = Color(0xFF6EE7B7),
        dark = Color(0xFF047857),
        displayName = "Green"
    ),
    YELLOW(
        primary = Color(0xFFF59E0B),
        light = Color(0xFFFDE68A),
        dark = Color(0xFFD97706),
        displayName = "Yellow"
    ),
    BLUE(
        primary = Color(0xFF3B82F6),
        light = Color(0xFF93C5FD),
        dark = Color(0xFF1D4ED8),
        displayName = "Blue"
    )
}

/**
 * Game piece model representing a token on the Ludo board.
 */
data class LudoPiece(
    val id: String,
    val color: BoardColor,
    val pieceIndex: Int,
    val step: Int = -1, // -1 in base yard; 0..50 on common path; 51..55 in home column; 56 completed
    val isMovable: Boolean = false
)

/**
 * Reusable Ludo Game Board layout and component.
 *
 * @param modifier Modifier for root container
 * @param pieces List of 16 pieces (4 per color) or dynamic pieces on board
 * @param currentTurn Color of player whose turn it currently is
 * @param onPieceClick Callback when a player taps an interactive piece
 * @param diceValue Current dice value (1..6)
 * @param isRolling Whether the dice is actively rolling
 * @param onRollDice Callback to trigger dice roll
 * @param statusMessage Optional notice banner (e.g. "Player rolled 6!", "Collision!")
 */
@Composable
fun LudoGameBoard(
    modifier: Modifier = Modifier,
    pieces: List<LudoPiece> = rememberDefaultPieces(),
    currentTurn: BoardColor = BoardColor.RED,
    onPieceClick: (LudoPiece) -> Unit = {},
    diceValue: Int = 6,
    isRolling: Boolean = false,
    onRollDice: () -> Unit = {},
    statusMessage: String? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ludo_game_board_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A),
                            Color(0xFF090D16)
                        )
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Turn Header & Notice
            LudoTurnBanner(
                currentTurn = currentTurn,
                statusMessage = statusMessage
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Main 15x15 Ludo Board
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(3.dp, HodalGold, RoundedCornerShape(16.dp))
                    .testTag("ludo_board_canvas_container"),
                contentAlignment = Alignment.Center
            ) {
                // Background Grid and Tracks rendered on Canvas
                LudoCanvasBoard()

                // Interactive Pieces Overlay
                LudoPiecesOverlay(
                    pieces = pieces,
                    currentTurn = currentTurn,
                    onPieceClick = onPieceClick
                )

                // Central Winning Home Star
                CentralHomeEmblem()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dice Roll Mechanism Section
            LudoDiceRollMechanism(
                diceValue = diceValue,
                isRolling = isRolling,
                currentTurn = currentTurn,
                onRollDice = onRollDice
            )
        }
    }
}

/**
 * Header banner showing current turn with color badge and status alert.
 */
@Composable
private fun LudoTurnBanner(
    currentTurn: BoardColor,
    statusMessage: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(currentTurn.primary)
                        .border(1.dp, Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${currentTurn.displayName}'s Turn",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Quick rule / status hint
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Classic Ludo 🎲",
                    color = HodalGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (!statusMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, currentTurn.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusMessage,
                    color = Color(0xFFFDE68A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Pure Canvas rendering of the classic 15x15 Ludo board:
 * 4 Home Bases, Paths, Safe Squares with Stars, and Home Columns.
 */
@Composable
private fun LudoCanvasBoard() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val totalSize = size.width
        val unit = totalSize / 15f

        val red = Color(0xFFEF4444)
        val green = Color(0xFF10B981)
        val yellow = Color(0xFFF59E0B)
        val blue = Color(0xFF3B82F6)
        val trackBg = Color(0xFFF8FAFC)
        val gridLineColor = Color(0xFFCBD5E1)

        // 1. Draw Background
        drawRect(color = trackBg, size = size)

        // 2. Base Yards (6x6 units each)
        // Red Yard (Top-Left)
        drawRect(color = red, topLeft = Offset(0f, 0f), size = Size(unit * 6, unit * 6))
        drawRect(color = Color.White, topLeft = Offset(unit * 1, unit * 1), size = Size(unit * 4, unit * 4))

        // Green Yard (Top-Right)
        drawRect(color = green, topLeft = Offset(unit * 9, 0f), size = Size(unit * 6, unit * 6))
        drawRect(color = Color.White, topLeft = Offset(unit * 10, unit * 1), size = Size(unit * 4, unit * 4))

        // Blue Yard (Bottom-Left)
        drawRect(color = blue, topLeft = Offset(0f, unit * 9), size = Size(unit * 6, unit * 6))
        drawRect(color = Color.White, topLeft = Offset(unit * 1, unit * 10), size = Size(unit * 4, unit * 4))

        // Yellow Yard (Bottom-Right)
        drawRect(color = yellow, topLeft = Offset(unit * 9, unit * 9), size = Size(unit * 6, unit * 6))
        drawRect(color = Color.White, topLeft = Offset(unit * 10, unit * 10), size = Size(unit * 4, unit * 4))

        // 3. Yard Piece Placement Circular Pods
        val podRadius = unit * 0.65f
        fun drawYardPods(baseX: Float, baseY: Float, color: Color) {
            val podPositions = listOf(
                Offset(baseX + unit * 2f, baseY + unit * 2f),
                Offset(baseX + unit * 4f, baseY + unit * 2f),
                Offset(baseX + unit * 2f, baseY + unit * 4f),
                Offset(baseX + unit * 4f, baseY + unit * 4f)
            )
            for (pos in podPositions) {
                drawCircle(color = color.copy(alpha = 0.25f), radius = podRadius, center = pos)
                drawCircle(
                    color = color,
                    radius = podRadius,
                    center = pos,
                    style = Stroke(width = 2f)
                )
            }
        }
        drawYardPods(0f, 0f, red)
        drawYardPods(unit * 9, 0f, green)
        drawYardPods(0f, unit * 9, blue)
        drawYardPods(unit * 9, unit * 9, yellow)

        // 4. Colored Home Run Columns (5 squares each)
        // Red Home Column (horizontal left -> center: row 7, cols 1..5)
        for (col in 1..5) {
            drawRect(
                color = red,
                topLeft = Offset(unit * col, unit * 7),
                size = Size(unit, unit)
            )
        }
        // Green Home Column (vertical top -> center: col 7, rows 1..5)
        for (row in 1..5) {
            drawRect(
                color = green,
                topLeft = Offset(unit * 7, unit * row),
                size = Size(unit, unit)
            )
        }
        // Yellow Home Column (horizontal right -> center: row 7, cols 9..13)
        for (col in 9..13) {
            drawRect(
                color = yellow,
                topLeft = Offset(unit * col, unit * 7),
                size = Size(unit, unit)
            )
        }
        // Blue Home Column (vertical bottom -> center: col 7, rows 9..13)
        for (row in 9..13) {
            drawRect(
                color = blue,
                topLeft = Offset(unit * 7, unit * row),
                size = Size(unit, unit)
            )
        }

        // 5. Starting Squares (Safe tiles with color)
        // Red Start (row 6, col 1)
        drawRect(color = red, topLeft = Offset(unit * 1, unit * 6), size = Size(unit, unit))
        // Green Start (row 1, col 8)
        drawRect(color = green, topLeft = Offset(unit * 8, unit * 1), size = Size(unit, unit))
        // Yellow Start (row 8, col 13)
        drawRect(color = yellow, topLeft = Offset(unit * 13, unit * 8), size = Size(unit, unit))
        // Blue Start (row 13, col 6)
        drawRect(color = blue, topLeft = Offset(unit * 6, unit * 13), size = Size(unit, unit))

        // 6. Safe Squares (Star positions)
        val safePositions = listOf(
            Offset(unit * 1.5f, unit * 6.5f), // Red Start
            Offset(unit * 6.5f, unit * 2.5f),
            Offset(unit * 8.5f, unit * 1.5f), // Green Start
            Offset(unit * 12.5f, unit * 6.5f),
            Offset(unit * 13.5f, unit * 8.5f), // Yellow Start
            Offset(unit * 8.5f, unit * 12.5f),
            Offset(unit * 6.5f, unit * 13.5f), // Blue Start
            Offset(unit * 2.5f, unit * 8.5f)
        )
        for (safePos in safePositions) {
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = unit * 0.35f,
                center = safePos
            )
            drawCircle(
                color = HodalGold,
                radius = unit * 0.28f,
                center = safePos
            )
        }

        // 7. Grid Lines on Track Cross
        val stroke = Stroke(width = 1.2f)
        // Vertical track lines
        for (col in 6..9) {
            drawLine(
                color = gridLineColor,
                start = Offset(unit * col, 0f),
                end = Offset(unit * col, totalSize),
                strokeWidth = 1f
            )
        }
        // Horizontal track lines
        for (row in 6..9) {
            drawLine(
                color = gridLineColor,
                start = Offset(0f, unit * row),
                end = Offset(totalSize, unit * row),
                strokeWidth = 1f
            )
        }
        // Internal track cell subdivisions
        for (i in 0..15) {
            drawLine(
                color = gridLineColor.copy(alpha = 0.4f),
                start = Offset(unit * i, 0f),
                end = Offset(unit * i, totalSize),
                strokeWidth = 0.7f
            )
            drawLine(
                color = gridLineColor.copy(alpha = 0.4f),
                start = Offset(0f, unit * i),
                end = Offset(totalSize, unit * i),
                strokeWidth = 0.7f
            )
        }

        // 8. Center Home Triangles (3x3 central hub)
        val centerLeft = unit * 6
        val centerTop = unit * 6
        val centerRight = unit * 9
        val centerBottom = unit * 9
        val centerPoint = Offset(totalSize / 2f, totalSize / 2f)

        // Red triangle (Left)
        val redTriangle = Path().apply {
            moveTo(centerLeft, centerTop)
            lineTo(centerPoint.x, centerPoint.y)
            lineTo(centerLeft, centerBottom)
            close()
        }
        drawPath(path = redTriangle, color = red)

        // Green triangle (Top)
        val greenTriangle = Path().apply {
            moveTo(centerLeft, centerTop)
            lineTo(centerPoint.x, centerPoint.y)
            lineTo(centerRight, centerTop)
            close()
        }
        drawPath(path = greenTriangle, color = green)

        // Yellow triangle (Right)
        val yellowTriangle = Path().apply {
            moveTo(centerRight, centerTop)
            lineTo(centerPoint.x, centerPoint.y)
            lineTo(centerRight, centerBottom)
            close()
        }
        drawPath(path = yellowTriangle, color = yellow)

        // Blue triangle (Bottom)
        val blueTriangle = Path().apply {
            moveTo(centerLeft, centerBottom)
            lineTo(centerPoint.x, centerPoint.y)
            lineTo(centerRight, centerBottom)
            close()
        }
        drawPath(path = blueTriangle, color = blue)

        // Center border
        drawRect(
            color = HodalGold,
            topLeft = Offset(centerLeft, centerTop),
            size = Size(unit * 3, unit * 3),
            style = Stroke(width = 2f)
        )
    }
}

/**
 * Overlay that places animated, interactive Ludo tokens/pawns on the board.
 */
@Composable
private fun LudoPiecesOverlay(
    pieces: List<LudoPiece>,
    currentTurn: BoardColor,
    onPieceClick: (LudoPiece) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val totalSize = maxWidth
        val unit = totalSize / 15f

        for (piece in pieces) {
            val (col, row) = calculatePieceCoordinates(piece)
            val isCurrentTurn = piece.color == currentTurn

            Box(
                modifier = Modifier
                    .offset(x = unit * col, y = unit * row)
                    .size(unit)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                LudoPieceToken(
                    piece = piece,
                    isInteractive = isCurrentTurn && piece.isMovable,
                    size = unit * 0.75f,
                    onClick = { onPieceClick(piece) }
                )
            }
        }
    }
}

/**
 * Individual 3D Ludo game piece with shadow, circular cap, and pulse animation when movable.
 */
@Composable
fun LudoPieceToken(
    piece: LudoPiece,
    isInteractive: Boolean,
    size: Dp = 24.dp,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "piece_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isInteractive) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (isInteractive) pulseScale else 1f)
            .shadow(if (isInteractive) 6.dp else 2.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        piece.color.light,
                        piece.color.primary,
                        piece.color.dark
                    )
                )
            )
            .border(
                width = if (isInteractive) 2.dp else 1.2.dp,
                color = if (isInteractive) Color.White else Color.White.copy(alpha = 0.8f),
                shape = CircleShape
            )
            .clickable(enabled = isInteractive, onClick = onClick)
            .testTag("ludo_piece_${piece.id}"),
        contentAlignment = Alignment.Center
    ) {
        // Center inner pawn crown
        Box(
            modifier = Modifier
                .size(size * 0.45f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${piece.pieceIndex + 1}",
                color = piece.color.dark,
                fontSize = (size.value * 0.28f).sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

/**
 * Central golden trophy emblem in the home goal.
 */
@Composable
private fun CentralHomeEmblem() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
            .border(1.5.dp, HodalGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "👑",
            fontSize = 18.sp
        )
    }
}

/**
 * Interactive Dice Roll Mechanism with 3D Dice, realistic pips, and roll trigger.
 */
@Composable
fun LudoDiceRollMechanism(
    diceValue: Int,
    isRolling: Boolean,
    currentTurn: BoardColor,
    onRollDice: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Interactive 3D Dice Display Box
            LudoDice(
                value = diceValue,
                isRolling = isRolling,
                activeColor = currentTurn.primary,
                onClick = onRollDice
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Turn info and roll description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${currentTurn.displayName} to Roll",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = if (isRolling) "Rolling dice..." else "Value: $diceValue (tap to roll)",
                    color = HodalGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Primary Roll Button
            Button(
                onClick = onRollDice,
                enabled = !isRolling,
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentTurn.primary
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("roll_dice_action_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Roll Dice",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ROLL",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

/**
 * 3D Dice Face Component with realistic pips (1 to 6) and rotation physics animation.
 */
@Composable
fun LudoDice(
    value: Int,
    isRolling: Boolean,
    activeColor: Color = HodalGold,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rollRotation = rememberInfiniteTransition(label = "dice_roll_anim")
    val rotationAngle by rollRotation.animateFloat(
        initialValue = 0f,
        targetValue = if (isRolling) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(280, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dice_rotation"
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .rotate(rotationAngle)
            .shadow(6.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(2.dp, activeColor, RoundedCornerShape(14.dp))
            .clickable(enabled = !isRolling, onClick = onClick)
            .testTag("ludo_dice_interactive"),
        contentAlignment = Alignment.Center
    ) {
        DicePipsCanvas(value = value)
    }
}

/**
 * Canvas rendering the standard 1..6 pips (dots) of a die.
 */
@Composable
private fun DicePipsCanvas(value: Int) {
    Canvas(modifier = Modifier.size(40.dp)) {
        val w = size.width
        val pipRadius = w * 0.09f
        val pipColor = Color(0xFF1E293B)

        val left = w * 0.25f
        val center = w * 0.5f
        val right = w * 0.75f

        val top = w * 0.25f
        val middle = w * 0.5f
        val bottom = w * 0.75f

        when (value.coerceIn(1, 6)) {
            1 -> {
                drawCircle(color = Color(0xFFEF4444), radius = pipRadius * 1.3f, center = Offset(center, middle))
            }
            2 -> {
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, top))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, bottom))
            }
            3 -> {
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, top))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(center, middle))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, bottom))
            }
            4 -> {
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, top))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, top))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, bottom))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, bottom))
            }
            5 -> {
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, top))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, top))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(center, middle))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, bottom))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, bottom))
            }
            6 -> {
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, top))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, top))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, middle))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, middle))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, bottom))
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, bottom))
            }
        }
    }
}

/**
 * Computes (col, row) fractional unit on the 15x15 board for a piece.
 */
private fun calculatePieceCoordinates(piece: LudoPiece): Pair<Float, Float> {
    // 1. In Yard Pods (step == -1)
    if (piece.step == -1) {
        val (baseCol, baseRow) = when (piece.color) {
            BoardColor.RED -> Pair(0f, 0f)
            BoardColor.GREEN -> Pair(9f, 0f)
            BoardColor.YELLOW -> Pair(9f, 9f)
            BoardColor.BLUE -> Pair(0f, 9f)
        }
        val (offsetCol, offsetRow) = when (piece.pieceIndex % 4) {
            0 -> Pair(1.5f, 1.5f)
            1 -> Pair(3.5f, 1.5f)
            2 -> Pair(1.5f, 3.5f)
            else -> Pair(3.5f, 3.5f)
        }
        return Pair(baseCol + offsetCol, baseRow + offsetRow)
    }

    // 2. On Common Outer Track (0..50)
    // 52-step classic circular track coordinates
    val trackCoordinates = listOf(
        // Red start and arm (0..4)
        Pair(1f, 6f), Pair(2f, 6f), Pair(3f, 6f), Pair(4f, 6f), Pair(5f, 6f),
        // Turn up to Green arm (5..10)
        Pair(6f, 5f), Pair(6f, 4f), Pair(6f, 3f), Pair(6f, 2f), Pair(6f, 1f), Pair(6f, 0f),
        Pair(7f, 0f), Pair(8f, 0f),
        // Down Green arm (13..17)
        Pair(8f, 1f), Pair(8f, 2f), Pair(8f, 3f), Pair(8f, 4f), Pair(8f, 5f),
        // Turn right to Yellow arm (18..23)
        Pair(9f, 6f), Pair(10f, 6f), Pair(11f, 6f), Pair(12f, 6f), Pair(13f, 6f), Pair(14f, 6f),
        Pair(14f, 7f), Pair(14f, 8f),
        // Down Yellow arm (26..30)
        Pair(13f, 8f), Pair(12f, 8f), Pair(11f, 8f), Pair(10f, 8f), Pair(9f, 8f),
        // Turn down to Blue arm (31..36)
        Pair(8f, 9f), Pair(8f, 10f), Pair(8f, 11f), Pair(8f, 12f), Pair(8f, 13f), Pair(8f, 14f),
        Pair(7f, 14f), Pair(6f, 14f),
        // Up Blue arm (39..43)
        Pair(6f, 13f), Pair(6f, 12f), Pair(6f, 11f), Pair(6f, 10f), Pair(6f, 9f),
        // Turn left to Red start (44..50)
        Pair(5f, 8f), Pair(4f, 8f), Pair(3f, 8f), Pair(2f, 8f), Pair(1f, 8f), Pair(0f, 8f), Pair(0f, 7f)
    )

    // Offset based on color start point
    val colorOffset = when (piece.color) {
        BoardColor.RED -> 0
        BoardColor.GREEN -> 13
        BoardColor.YELLOW -> 26
        BoardColor.BLUE -> 39
    }

    if (piece.step in 0..50) {
        val index = (piece.step + colorOffset) % trackCoordinates.size
        return trackCoordinates[index]
    }

    // 3. Home Run Runways (step 51..55)
    val homeStep = (piece.step - 51).coerceIn(0, 4)
    return when (piece.color) {
        BoardColor.RED -> Pair(1f + homeStep, 7f)
        BoardColor.GREEN -> Pair(7f, 1f + homeStep)
        BoardColor.YELLOW -> Pair(13f - homeStep, 7f)
        BoardColor.BLUE -> Pair(7f, 13f - homeStep)
    }
}

/**
 * Factory function to create a default set of 16 pieces (4 for each color).
 */
fun createDefaultLudoPieces(): List<LudoPiece> {
    val list = mutableListOf<LudoPiece>()
    for (color in BoardColor.values()) {
        for (i in 0..3) {
            list.add(
                LudoPiece(
                    id = "${color.name.lowercase()}_$i",
                    color = color,
                    pieceIndex = i,
                    step = if (i == 0) 0 else -1, // First piece on starting square
                    isMovable = (color == BoardColor.RED)
                )
            )
        }
    }
    return list
}

/**
 * Provides a default set of 16 pieces (4 for each color).
 */
@Composable
fun rememberDefaultPieces(): androidx.compose.runtime.snapshots.SnapshotStateList<LudoPiece> {
    return remember {
        mutableStateListOf<LudoPiece>().apply {
            addAll(createDefaultLudoPieces())
        }
    }
}

