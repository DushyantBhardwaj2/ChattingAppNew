package com.example.chattingapp.data

import com.example.chattingapp.R

object ProfileIcons {
    private val iconList = listOf(
        R.drawable.ic_profile_default,
        R.drawable.ic_profile_1,
        R.drawable.ic_profile_2,
        R.drawable.ic_profile_3,
        R.drawable.ic_profile_4
    )
    
    fun getRandomIcon(): Int {
        return iconList.random()
    }
    
    fun getIconByIndex(index: Int): Int {
        return iconList.getOrElse(index % iconList.size) { iconList[0] }
    }
    
    fun getIconByUserId(userId: String): Int {
        // Generate consistent icon based on user ID hash
        val hashCode = userId.hashCode()
        val index = kotlin.math.abs(hashCode) % iconList.size
        return iconList[index]
    }
    
    fun getDefaultIcon(): Int {
        return R.drawable.ic_profile_default
    }
}
