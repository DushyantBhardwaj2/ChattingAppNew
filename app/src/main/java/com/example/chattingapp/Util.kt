package com.example.chattingapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import androidx.compose.ui.platform.LocalContext

fun navigateTO(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun commonProgressBar() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) {}
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun commonDivider() {
    HorizontalDivider(
        modifier = Modifier
            .alpha(2f)
            ,
        thickness = 1.dp,
        color = Color.Black
    )
}

@Composable
fun CheckSignedIn(vm: LCViewModel, navController: NavController) {
    val alreadySignIn = remember { mutableStateOf(false) }
    val signIn = vm.signIn.value
    if (signIn && !alreadySignIn.value) {
        alreadySignIn.value = true
        navController.navigate(DestinationScreen.ChatList.route) {
            popUpTo(0)
        }

    }
}

@Composable
fun CommonImage(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberAsyncImagePainter(model = data)
    Image(
        painter = painter, contentDescription = null,
        modifier = modifier, contentScale = contentScale,

    )
}

@Composable
fun Titletext(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 35.sp,
        modifier = Modifier.padding(10.dp),
        textDecoration = TextDecoration.Underline, color = Color.Black
    )
}

@Composable
fun CommonRow(
    profileIcon: Int,
    name: String?,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable {
                onItemClick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileIcon(
            iconRes = profileIcon,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
        )
        Text(
            text = name ?: "-----",
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun ProfileIcon(
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = if (iconRes == 0) com.example.chattingapp.data.ProfileIcons.getDefaultIcon() else iconRes),
        contentDescription = "Profile Icon",
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Gray.copy(alpha = 0.3f))
    )
}

@Composable
fun NotificationMessage(vm: LCViewModel) {
    val notifState = vm.eventMutableState.value
    val notifMessage = notifState?.getContentOrNull()
    if (notifMessage != null) {
        val context = LocalContext.current
        LaunchedEffect(key1 = notifMessage) {
            Toast.makeText(context, notifMessage, Toast.LENGTH_LONG).show()
        }
    }
}



