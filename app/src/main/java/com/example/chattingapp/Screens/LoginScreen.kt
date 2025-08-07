package com.example.chattingapp.Screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.example.chattingapp.CheckSignedIn
import com.example.chattingapp.DestinationScreen
import com.example.chattingapp.GoogleSignInHelper
import com.example.chattingapp.LCViewModel
import com.example.chattingapp.R
import com.example.chattingapp.commonProgressBar
import com.example.chattingapp.navigateTO
import com.example.chattingapp.NotificationMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, vm : LCViewModel) {
    NotificationMessage(vm = vm)
    CheckSignedIn(vm,navController)
    
    val context = LocalContext.current
    val googleSignInHelper = remember { GoogleSignInHelper(context) }
    
    // State for Google sign-in flow - moved to top level
    val showProfileCompletion = remember { mutableStateOf(false) }
    val googleSignInAccount = remember { mutableStateOf<com.google.android.gms.auth.api.signin.GoogleSignInAccount?>(null) }
    
    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("LoginScreen", "Google Sign-In result received")
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = googleSignInHelper.handleSignInResult(task)
        if (account != null) {
            Log.d("LoginScreen", "Google account received: ${account.email}")
            // Handle complete Google Sign-In flow with proper authentication
            vm.handleGoogleSignIn(
                account = account,
                onSuccess = { userData ->
                    // User exists and is now signed in
                    vm.signIn.value = true
                },
                onNewUser = {
                    // New user, show profile completion
                    googleSignInAccount.value = account
                    showProfileCompletion.value = true
                },
                onError = { error ->
                    vm.handleException(customMessage = error)
                }
            )
        } else {
            Log.e("LoginScreen", "Google Sign-In failed - no account returned")
        }
    }
    
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
            var passwordVisible by remember { mutableStateOf(false) }
            var autoHidePassword by remember { mutableStateOf(true) }
            val coroutineScope = rememberCoroutineScope()

            // Auto-hide password after 3 seconds of no typing
            LaunchedEffect(passwordState.value.text) {
                if (passwordState.value.text.isNotEmpty() && passwordVisible) {
                    delay(3000) // 3 seconds delay
                    if (autoHidePassword) {
                        passwordVisible = false
                    }
                }
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
                    // Show password temporarily when typing
                    if (it.text.isNotEmpty()) {
                        passwordVisible = true
                        autoHidePassword = true
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                            autoHidePassword = false // Disable auto-hide when manually toggled
                        }
                    ) {
                        Text(
                            text = if (passwordVisible) "👁️" else "🙈",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                singleLine = true
            )
            Button(onClick = {
                vm.logIN(emailState.value.text, passwordState.value.text)
            }, modifier = Modifier.padding(8.dp)) {
                Text(text = "LOG IN")
            }
            
            // OR Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            
            // Google Sign-In Button
            OutlinedButton(
                onClick = {
                    Log.d("LoginScreen", "Google Sign-In button clicked")
                    try {
                        val signInIntent = googleSignInHelper.getSignInIntent()
                        googleSignInLauncher.launch(signInIntent)
                        Log.d("LoginScreen", "Google Sign-In intent launched")
                    } catch (e: Exception) {
                        Log.e("LoginScreen", "Error launching Google Sign-In", e)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    Color.Gray.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
    
    // Show Profile Completion Screen when Google Sign-In is successful
    if (showProfileCompletion.value && googleSignInAccount.value != null) {
        ProfileCompletionScreen(
            navController = navController,
            vm = vm,
            googleAccount = googleSignInAccount.value!!,
            onProfileComplete = {
                showProfileCompletion.value = false
                googleSignInAccount.value = null
            }
        )
    }
}