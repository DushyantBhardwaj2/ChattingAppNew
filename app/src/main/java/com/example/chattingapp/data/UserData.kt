package com.example.chattingapp.data

// Data class representing a user

data class UserData(
    val userID: String = "",
    val name: String? = null,
    val number: String? = null,
    val profileIcon: Int = 0 // Resource ID for profile icon instead of image URL
)

