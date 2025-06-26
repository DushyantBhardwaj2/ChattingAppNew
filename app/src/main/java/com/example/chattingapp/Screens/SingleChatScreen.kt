package com.example.chattingapp.Screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chattingapp.CommonImage
import com.example.chattingapp.LCViewModel
import com.example.chattingapp.commonDivider
import com.example.chattingapp.data.Message

@Composable
fun SingleChatScreen(navController: NavController, vm: LCViewModel, chatId: String) {
    var reply by rememberSaveable { mutableStateOf("") }
    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }

    val myUser = vm.userData.value
    var chatMessage = vm.chatMessages
    var currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser = if (myUser?.userID == currentChat.user1.userID) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit) {
        vm.populateMessages(chatId)
    }

    BackHandler {
        vm.depopulateMessage()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        ChatHeader(name = chatUser.name ?: "", imageUrl = chatUser.imageUrl ?: "") {
            navController.popBackStack()
            vm.depopulateMessage()
        }

        commonDivider()
        MessageBox(
            modifier = Modifier.weight(1f),
            chatMessages = chatMessage.value,
            currentUserId = myUser?.userID ?: ""
        )
        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply)
    }
}

@Composable
fun MessageBox(modifier: Modifier, chatMessages: List<Message>, currentUserId: String) {
    LazyColumn(
        modifier = modifier
            .navigationBarsPadding() // Adjust for navigation bar insets
    ) {
        items(chatMessages) { msg ->
            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) Color.Green else Color.Gray

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
                Text(
                    text = msg.message ?: "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(12.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding() // Adjust for keyboard insets
    ) {
        commonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(value = reply, onValueChange = onReplyChange, maxLines = 3)
            Button(onClick = onSendReply) {
                Text(text = "Send")
            }
        }
    }
}

@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .clickable { onBackClicked.invoke() }
                .padding(8.dp)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(4.dp)
                )
        )
        // Display profile picture or initial letter
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .border(
                    width = 4.dp,
                    color = Color.Black,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl.isNotEmpty()) {
                // Display profile picture if available
                CommonImage(
                    data = imageUrl,
                    modifier = Modifier.size(50.dp)
                )
            } else {
                // Display initial letter if no profile picture is available
                Text(
                    text = name.firstOrNull()?.toString() ?: "",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}