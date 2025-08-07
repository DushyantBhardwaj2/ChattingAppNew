package com.example.chattingapp.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.chattingapp.LCViewModel
import com.example.chattingapp.R
import com.example.chattingapp.commonProgressBar
import com.example.chattingapp.NotificationMessage
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@Composable
fun ProfileCompletionScreen(
    navController: NavController, 
    vm: LCViewModel,
    googleAccount: GoogleSignInAccount,
    onProfileComplete: () -> Unit
) {
    NotificationMessage(vm = vm)
    
    var nameState by remember { 
        mutableStateOf(TextFieldValue(googleAccount.displayName ?: "")) 
    }
    var numberState by remember { 
        mutableStateOf(TextFieldValue()) 
    }
    var isValidating by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Header with Google Account Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google Profile Image
                    if (googleAccount.photoUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(googleAccount.photoUrl),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Color.Gray.copy(alpha = 0.3f),
                                    RoundedCornerShape(40.dp)
                                )
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.signupchaticon),
                            contentDescription = "Default Profile",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Welcome to ChatApp!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = googleAccount.email ?: "",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            
            Text(
                text = "Complete Your Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Please provide your details to continue",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Name Field
            OutlinedTextField(
                value = nameState,
                onValueChange = { nameState = it },
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )
            
            // Phone Number Field
            OutlinedTextField(
                value = numberState,
                onValueChange = { 
                    // Only allow digits and limit to reasonable phone number length
                    val filtered = it.text.filter { char -> char.isDigit() }
                    if (filtered.length <= 15) {
                        numberState = it.copy(text = filtered)
                    }
                },
                label = { Text("Phone Number") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                placeholder = { Text("Enter your phone number") }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Complete Profile Button
            Button(
                onClick = {
                    val name = nameState.text.trim()
                    val number = numberState.text.trim()
                    
                    when {
                        name.isEmpty() -> {
                            vm.handleException(customMessage = "Please enter your name")
                        }
                        name.length < 2 -> {
                            vm.handleException(customMessage = "Name must be at least 2 characters")
                        }
                        number.isEmpty() -> {
                            vm.handleException(customMessage = "Please enter your phone number")
                        }
                        number.length < 10 -> {
                            vm.handleException(customMessage = "Please enter a valid phone number (minimum 10 digits)")
                        }
                        else -> {
                            isValidating = true
                            vm.completeGoogleProfile(
                                googleAccount = googleAccount,
                                name = name,
                                number = number,
                                onSuccess = {
                                    isValidating = false
                                    onProfileComplete()
                                },
                                onError = { error ->
                                    isValidating = false
                                    vm.handleException(customMessage = error)
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !isValidating && !vm.inProcess.value
            ) {
                if (isValidating || vm.inProcess.value) {
                    Text("Validating...")
                } else {
                    Text(
                        text = "Complete Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
    
    if (vm.inProcess.value || isValidating) {
        commonProgressBar()
    }
}
