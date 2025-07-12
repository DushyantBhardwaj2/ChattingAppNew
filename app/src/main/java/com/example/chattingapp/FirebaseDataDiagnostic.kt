package com.example.chattingapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataDiagnostic @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    fun runDiagnostic() {
        Log.d("FirebaseDiagnostic", "=== STARTING FIREBASE DATA STRUCTURE DIAGNOSTIC ===")
        
        CoroutineScope(Dispatchers.IO).launch {
            checkUsersCollection()
            checkChatsCollection()
            checkCurrentUser()
        }
    }
    
    private fun checkUsersCollection() {
        Log.d("FirebaseDiagnostic", "--- Checking Users Collection ---")
        
        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                Log.d("FirebaseDiagnostic", "Total users found: ${result.documents.size}")
                
                result.documents.forEachIndexed { index, doc ->
                    Log.d("FirebaseDiagnostic", "User ${index + 1}:")
                    Log.d("FirebaseDiagnostic", "  Document ID: ${doc.id}")
                    
                    // Check required fields
                    val userID = doc.getString("userID")
                    val name = doc.getString("name")
                    val number = doc.getString("number")
                    val profileIcon = doc.getLong("profileIcon")
                    
                    Log.d("FirebaseDiagnostic", "  userID: ${userID ?: "❌ MISSING"}")
                    Log.d("FirebaseDiagnostic", "  name: ${name ?: "❌ MISSING"}")
                    Log.d("FirebaseDiagnostic", "  number: ${number ?: "❌ MISSING"}")
                    Log.d("FirebaseDiagnostic", "  profileIcon: ${profileIcon ?: "❌ MISSING"}")
                    
                    // Check for issues
                    val issues = mutableListOf<String>()
                    if (userID.isNullOrBlank()) issues.add("Missing userID")
                    if (name.isNullOrBlank()) issues.add("Missing name")
                    if (number.isNullOrBlank()) issues.add("Missing number")
                    if (profileIcon == null) issues.add("Missing profileIcon")
                    if (userID != doc.id) issues.add("userID doesn't match document ID")
                    
                    if (issues.isNotEmpty()) {
                        Log.w("FirebaseDiagnostic", "  ⚠️ ISSUES: ${issues.joinToString(", ")}")
                    } else {
                        Log.d("FirebaseDiagnostic", "  ✅ User data structure is correct")
                    }
                    
                    Log.d("FirebaseDiagnostic", "  ---")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseDiagnostic", "❌ Failed to fetch users collection", exception)
            }
    }
    
    private fun checkChatsCollection() {
        Log.d("FirebaseDiagnostic", "--- Checking Chats Collection ---")
        
        firestore.collection("chats")
            .get()
            .addOnSuccessListener { result ->
                Log.d("FirebaseDiagnostic", "Total chats found: ${result.documents.size}")
                
                result.documents.forEachIndexed { index, doc ->
                    Log.d("FirebaseDiagnostic", "Chat ${index + 1}:")
                    Log.d("FirebaseDiagnostic", "  Document ID: ${doc.id}")
                    
                    // Check required fields
                    val users = doc.get("users") as? List<*>
                    val createdAt = doc.getLong("createdAt")
                    val lastMessage = doc.getString("lastMessage")
                    val lastMessageTime = doc.getLong("lastMessageTime")
                    
                    Log.d("FirebaseDiagnostic", "  users: ${users?.toString() ?: "❌ MISSING"}")
                    Log.d("FirebaseDiagnostic", "  createdAt: ${createdAt ?: "❌ MISSING"}")
                    Log.d("FirebaseDiagnostic", "  lastMessage: ${lastMessage ?: "❌ MISSING"}")
                    Log.d("FirebaseDiagnostic", "  lastMessageTime: ${lastMessageTime ?: "❌ MISSING"}")
                    
                    // Check for issues
                    val issues = mutableListOf<String>()
                    if (users == null) issues.add("Missing users array")
                    else if (users.size != 2) issues.add("Users array should have exactly 2 items, has ${users.size}")
                    if (createdAt == null) issues.add("Missing createdAt")
                    if (lastMessage == null) issues.add("Missing lastMessage")
                    if (lastMessageTime == null) issues.add("Missing lastMessageTime")
                    
                    if (issues.isNotEmpty()) {
                        Log.w("FirebaseDiagnostic", "  ⚠️ ISSUES: ${issues.joinToString(", ")}")
                    } else {
                        Log.d("FirebaseDiagnostic", "  ✅ Chat data structure is correct")
                    }
                    
                    Log.d("FirebaseDiagnostic", "  ---")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseDiagnostic", "❌ Failed to fetch chats collection", exception)
            }
    }
    
    private fun checkCurrentUser() {
        Log.d("FirebaseDiagnostic", "--- Checking Current User ---")
        
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("FirebaseDiagnostic", "Current authenticated user ID: ${currentUser.uid}")
            Log.d("FirebaseDiagnostic", "Current user email: ${currentUser.email}")
            
            // Check if current user exists in Firestore
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        Log.d("FirebaseDiagnostic", "✅ Current user found in Firestore")
                        Log.d("FirebaseDiagnostic", "  Data: ${doc.data}")
                    } else {
                        Log.w("FirebaseDiagnostic", "⚠️ Current user NOT found in Firestore users collection")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseDiagnostic", "❌ Failed to check current user in Firestore", exception)
                }
        } else {
            Log.w("FirebaseDiagnostic", "⚠️ No authenticated user")
        }
        
        Log.d("FirebaseDiagnostic", "=== DIAGNOSTIC COMPLETE ===")
    }
}
