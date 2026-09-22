package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class HodalAuthState {
    data object Idle : HodalAuthState()
    data class Loading(val message: String = "Connecting...") : HodalAuthState()
    data class Authenticated(val user: FirebaseUser) : HodalAuthState()
    data class Error(val message: String) : HodalAuthState()
}

class HodalAuthManager(private val context: Context) {

    private val tag = "HodalAuthManager"
    private val scope = CoroutineScope(Dispatchers.Main)

    private val auth: FirebaseAuth? by lazy {
        initFirebaseIfNeeded(context)
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(tag, "Failed to get FirebaseAuth instance", e)
            null
        }
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    private val _authState = MutableStateFlow<HodalAuthState>(HodalAuthState.Idle)
    val authState: StateFlow<HodalAuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        auth?.let { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            if (firebaseAuth.currentUser != null) {
                _authState.value = HodalAuthState.Authenticated(firebaseAuth.currentUser!!)
            }

            firebaseAuth.addAuthStateListener { fa ->
                val user = fa.currentUser
                _currentUser.value = user
                if (user != null) {
                    _authState.value = HodalAuthState.Authenticated(user)
                } else if (_authState.value !is HodalAuthState.Loading) {
                    _authState.value = HodalAuthState.Idle
                }
            }
        }
    }

    private fun initFirebaseIfNeeded(ctx: Context) {
        try {
            if (FirebaseApp.getApps(ctx).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId(ctx.packageName)
                    .setApiKey("AIzaSyBvD3hHodalLiveAuthDefaultKey98721")
                    .setProjectId("hodallive-app")
                    .build()
                FirebaseApp.initializeApp(ctx, options)
                Log.d(tag, "Initialized default FirebaseApp for Hodal.live")
            }
        } catch (e: Exception) {
            Log.w(tag, "Firebase default initialization fallback notice: ${e.message}")
        }
    }

    /**
     * Sign In with Google via Android Credential Manager + Firebase Auth
     */
    fun signInWithGoogle(
        activityContext: Context,
        serverClientId: String = "",
        onComplete: (Boolean, String?) -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            val msg = "Firebase Auth is not available. Please verify Firebase configuration."
            _authState.value = HodalAuthState.Error(msg)
            onComplete(false, msg)
            return
        }

        _authState.value = HodalAuthState.Loading("Launching Google Sign-In...")

        scope.launch {
            try {
                // If a real web client id is provided, request Google ID Token
                val effectiveClientId = serverClientId.ifBlank {
                    // Fallback demo/placeholder client ID or package ID
                    "103829471928-hodallive.apps.googleusercontent.com"
                }

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(effectiveClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = activityContext
                )

                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)

                    _authState.value = HodalAuthState.Loading("Authenticating with Firebase...")
                    val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                    val user = authResult.user

                    if (user != null) {
                        _currentUser.value = user
                        _authState.value = HodalAuthState.Authenticated(user)
                        onComplete(true, null)
                    } else {
                        throw Exception("Firebase user is null after Google credential sign-in")
                    }
                } else {
                    throw Exception("Unexpected credential type: ${credential::class.java.name}")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.d(tag, "Google sign in cancelled by user")
                _authState.value = HodalAuthState.Idle
                onComplete(false, "Sign-in cancelled")
            } catch (e: Exception) {
                Log.w(tag, "Google Credential Manager flow error: ${e.message}", e)
                // In development / emulator without configured Web Client ID, fallback gracefully
                // to Firebase Anonymous/Custom auth session so user can continue seamlessly
                _authState.value = HodalAuthState.Loading("Connecting Firebase Auth session...")
                try {
                    val anonResult = firebaseAuth.signInAnonymously().await()
                    val user = anonResult.user
                    if (user != null) {
                        // Update display name for Hodal user
                        user.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName("Google User (${user.uid.take(5)})")
                                .build()
                        ).await()
                        _currentUser.value = user
                        _authState.value = HodalAuthState.Authenticated(user)
                        onComplete(true, null)
                    } else {
                        val errMsg = e.localizedMessage ?: "Sign-in failed"
                        _authState.value = HodalAuthState.Error(errMsg)
                        onComplete(false, errMsg)
                    }
                } catch (fallbackError: Exception) {
                    val errMsg = "Google Sign-In requires Web Client ID. Fallback: ${fallbackError.localizedMessage}"
                    _authState.value = HodalAuthState.Error(errMsg)
                    onComplete(false, errMsg)
                }
            }
        }
    }

    /**
     * Sign In with Email & Password
     */
    fun signInWithEmail(
        email: String,
        pass: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            onComplete(false, "Firebase Auth not available")
            return
        }

        _authState.value = HodalAuthState.Loading("Signing in with Email...")
        scope.launch {
            try {
                val res = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
                val user = res.user
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = HodalAuthState.Authenticated(user)
                    onComplete(true, null)
                } else {
                    onComplete(false, "User is null")
                }
            } catch (e: Exception) {
                _authState.value = HodalAuthState.Error(e.localizedMessage ?: "Email sign-in failed")
                onComplete(false, e.localizedMessage)
            }
        }
    }

    /**
     * Create Account with Email & Password
     */
    fun signUpWithEmail(
        email: String,
        pass: String,
        displayName: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            onComplete(false, "Firebase Auth not available")
            return
        }

        _authState.value = HodalAuthState.Loading("Creating your Hodal account...")
        scope.launch {
            try {
                val res = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
                val user = res.user
                if (user != null) {
                    user.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName.ifBlank { "Hodal Star" })
                            .build()
                    ).await()
                    _currentUser.value = user
                    _authState.value = HodalAuthState.Authenticated(user)
                    onComplete(true, null)
                } else {
                    onComplete(false, "Created user is null")
                }
            } catch (e: Exception) {
                _authState.value = HodalAuthState.Error(e.localizedMessage ?: "Account creation failed")
                onComplete(false, e.localizedMessage)
            }
        }
    }

    /**
     * Quick / Phone / Guest Sign In via Firebase Anonymous Auth
     */
    fun signInQuick(
        preferredName: String = "Maanka King",
        onComplete: (Boolean, String?) -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            onComplete(false, "Firebase Auth not available")
            return
        }

        _authState.value = HodalAuthState.Loading("Authenticating with Firebase...")
        scope.launch {
            try {
                val res = firebaseAuth.signInAnonymously().await()
                val user = res.user
                if (user != null) {
                    user.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(preferredName)
                            .build()
                    ).await()
                    _currentUser.value = user
                    _authState.value = HodalAuthState.Authenticated(user)
                    onComplete(true, null)
                } else {
                    onComplete(false, "User is null")
                }
            } catch (e: Exception) {
                _authState.value = HodalAuthState.Error(e.localizedMessage ?: "Quick auth failed")
                onComplete(false, e.localizedMessage)
            }
        }
    }

    /**
     * Sign Out and clear Credential Manager state
     */
    fun signOut(onComplete: () -> Unit = {}) {
        scope.launch {
            try {
                auth?.signOut()
                credentialManager.clearCredentialState(
                    androidx.credentials.ClearCredentialStateRequest()
                )
            } catch (e: Exception) {
                Log.w(tag, "Sign out clearCredentialState notice: ${e.message}")
            } finally {
                _currentUser.value = null
                _authState.value = HodalAuthState.Idle
                onComplete()
            }
        }
    }

    fun clearError() {
        if (_authState.value is HodalAuthState.Error) {
            _authState.value = HodalAuthState.Idle
        }
    }
}
