package com.example.launchpad.data

data class Chat(
    val id: String = "",
    val receiverID: String = "",
    val latestMessage: ChatMessage = ChatMessage(),
    val numOfUnreadMsg: Int = 0,
)