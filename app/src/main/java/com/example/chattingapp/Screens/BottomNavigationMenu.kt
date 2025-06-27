package com.example.chattingapp.Screens

import android.os.Bundle
import android.util.Log

import android.text.Highlights
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chattingapp.DestinationScreen
import com.example.chattingapp.R
import com.example.chattingapp.navigateTO

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {
    CHATLIST(R.drawable.chatnavigationicon, DestinationScreen.ChatList),
    PROFILE(R.drawable.profilenavigationicon, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom, // Places the content at the bottom
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 4.dp)
                .background(color = Color.Transparent)
                .border(width = 1.dp, color =Color.White, shape = RectangleShape)
        ) {
            for (item in BottomNavigationItem.values()) {
                Image(
                    painter = painterResource(id = item.icon), contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .weight(1f)
                        .clickable { navigateTO(navController, item.navDestination.route) }
                        .background(
                            if (item == selectedItem) Color.Gray else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                )

            }
        }
    }


}