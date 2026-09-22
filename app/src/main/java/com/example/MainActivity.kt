package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GiftItem
import com.example.ui.HodalViewModel
import com.example.ui.components.GiftDialog
import com.example.ui.components.HodalBottomNav
import com.example.ui.components.HodalNavTab
import com.example.ui.components.LuxuryGiftOverlay
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ConversationsScreen
import com.example.ui.screens.LudoGameScreen
import com.example.ui.screens.PrivateChatScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RoomsScreen
import com.example.ui.screens.VoicePartyRoomScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HodalApp()
            }
        }
    }
}

@Composable
fun HodalApp(viewModel: HodalViewModel = viewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val roomsList by viewModel.roomsList.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val activeRoom by viewModel.activeRoom.collectAsState()
    val partySeats by viewModel.partySeats.collectAsState()
    val roomMessages by viewModel.roomMessages.collectAsState()
    val privateMessages by viewModel.privateMessagesWithSufake.collectAsState()
    val isMicMuted by viewModel.isMicMuted.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val currentFirebaseUser by viewModel.currentFirebaseUser.collectAsState()

    // Gift effect overlay state
    val activeGiftEffect by viewModel.activeGiftEffect.collectAsState()
    val giftSender by viewModel.giftSenderName.collectAsState()
    val giftReceiver by viewModel.giftReceiverName.collectAsState()
    val giftCombo by viewModel.giftComboCount.collectAsState()

    // Ludo state
    val ludoPlayers by viewModel.ludoPlayers.collectAsState()
    val currentDice by viewModel.currentDiceValue.collectAsState()
    val isDiceRolling by viewModel.isDiceRolling.collectAsState()
    val ludoCollisionNotice by viewModel.ludoCollisionNotice.collectAsState()

    // Navigation and UI state
    var selectedTab by remember { mutableStateOf(HodalNavTab.ROOMS) }
    var inPrivateChatWith by remember { mutableStateOf<String?>(null) }
    var showGiftSheet by remember { mutableStateOf(false) }

    // Audio Permission Launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleMic()
        }
    }

    if (!isLoggedIn) {
        val context = androidx.compose.ui.platform.LocalContext.current
        AuthScreen(
            authState = authState,
            onGoogleSignIn = {
                viewModel.signInWithGoogle(context)
            },
            onEmailSignIn = { email, pass ->
                viewModel.signInWithEmail(email, pass)
            },
            onEmailSignUp = { email, pass, name ->
                viewModel.signUpWithEmail(email, pass, name)
            },
            onQuickSignIn = { name ->
                viewModel.signInQuick(name)
            },
            onClearError = {
                viewModel.clearAuthError()
            },
            onLoginSuccess = {
                viewModel.login()
            }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Screen Router
        if (activeRoom != null) {
            // Inside Voice Party Room (Screenshot 6 + 5)
            VoicePartyRoomScreen(
                room = activeRoom,
                seats = partySeats,
                messages = roomMessages,
                isMicMuted = isMicMuted,
                onToggleMic = {
                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                onSeatClick = { seat ->
                    viewModel.takeOrLeaveSeat(seat.seatIndex)
                },
                onSendMessage = { text ->
                    viewModel.sendRoomMessage(text)
                },
                onOpenGiftTray = {
                    showGiftSheet = true
                },
                onOpenLudoGame = {
                    selectedTab = HodalNavTab.LUDO
                    viewModel.leaveRoom()
                },
                onOpenEmojiTray = {
                    // Quick emoji cheer in room
                    viewModel.sendRoomMessage("😎✨🎉")
                },
                onLeaveRoom = {
                    viewModel.leaveRoom()
                }
            )
        } else if (inPrivateChatWith != null) {
            // 1-on-1 Encrypted Private Chat Screen (Screenshot 4)
            PrivateChatScreen(
                messages = privateMessages,
                onSendMessage = { text ->
                    viewModel.sendPrivateMessage(inPrivateChatWith!!, text)
                },
                onBack = {
                    inPrivateChatWith = null
                }
            )
        } else {
            // Main Bottom Navigation Scaffold
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    HodalBottomNav(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            selectedTab = tab
                        },
                        unreadMessagesCount = 1
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        HodalNavTab.ROOMS -> {
                            RoomsScreen(
                                rooms = roomsList,
                                selectedCategory = selectedCategory,
                                onCategorySelected = { cat ->
                                    viewModel.setCategory(cat)
                                },
                                onRoomClick = { room ->
                                    viewModel.enterRoom(room)
                                },
                                onCreateRoomClick = {
                                    // Enter first room or custom room
                                    val defaultRoom = roomsList.firstOrNull()
                                    if (defaultRoom != null) {
                                        viewModel.enterRoom(defaultRoom)
                                    }
                                }
                            )
                        }
                        HodalNavTab.LUDO -> {
                            LudoGameScreen(
                                players = ludoPlayers,
                                currentDice = currentDice,
                                isRolling = isDiceRolling,
                                collisionNotice = ludoCollisionNotice,
                                onRollDice = {
                                    viewModel.rollLudoDice()
                                },
                                onBack = {
                                    selectedTab = HodalNavTab.ROOMS
                                }
                            )
                        }
                        HodalNavTab.MESSAGES -> {
                            ConversationsScreen(
                                onConversationClick = { convo ->
                                    inPrivateChatWith = convo.userId
                                }
                            )
                        }
                        HodalNavTab.PROFILE -> {
                            ProfileScreen(
                                user = userProfile,
                                firebaseUser = currentFirebaseUser,
                                onRecharge = { amount ->
                                    viewModel.rechargeCoins(amount)
                                },
                                onSyncFirestore = {
                                    viewModel.syncProfileToFirestore()
                                },
                                onLogout = {
                                    viewModel.logout()
                                }
                            )
                        }
                    }
                }
            }
        }

        // Gift Picker Sheet (Modal)
        if (showGiftSheet) {
            GiftDialog(
                userCoins = userProfile?.coins ?: 1250000L,
                onDismiss = { showGiftSheet = false },
                onSendGift = { gift, combo ->
                    viewModel.triggerSendGift(gift, combo)
                },
                onRecharge = {
                    viewModel.rechargeCoins(500000L)
                }
            )
        }

        // Luxury Gift Full-screen Celebration Effect (Screenshot 5: Lion & Tiger confrontation!)
        if (activeGiftEffect != null) {
            LuxuryGiftOverlay(
                gift = activeGiftEffect,
                senderName = giftSender,
                receiverName = giftReceiver,
                comboCount = giftCombo,
                onDismiss = {
                    viewModel.dismissGiftEffect()
                }
            )
        }
    }
}
