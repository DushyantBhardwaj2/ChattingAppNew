package com.example.chattingapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.Font
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, vm: LCViewModel) {
    CheckSignedIn(vm,navController)
    Box(modifier = Modifier.fillMaxSize().padding(top=40.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var nameState = remember {
                mutableStateOf(TextFieldValue())
            }
            var numberState = remember {
                mutableStateOf(TextFieldValue())
            }
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
                text = "  Sign UP  ",
                fontSize = 30.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = nameState.value,
                label = { Text(text = "Name") },
                modifier = Modifier.padding(8.dp),
                onValueChange = {
                    nameState.value = it
                }
            )
            OutlinedTextField(
                value = numberState.value,
                label = { Text(text = "Phone Number") },
                modifier = Modifier.padding(8.dp),
                onValueChange = {
                    numberState.value = it
                }
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
                vm.signUP(
                    name=nameState.value.text,
                    number=numberState.value.text,
                    email=emailState.value.text,
                    password = passwordState.value.text
                )
            }, modifier = Modifier.padding(8.dp)) {
                Text(text = "Sign UP")
            }
            Text(text = "Already A User? Go to Login ->",
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                fontFamily = FontFamily.Cursive, modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTO(navController, DestinationScreen.Login.route)
                    }
            )


        }

    }
    if (vm.inProcess.value) {
        commonProgressBar()

    }
}