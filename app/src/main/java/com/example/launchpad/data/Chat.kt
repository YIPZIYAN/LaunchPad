package com.example.launchpad.data

import com.google.firebase.firestore.Blob

data class Chat(
    val id: String = "",
    val receiverName: String = "",
    val avatar: Blob = Blob.fromBytes(ByteArray(0)),
    var latestMessage: ChatMessage = ChatMessage(),
    var numOfUnreadMsg: Int = 0,
)