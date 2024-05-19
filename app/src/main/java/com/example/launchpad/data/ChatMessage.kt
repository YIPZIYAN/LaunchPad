package com.example.launchpad.data

data class ChatMessage(
    val id: String = "",
    val senderID: String = "",
    val message: String = "",
    val sendTime: Long = 0
)
