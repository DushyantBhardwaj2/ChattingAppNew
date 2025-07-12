package com.example.chattingapp.Screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chattingapp.CommonImage
import com.example.chattingapp.LCViewModel
import com.example.chattingapp.ProfileIcon
import com.example.chattingapp.commonDivider
import com.example.chattingapp.data.Message
import com.example.chattingapp.ui.theme.ChatTheme

@Composable
fun SingleChatScreen(navController: NavController, vm: LCViewModel, chatId: String) {
    var reply by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    val onSendReply = {
        // Only send if message is not empty or just whitespace
        if (reply.trim().isNotEmpty()) {
            vm.onSendReply(chatId, reply.trim())
            reply = ""
        }
    }

    val myUser = vm.userData.value
    var chatMessage = vm.chatMessages
    var currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser = if (myUser?.userID == currentChat.user1.userID) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit) {
        vm.populateMessages(chatId)
    }

    // Auto-scroll to bottom when messages change
    LaunchedEffect(chatMessage.value.size) {
        if (chatMessage.value.isNotEmpty()) {
            listState.animateScrollToItem(chatMessage.value.size - 1)
        }
    }

    BackHandler {
        vm.depopulateMessage()
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ChatHeader(name = chatUser.name ?: "", profileIcon = chatUser.profileIcon ?: 0) {
            navController.popBackStack()
            vm.depopulateMessage()
        }

        commonDivider()
        MessageBox(
            modifier = Modifier.weight(1f),
            chatMessages = chatMessage.value,
            currentUserId = myUser?.userID ?: "",
            listState = listState
        )
        ReplyBox(
            reply = reply, 
            onReplyChange = { reply = it }, 
            onSendReply = onSendReply,
            canSend = reply.trim().isNotEmpty()
        )
    }
}

@Composable
fun MessageBox(modifier: Modifier, chatMessages: List<Message>, currentUserId: String, listState: LazyListState) {
    LazyColumn(
        state = listState,
        modifier = modifier
            .navigationBarsPadding() // Adjust for navigation bar insets
    ) {
        items(chatMessages) { msg ->
            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val backgroundColor = if (msg.sendBy == currentUserId) ChatTheme.myMessageBackground else ChatTheme.otherMessageBackground
            val textColor = if (msg.sendBy == currentUserId) ChatTheme.myMessageText else ChatTheme.otherMessageText

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
                        .background(backgroundColor)
                        .padding(12.dp),
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit, canSend: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.surface)
            .imePadding() // Adjust for keyboard insets
    ) {
        commonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = reply, 
                onValueChange = onReplyChange, 
                maxLines = 3,
                placeholder = { Text("Type a message...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (canSend) {
                            onSendReply()
                        }
                    }
                ),
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Button(
                onClick = onSendReply,
                enabled = canSend,
                modifier = Modifier.padding(start = 8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                )
            ) {
                Text(text = "Send")
            }
        }
    }
}

@Composable
fun ChatHeader(name: String, profileIcon: Int, onBackClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .clickable { onBackClicked.invoke() }
                .padding(8.dp)
                .border(
                    width = 2.dp,
                    color = ChatTheme.profileIconBorder,
                    shape = RoundedCornerShape(4.dp)
                )
        )
        ProfileIcon(
            iconRes = profileIcon,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
        )
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}