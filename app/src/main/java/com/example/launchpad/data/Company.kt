package com.example.launchpad.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.firestore

data class Company(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val avatar: Blob = Blob.fromBytes(ByteArray(0)),
    val description: String = "",
    val location: String = "",
    val year: Int = -1,
)
