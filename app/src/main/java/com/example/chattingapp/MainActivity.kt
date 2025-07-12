package com.example.chattingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chattingapp.Screens.ChatListScreen
import com.example.chattingapp.Screens.LoginScreen
import com.example.chattingapp.Screens.Profile
import com.example.chattingapp.Screens.SignUpScreen
import com.example.chattingapp.Screens.SingleChatScreen
import com.example.chattingapp.ui.theme.ChattingAppTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class DestinationScreen(var route: String) {
    object SignUp : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object ChatList : DestinationScreen("chatlist")
    object SingleChat : DestinationScreen("singlechat/{chatId}") {
        fun createRoute(chatID: String?) = "singlechat/$chatID"
    }


}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            ChattingAppTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ChatAppNavigation(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                    )
                }
            }
        }
    }

    @Composable
    fun ChatAppNavigation(
        isDarkTheme: Boolean,
        onThemeChange: (Boolean) -> Unit
    ) {
        val navController = rememberNavController()
        val vm = hiltViewModel<LCViewModel>()
        NavHost(navController = navController, startDestination = DestinationScreen.SignUp.route) {
            composable(
                DestinationScreen.SignUp.route
            ) {
                SignUpScreen(navController, vm)
            }
            composable(
                DestinationScreen.Login.route
            ) {
                LoginScreen(navController, vm)
            }
            composable(
                DestinationScreen.ChatList.route
            ) {
                ChatListScreen(navController,vm)
            }
            composable(
                DestinationScreen.SingleChat.route
            ) {
                val chatId=it.arguments?.getString("chatId")
                chatId?.let {
                    SingleChatScreen(navController,vm=vm,chatId)
            }
            }

            composable(
                DestinationScreen.Profile.route
            ) {
                Profile(navController, vm, isDarkTheme, onThemeChange)
            }

        }

    }
}
