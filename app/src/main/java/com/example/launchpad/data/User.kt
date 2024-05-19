package com.example.launchpad.data

import android.app.Application
import android.graphics.BitmapFactory
import com.example.launchpad.R
import com.example.launchpad.util.toBlob
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val avatar: Blob = Blob.fromBytes(ByteArray(0)),
    val provider: String = "",
    val company_id: String = "",
    var isEnterprise: Boolean = false,
    val token: String = "",
)
