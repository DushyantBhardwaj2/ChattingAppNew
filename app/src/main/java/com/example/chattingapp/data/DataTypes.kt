package com.example.chattingapp.data

// Removed duplicate UserData and ChatData definitions. Only ChatUser and Message remain.

data class ChatUser(
    val userID : String?="",
    val name:String?="",
    val profileIcon: Int = 0,
    val number: String?=""
)
data class  Message(
    val sendBy:String?="",
    val message: String?="",
    val timeStamp:String?=""
)