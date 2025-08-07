package com.example.chattingapp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chattingapp.LCViewModel

@Composable
fun PhonePasswordScreen(navController: NavController, vm: LCViewModel, onComplete: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            if (error.isNotEmpty()) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = {
                    if (phone.isBlank() || password.isBlank()) {
                        error = "Please enter both phone number and password."
                        return@Button
                    }
                    loading = true
                    vm.savePhoneAndPassword(phone, password, onSuccess = {
                        loading = false
                        onComplete()
                    }, onError = {
                        loading = false
                        error = it
                    })
                },
                enabled = !loading,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Continue")
            }
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }
        }
    }
}
