package com.example.launchpad.data

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val provider: String = "",
    val avatar: String = "",
)
