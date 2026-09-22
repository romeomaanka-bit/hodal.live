package com.example.data.repository

import com.google.firebase.firestore.FirebaseFirestore

/**
 * Concrete implementation of [UserRepository] specifically for Cloud Firestore.
 */
class FirestoreUserRepository(
    firestore: FirebaseFirestore? = getInitializedFirestoreInstance()
) : UserRepository(firestore)
