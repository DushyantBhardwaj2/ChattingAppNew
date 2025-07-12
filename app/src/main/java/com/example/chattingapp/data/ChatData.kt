package com.example.chattingapp.data

import com.google.firebase.firestore.DocumentSnapshot

// Data class representing a chat between two users

data class ChatData(
    val chatId: String? = null,
    val user1: UserData = UserData(),
    val user2: UserData = UserData(),
    val createdAt: Long = 0L
)

