package com.example.chattingapp.diagnostic

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataDiagnostic @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    fun checkDataStructure() {
        Log.d("FirebaseDiagnostic", "==========================================")
        Log.d("FirebaseDiagnostic", "🔍 STARTING COMPREHENSIVE FIREBASE DIAGNOSTIC")
        Log.d("FirebaseDiagnostic", "==========================================")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                checkAuthenticationStatus()
                checkUsersCollectionDetailed()
                checkChatsCollectionDetailed()
                provideDiagnosticSummary()
            } catch (e: Exception) {
                Log.e("FirebaseDiagnostic", "❌ Critical error during diagnostic", e)
            }
        }
    }
    
    private fun checkAuthenticationStatus() {
        Log.d("FirebaseDiagnostic", "")
        Log.d("FirebaseDiagnostic", "🔐 CHECKING AUTHENTICATION STATUS")
        Log.d("FirebaseDiagnostic", "----------------------------------------")
        
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Log.d("FirebaseDiagnostic", "✅ User is authenticated")
            Log.d("FirebaseDiagnostic", "   User ID: ${currentUser.uid}")
            Log.d("FirebaseDiagnostic", "   Email: ${currentUser.email}")
            Log.d("FirebaseDiagnostic", "   Display Name: ${currentUser.displayName ?: "Not set"}")
        } else {
            Log.w("FirebaseDiagnostic", "⚠️ No authenticated user found")
            Log.w("FirebaseDiagnostic", "   This may cause issues with data access")
        }
    }
    
    private suspend fun checkUsersCollectionDetailed() {
        Log.d("FirebaseDiagnostic", "")
        Log.d("FirebaseDiagnostic", "👥 CHECKING USERS COLLECTION")
        Log.d("FirebaseDiagnostic", "----------------------------------------")
        
        try {
            val usersSnapshot = firestore.collection("users").get().await()
            val documents = usersSnapshot.documents
            
            Log.d("FirebaseDiagnostic", "📊 USERS COLLECTION OVERVIEW:")
            Log.d("FirebaseDiagnostic", "   Total documents: ${documents.size}")
            
            if (documents.isEmpty()) {
                Log.e("FirebaseDiagnostic", "🚨 CRITICAL ISSUE: Users collection is EMPTY!")
                Log.e("FirebaseDiagnostic", "   No users found. You need to create users first.")
                Log.e("FirebaseDiagnostic", "   Solution: Complete the signup process for test users")
                return
            }
            
            var validUsers = 0
            var usersWithIssues = 0
            val phoneNumbers = mutableListOf<String>()
            
            documents.forEachIndexed { index, doc ->
                Log.d("FirebaseDiagnostic", "")
                Log.d("FirebaseDiagnostic", "👤 USER ${index + 1} ANALYSIS:")
                Log.d("FirebaseDiagnostic", "   Document ID: ${doc.id}")
                
                // Check all fields in document
                val allFields = doc.data ?: emptyMap()
                Log.d("FirebaseDiagnostic", "   All fields present: ${allFields.keys}")
                
                // Check required fields
                val userID = doc.getString("userID")
                val name = doc.getString("name")
                val number = doc.getString("number")
                val profileIcon = doc.get("profileIcon")
                
                Log.d("FirebaseDiagnostic", "   📋 FIELD ANALYSIS:")
                
                // UserID check
                if (userID != null) {
                    Log.d("FirebaseDiagnostic", "   ✅ userID: '$userID'")
                    if (userID == doc.id) {
                        Log.d("FirebaseDiagnostic", "      ✅ Matches document ID")
                    } else {
                        Log.w("FirebaseDiagnostic", "      ⚠️ Does NOT match document ID")
                    }
                } else {
                    Log.e("FirebaseDiagnostic", "   ❌ userID: MISSING")
                    usersWithIssues++
                }
                
                // Name check
                if (name != null && name.isNotBlank()) {
                    Log.d("FirebaseDiagnostic", "   ✅ name: '$name'")
                } else {
                    Log.e("FirebaseDiagnostic", "   ❌ name: MISSING or EMPTY")
                    usersWithIssues++
                }
                
                // Number check (CRITICAL)
                if (number != null && number.isNotBlank()) {
                    Log.d("FirebaseDiagnostic", "   ✅ number: '$number' (Type: ${number::class.simpleName})")
                    phoneNumbers.add(number)
                    
                    // Check if it's numeric
                    if (number.all { it.isDigit() }) {
                        Log.d("FirebaseDiagnostic", "      ✅ Contains only digits")
                    } else {
                        Log.w("FirebaseDiagnostic", "      ⚠️ Contains non-digit characters")
                    }
                } else {
                    Log.e("FirebaseDiagnostic", "   🚨 number: MISSING or EMPTY - CRITICAL ISSUE!")
                    Log.e("FirebaseDiagnostic", "      This will prevent users from being found by phone search!")
                    usersWithIssues++
                }
                
                // ProfileIcon check
                if (profileIcon != null) {
                    Log.d("FirebaseDiagnostic", "   ✅ profileIcon: $profileIcon (Type: ${profileIcon::class.simpleName})")
                } else {
                    Log.w("FirebaseDiagnostic", "   ⚠️ profileIcon: MISSING (will use default)")
                }
                
                // Overall user status
                if (userID != null && name != null && number != null) {
                    validUsers++
                    Log.d("FirebaseDiagnostic", "   ✅ User data structure: VALID")
                } else {
                    Log.e("FirebaseDiagnostic", "   ❌ User data structure: INVALID")
                }
            }
            
            Log.d("FirebaseDiagnostic", "")
            Log.d("FirebaseDiagnostic", "📈 USERS COLLECTION SUMMARY:")
            Log.d("FirebaseDiagnostic", "   Total users: ${documents.size}")
            Log.d("FirebaseDiagnostic", "   Valid users: $validUsers")
            Log.d("FirebaseDiagnostic", "   Users with issues: $usersWithIssues")
            Log.d("FirebaseDiagnostic", "   Phone numbers found: ${phoneNumbers.size}")
            Log.d("FirebaseDiagnostic", "   Available phone numbers: $phoneNumbers")
            
            if (phoneNumbers.isEmpty()) {
                Log.e("FirebaseDiagnostic", "🚨 CRITICAL: No phone numbers found!")
                Log.e("FirebaseDiagnostic", "   Cannot add chats by phone number!")
            }
            
        } catch (e: Exception) {
            Log.e("FirebaseDiagnostic", "❌ Failed to analyze users collection", e)
            
            // Check if it's a permission error
            if (e.message?.contains("PERMISSION_DENIED") == true || 
                e.message?.contains("Missing or insufficient permissions") == true) {
                Log.e("FirebaseDiagnostic", "")
                Log.e("FirebaseDiagnostic", "🚨 FIRESTORE SECURITY RULES ERROR DETECTED!")
                Log.e("FirebaseDiagnostic", "========================================")
                Log.e("FirebaseDiagnostic", "PROBLEM: Your Firestore Security Rules are blocking access")
                Log.e("FirebaseDiagnostic", "SOLUTION: You need to update your Firestore Security Rules")
                Log.e("FirebaseDiagnostic", "")
                Log.e("FirebaseDiagnostic", "STEPS TO FIX:")
                Log.e("FirebaseDiagnostic", "1. Go to Firebase Console")
                Log.e("FirebaseDiagnostic", "2. Select your project")
                Log.e("FirebaseDiagnostic", "3. Go to Firestore Database → Rules tab")
                Log.e("FirebaseDiagnostic", "4. Apply the security rules from FIRESTORE_RULES_FIX.md")
                Log.e("FirebaseDiagnostic", "5. Publish the changes")
                Log.e("FirebaseDiagnostic", "")
                Log.e("FirebaseDiagnostic", "THIS IS WHY NEW CHAT USERS DON'T APPEAR!")
                Log.e("FirebaseDiagnostic", "========================================")
            }
        }
    }
    
    private suspend fun checkChatsCollectionDetailed() {
        Log.d("FirebaseDiagnostic", "")
        Log.d("FirebaseDiagnostic", "💬 CHECKING CHATS COLLECTION")
        Log.d("FirebaseDiagnostic", "----------------------------------------")
        
        try {
            val chatsSnapshot = firestore.collection("chats").get().await()
            val documents = chatsSnapshot.documents
            
            Log.d("FirebaseDiagnostic", "📊 CHATS COLLECTION OVERVIEW:")
            Log.d("FirebaseDiagnostic", "   Total documents: ${documents.size}")
            
            if (documents.isEmpty()) {
                Log.i("FirebaseDiagnostic", "ℹ️ Chats collection is empty")
                Log.i("FirebaseDiagnostic", "   This is normal for new users or after cleanup")
                return
            }
            
            var validChats = 0
            var chatsWithIssues = 0
            
            documents.forEachIndexed { index, doc ->
                Log.d("FirebaseDiagnostic", "")
                Log.d("FirebaseDiagnostic", "💬 CHAT ${index + 1} ANALYSIS:")
                Log.d("FirebaseDiagnostic", "   Document ID: ${doc.id}")
                
                // Check all fields in document
                val allFields = doc.data ?: emptyMap()
                Log.d("FirebaseDiagnostic", "   All fields present: ${allFields.keys}")
                
                // Check required fields
                val users = doc.get("users")
                val createdAt = doc.get("createdAt")
                val lastMessage = doc.getString("lastMessage")
                val lastMessageTime = doc.get("lastMessageTime")
                
                Log.d("FirebaseDiagnostic", "   📋 FIELD ANALYSIS:")
                
                // Users array check
                if (users is List<*>) {
                    Log.d("FirebaseDiagnostic", "   ✅ users: $users (Type: List)")
                    Log.d("FirebaseDiagnostic", "      Array size: ${users.size}")
                    
                    if (users.size == 2) {
                        Log.d("FirebaseDiagnostic", "      ✅ Correct array size (2 users)")
                        users.forEachIndexed { i, userId ->
                            Log.d("FirebaseDiagnostic", "      User $i: $userId")
                        }
                    } else {
                        Log.e("FirebaseDiagnostic", "      ❌ Wrong array size: ${users.size} (should be 2)")
                        chatsWithIssues++
                    }
                } else {
                    Log.e("FirebaseDiagnostic", "   ❌ users: MISSING or NOT AN ARRAY")
                    Log.e("FirebaseDiagnostic", "      Found: $users (Type: ${users?.let { it::class.simpleName }})")
                    chatsWithIssues++
                }
                
                // Timestamp checks
                if (createdAt != null) {
                    Log.d("FirebaseDiagnostic", "   ✅ createdAt: $createdAt")
                } else {
                    Log.w("FirebaseDiagnostic", "   ⚠️ createdAt: MISSING")
                }
                
                if (lastMessage != null) {
                    Log.d("FirebaseDiagnostic", "   ✅ lastMessage: '$lastMessage'")
                } else {
                    Log.w("FirebaseDiagnostic", "   ⚠️ lastMessage: MISSING")
                }
                
                if (lastMessageTime != null) {
                    Log.d("FirebaseDiagnostic", "   ✅ lastMessageTime: $lastMessageTime")
                } else {
                    Log.w("FirebaseDiagnostic", "   ⚠️ lastMessageTime: MISSING")
                }
                
                // Overall chat status
                if (users is List<*> && users.size == 2) {
                    validChats++
                    Log.d("FirebaseDiagnostic", "   ✅ Chat data structure: VALID")
                } else {
                    Log.e("FirebaseDiagnostic", "   ❌ Chat data structure: INVALID")
                }
            }
            
            Log.d("FirebaseDiagnostic", "")
            Log.d("FirebaseDiagnostic", "📈 CHATS COLLECTION SUMMARY:")
            Log.d("FirebaseDiagnostic", "   Total chats: ${documents.size}")
            Log.d("FirebaseDiagnostic", "   Valid chats: $validChats")
            Log.d("FirebaseDiagnostic", "   Chats with issues: $chatsWithIssues")
            
        } catch (e: Exception) {
            Log.e("FirebaseDiagnostic", "❌ Failed to analyze chats collection", e)
        }
    }
    
    private fun provideDiagnosticSummary() {
        Log.d("FirebaseDiagnostic", "")
        Log.d("FirebaseDiagnostic", "🎯 DIAGNOSTIC SUMMARY & RECOMMENDATIONS")
        Log.d("FirebaseDiagnostic", "==========================================")
        
        Log.d("FirebaseDiagnostic", "📋 COMMON ISSUES & SOLUTIONS:")
        Log.d("FirebaseDiagnostic", "")
        Log.d("FirebaseDiagnostic", "1. 🚨 MISSING PHONE NUMBERS:")
        Log.d("FirebaseDiagnostic", "   Issue: Users don't have 'number' field")
        Log.d("FirebaseDiagnostic", "   Impact: Cannot find users by phone")
        Log.d("FirebaseDiagnostic", "   Solution: Add 'number' field to user documents")
        Log.d("FirebaseDiagnostic", "")
        Log.d("FirebaseDiagnostic", "2. ⚠️ MALFORMED CHAT DATA:")
        Log.d("FirebaseDiagnostic", "   Issue: 'users' field not an array or wrong size")
        Log.d("FirebaseDiagnostic", "   Impact: Chat queries fail")
        Log.d("FirebaseDiagnostic", "   Solution: Delete and recreate chat documents")
        Log.d("FirebaseDiagnostic", "")
        Log.d("FirebaseDiagnostic", "3. 📱 TESTING INSTRUCTIONS:")
        Log.d("FirebaseDiagnostic", "   - Use phone numbers found in diagnostic")
        Log.d("FirebaseDiagnostic", "   - Try adding chat with existing user's number")
        Log.d("FirebaseDiagnostic", "   - Monitor AddChat logs for detailed process")
        Log.d("FirebaseDiagnostic", "")
        Log.d("FirebaseDiagnostic", "==========================================")
        Log.d("FirebaseDiagnostic", "✅ DIAGNOSTIC COMPLETE")
        Log.d("FirebaseDiagnostic", "==========================================")
    }
}
