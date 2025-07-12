package com.example.chattingapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.chattingapp.data.CHATS
import com.example.chattingapp.data.ChatData
import com.example.chattingapp.data.Events
import com.example.chattingapp.data.MESSAGE
import com.example.chattingapp.data.Message
import com.example.chattingapp.data.USER_NODE
import com.example.chattingapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore
) : ViewModel() {
    var inProcess = mutableStateOf(false)
    var inProcessChats = mutableStateOf(false)
    var eventMutableState = mutableStateOf<Events<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val inProgressChatMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null
    var currentChatListListener: ListenerRegistration? = null
    val chatMessages = mutableStateOf<List<Message>>(listOf())

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        Log.d("LCViewModel", "Init - Current user: ${currentUser?.uid}, SignIn: ${signIn.value}")
        currentUser?.uid?.let {
            Log.d("LCViewModel", "Getting user data for UID: $it")
            getUserData(it)
        }
    }


    fun signUP(name: String, number: String, email: String, password: String) {
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "please fill all field")
            return
        }
        
        Log.d("LCViewModel", "Starting signup process for email: $email, number: $number")
        inProcess.value = true
        
        // Create Firebase auth account first, then check for duplicate numbers
        Log.d("LCViewModel", "Creating Firebase auth account")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    Log.d("LCViewModel", "Firebase auth successful, UID: ${auth.currentUser?.uid}")
                    
                    // Now that user is authenticated, check for duplicate phone numbers
                    Log.d("LCViewModel", "Checking if number $number already exists in Firestore")
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener { querySnapshot ->
                            Log.d("LCViewModel", "Successfully queried Firestore. Found ${querySnapshot.size()} documents")
                            if (querySnapshot.isEmpty) {
                                Log.d("LCViewModel", "Number not in use, creating user profile")
                                signIn.value = true
                                createOrUpdateProfile(name, number)
                            } else {
                                Log.w("LCViewModel", "User with number $number already exists, deleting auth account")
                                // Delete the created auth account since number is already taken
                                auth.currentUser?.delete()?.addOnCompleteListener {
                                    auth.signOut()
                                    inProcess.value = false
                                    handleException(customMessage = "User with this phone number already exists")
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("LCViewModel", "Error checking existing number: ${exception.message}", exception)
                            // Delete the created auth account due to error
                            auth.currentUser?.delete()?.addOnCompleteListener {
                                auth.signOut()
                                inProcess.value = false
                                val errorMessage = when {
                                    exception.message?.contains("PERMISSION_DENIED") == true -> 
                                        "Database permission error. Please try again."
                                    exception.message?.contains("UNAVAILABLE") == true -> 
                                        "Network error. Please check your internet connection."
                                    else -> "Failed to check existing users: ${exception.message}"
                                }
                                handleException(exception, errorMessage)
                            }
                        }
                } else {
                    Log.e("LCViewModel", "Firebase auth failed", authResult.exception)
                    inProcess.value = false
                    handleException(authResult.exception, customMessage = "Sign UP failed")
                }
            }
    }

    fun logIN(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "email and password can not be null")
            return
        } else {
            Log.d("LCViewModel", "Attempting login with email: $email")
            inProcess.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("LCViewModel", "Login successful, UID: ${auth.currentUser?.uid}")
                        signIn.value = true
                        inProcess.value = false
                        auth.currentUser?.uid?.let { uid ->
                            getUserData(uid)
                        }
                    } else {
                        Log.e("LCViewModel", "Login failed", it.exception)
                        handleException(exception = it.exception, customMessage = "Login failed ")
                    }
                }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("Live Chat App", "live Chat Exception: ", exception)
        exception?.printStackTrace()
        val errorMSG = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrBlank()) errorMSG else customMessage
        eventMutableState.value = Events(message)
        inProcess.value = false
        inProcessChats.value = false
    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        profileIcon: Int? = null
    ) {
        val uid = auth.currentUser?.uid
        val newUserData = UserData(
            userID = uid ?: "",
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            profileIcon = profileIcon ?: userData.value?.profileIcon ?: com.example.chattingapp.data.ProfileIcons.getDefaultIcon()
        )
        uid?.let {
            inProcess.value = true
            if (uid != null) {
                db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        val updatedData = mutableMapOf<String, Any>()
                        name?.let { updatedData["name"] = it }
                        number?.let { updatedData["number"] = it }
                        profileIcon?.let { updatedData["profileIcon"] = it }

                        db.collection(USER_NODE).document(uid).update(updatedData)
                            .addOnSuccessListener {
                                inProcess.value = false
                                getUserData(uid)
                                // Refresh chats to show updated profile info
                                populateChats()
                            }
                            .addOnFailureListener { exception ->
                                handleException(exception, "Failed to update user data")
                            }


                    } else {
                        db.collection(USER_NODE).document(uid).set(newUserData)
                            .addOnSuccessListener {
                                inProcess.value = false
                                getUserData(uid)
                                // Refresh chats to show updated profile info
                                populateChats()
                            }
                            .addOnFailureListener { exception ->
                                handleException(exception, "Failed to create user data")
                            }
                    }
                }
                    .addOnFailureListener {
                        handleException(it, "Can not retrieve Use")
                    }
            }
        }
    }

    fun populateMessages(chatID: String) {
        inProgressChatMessage.value = true
        currentChatMessageListener =
            db.collection(CHATS).document(chatID).collection(MESSAGE)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error)

                    }
                    if (value != null) {
                        chatMessages.value = value.documents.mapNotNull {
                            it.toObject<Message>()
                        }.sortedBy { it.timeStamp }
                        inProgressChatMessage.value = false
                    }
                }

    }

    fun depopulateMessage() {
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }

    fun populateChats() {
        inProcessChats.value = true
        val currentUserId = userData.value?.userID
        if (currentUserId == null) {
            Log.w("PopulateChats", "No current user data available")
            inProcessChats.value = false
            return
        }
        
        // Remove existing listener to avoid duplicates
        currentChatListListener?.remove()
        
        Log.d("PopulateChats", "Starting chat population for user: $currentUserId")
        
        // Use the correct Firestore structure with users array
        currentChatListListener = db.collection(CHATS)
            .whereArrayContains("users", currentUserId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("PopulateChats", "Error fetching chats", error)
                    handleException(error)
                    inProcessChats.value = false
                    return@addSnapshotListener
                }
                
                if (value != null) {
                    Log.d("PopulateChats", "Found ${value.documents.size} chat documents")
                    
                    val chatList = mutableListOf<ChatData>()
                    var documentsProcessed = 0
                    val totalDocuments = value.documents.size
                    
                    if (totalDocuments == 0) {
                        chats.value = listOf()
                        inProcessChats.value = false
                        return@addSnapshotListener
                    }
                    
                    value.documents.forEach { doc ->
                        try {
                            val users = doc.get("users") as? List<String> ?: listOf()
                            val otherUserId = users.firstOrNull { it != currentUserId }
                            
                            if (otherUserId != null) {
                                // Fetch fresh user data from users collection instead of using stored data
                                db.collection(USER_NODE).document(otherUserId).get()
                                    .addOnSuccessListener { userDoc ->
                                        try {
                                            val otherUser = if (userDoc.exists()) {
                                                userDoc.toObject<UserData>() ?: UserData(
                                                    userID = otherUserId,
                                                    name = "Unknown User",
                                                    number = "",
                                                    profileIcon = com.example.chattingapp.data.ProfileIcons.getDefaultIcon()
                                                )
                                            } else {
                                                UserData(
                                                    userID = otherUserId,
                                                    name = "Unknown User",
                                                    number = "",
                                                    profileIcon = com.example.chattingapp.data.ProfileIcons.getDefaultIcon()
                                                )
                                            }
                                            
                                            val chat = ChatData(
                                                chatId = doc.id,
                                                user1 = userData.value ?: UserData(userID = currentUserId),
                                                user2 = otherUser,
                                                createdAt = doc.getLong("createdAt") ?: 0L
                                            )
                                            chatList.add(chat)
                                            Log.d("PopulateChats", "Added chat with ${otherUser.name} (Chat ID: ${doc.id})")
                                            
                                            documentsProcessed++
                                            if (documentsProcessed == totalDocuments) {
                                                // All documents processed, update the chats list
                                                val sortedChats = chatList.sortedByDescending { it.createdAt }
                                                chats.value = sortedChats
                                                Log.d("PopulateChats", "Chat population completed. Total chats: ${sortedChats.size}")
                                                inProcessChats.value = false
                                            }
                                        } catch (e: Exception) {
                                            Log.e("PopulateChats", "Error processing user data for ${doc.id}", e)
                                            documentsProcessed++
                                            if (documentsProcessed == totalDocuments) {
                                                val sortedChats = chatList.sortedByDescending { it.createdAt }
                                                chats.value = sortedChats
                                                inProcessChats.value = false
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("PopulateChats", "Error fetching user data for $otherUserId", e)
                                        documentsProcessed++
                                        if (documentsProcessed == totalDocuments) {
                                            val sortedChats = chatList.sortedByDescending { it.createdAt }
                                            chats.value = sortedChats
                                            inProcessChats.value = false
                                        }
                                    }
                            } else {
                                documentsProcessed++
                                if (documentsProcessed == totalDocuments) {
                                    val sortedChats = chatList.sortedByDescending { it.createdAt }
                                    chats.value = sortedChats
                                    inProcessChats.value = false
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("PopulateChats", "Error processing chat document ${doc.id}", e)
                            documentsProcessed++
                            if (documentsProcessed == totalDocuments) {
                                val sortedChats = chatList.sortedByDescending { it.createdAt }
                                chats.value = sortedChats
                                inProcessChats.value = false
                            }
                        }
                    }
                }
            }
    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot retrieve User")
                inProcess.value = false
                return@addSnapshotListener
            }
            if (value != null && value.exists()) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProcess.value = false
                // Only populate chats if we don't already have a listener
                if (currentChatListListener == null) {
                    populateChats()
                }
            } else {
                Log.w("GetUserData", "User document does not exist")
                inProcess.value = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        depopulateMessage()
        currentChatMessageListener?.remove()
        currentChatMessageListener = null
        currentChatListListener?.remove()
        currentChatListListener = null
        chats.value = listOf()
        eventMutableState.value = Events("logout")
    }

    fun onAddChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must be contain digits only")
        } else {
            // First check if a chat already exists using the users array
            val currentUserId = userData.value?.userID
            if (currentUserId == null) {
                handleException(customMessage = "User not logged in")
                return
            }
            
            // Check if user exists first
            Log.d("LCViewModel", "Searching for user with number: $number")
            db.collection(USER_NODE).whereEqualTo("number", number).get()
                .addOnSuccessListener { userQuery ->
                    Log.d("LCViewModel", "User search completed. Found ${userQuery.size()} users")
                    if (userQuery.isEmpty) {
                        handleException(customMessage = "Number not found")
                    } else {
                        val chatPartner = userQuery.toObjects<UserData>()[0]
                        val chatPartnerId = chatPartner.userID
                        Log.d("LCViewModel", "Found chat partner: ${chatPartner.name} (ID: $chatPartnerId)")
                        
                        // Check if chat already exists using users array
                        db.collection(CHATS)
                            .whereArrayContains("users", currentUserId)
                            .get()
                            .addOnSuccessListener { existingChats ->
                                var chatExists = false
                                
                                // Check if any existing chat contains both users
                                for (doc in existingChats.documents) {
                                    val users = doc.get("users") as? List<String> ?: listOf()
                                    if (users.contains(chatPartnerId)) {
                                        chatExists = true
                                        break
                                    }
                                }
                                
                                if (chatExists) {
                                    handleException(customMessage = "Chat already exists")
                                } else {
                                    // Create new chat
                                    val id = db.collection(CHATS).document().id
                                    val currentTime = System.currentTimeMillis()
                                    
                                    val chatForFirestore = mapOf(
                                        "chatId" to id,
                                        "users" to listOf(currentUserId, chatPartnerId),
                                        "user1" to mapOf(
                                            "userID" to userData.value?.userID,
                                            "name" to userData.value?.name,
                                            "number" to userData.value?.number,
                                            "profileIcon" to (userData.value?.profileIcon ?: 0)
                                        ),
                                        "user2" to mapOf(
                                            "userID" to chatPartner.userID,
                                            "name" to chatPartner.name,
                                            "number" to chatPartner.number,
                                            "profileIcon" to chatPartner.profileIcon
                                        ),
                                        "createdAt" to currentTime
                                    )
                                    
                                    db.collection(CHATS).document(id).set(chatForFirestore)
                                        .addOnSuccessListener {
                                            Log.d("AddChat", "Chat created successfully with ID: $id")
                                        }
                                        .addOnFailureListener { exception ->
                                            handleException(exception, "Failed to create chat")
                                        }
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("LCViewModel", "Error checking existing chats: ${exception.message}", exception)
                                handleException(exception, "Failed to check existing chats")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("LCViewModel", "Error finding user by number: ${exception.message}", exception)
                    val errorMessage = when {
                        exception.message?.contains("PERMISSION_DENIED") == true -> 
                            "Permission denied. Cannot search for users."
                        exception.message?.contains("UNAVAILABLE") == true -> 
                            "Network error. Please check your connection."
                        else -> "Failed to find user: ${exception.message}"
                    }
                    handleException(exception, errorMessage)
                }
        }
    }

    fun onSendReply(chatID: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userID, message, time)
        db.collection(CHATS).document(chatID).collection(MESSAGE).document().set(msg)
    }

}
