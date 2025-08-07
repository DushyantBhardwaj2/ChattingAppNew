package com.example.chattingapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GoogleSignInHelper(private val context: Context) {
    
    private val googleSignInClient: GoogleSignInClient
    
    companion object {
        const val RC_SIGN_IN = 9001
    }
    
    init {
        // Configure Google Sign-In with ID token for Firebase Authentication
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("512869849262-vr0ora4qqdab72l6fs804d8b0klnn80d.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>): GoogleSignInAccount? {
        return try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d("GoogleSignInHelper", "Sign in successful: ${account.email}")
            account
        } catch (e: ApiException) {
            Log.w("GoogleSignInHelper", "signInResult:failed code=" + e.statusCode + " message=" + e.message)
            when (e.statusCode) {
                10 -> Log.e("GoogleSignInHelper", "DEVELOPER_ERROR: Check your google-services.json configuration")
                12500 -> Log.e("GoogleSignInHelper", "SIGN_IN_REQUIRED: User needs to sign in")
                7 -> Log.e("GoogleSignInHelper", "NETWORK_ERROR: No internet connection")
                else -> Log.e("GoogleSignInHelper", "Unknown error: ${e.statusCode}")
            }
            null
        }
    }
    
    fun signOut() {
        googleSignInClient.signOut()
    }
    
    fun revokeAccess() {
        googleSignInClient.revokeAccess()
    }
}
