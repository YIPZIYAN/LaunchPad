package com.example.launchpad.data

data class Chat(
    val id: String = "",
    val receiverID: String = "",
    val latestMessage: String = "",
    val sendTime: Long = 0,
    val numOfUnreadMsg: Int = 0,
)