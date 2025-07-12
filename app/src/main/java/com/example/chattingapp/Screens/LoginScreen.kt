package com.example.chattingapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chattingapp.CheckSignedIn
import com.example.chattingapp.DestinationScreen
import com.example.chattingapp.LCViewModel
import com.example.chattingapp.R
import com.example.chattingapp.commonProgressBar
import com.example.chattingapp.navigateTO
import com.example.chattingapp.NotificationMessage

@Composable
fun LoginScreen(navController: NavController, vm : LCViewModel) {
    NotificationMessage(vm = vm)
    CheckSignedIn(vm,navController)
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var emailState = remember {
                mutableStateOf(TextFieldValue())
            }
            var passwordState = remember {
                mutableStateOf(TextFieldValue())
            }
            val focus = LocalFocusManager.current
            Image(
                painter = painterResource(id = R.drawable.signupchaticon),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(
                text = "  Sign IN  ",
                fontSize = 30.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(
                value = emailState.value,
                label = { Text(text = "Email") },
                modifier = Modifier.padding(8.dp),
                onValueChange = {
                    emailState.value = it
                }
            )
            OutlinedTextField(
                value = passwordState.value,
                label = { Text(text = "Password") },
                modifier = Modifier.padding(8.dp),
                onValueChange = {
                    passwordState.value = it
                }
            )
            Button(onClick = {
                vm.logIN(emailState.value.text, passwordState.value.text)
            }, modifier = Modifier.padding(8.dp)) {
                Text(text = "LOG IN")
            }
            Text(text = "Not a User || Register->",
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                fontFamily = FontFamily.Cursive, modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTO(navController, DestinationScreen.SignUp.route)
                    }
            )


        }

    }
    if (vm.inProcess.value) {
        commonProgressBar()

    }
}