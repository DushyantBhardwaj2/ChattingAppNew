package com.example.chattingapp

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import com.example.chattingapp.data.ChatData
import com.example.chattingapp.data.Events
import com.example.chattingapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class LCViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    application: Application
) : AndroidViewModel(application) {
    var inProcess = mutableStateOf(false)
    var inProcessChats = mutableStateOf(false)
    var eventMutableState = mutableStateOf<Events<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val inProgressChatMessage = mutableStateOf(false)
    val chatMessages = mutableStateOf<List<com.example.chattingapp.data.Message>>(listOf())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = auth.currentUser
                signIn.value = user != null
                user?.uid?.let { getUserData(it) }
            } catch (e: Exception) {
                signIn.value = false
            }
        }
    }

    fun signUP(name: String, number: String, email: String, password: String) {
        inProcess.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "please fill all field")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signIn.value = true
                        createOrUpdateProfile(name, number)
                    } else {
                        handleException(task.exception, customMessage = "Sign UP failed")
                    }
                    inProcess.value = false
                }
            } catch (e: Exception) {
                handleException(e, customMessage = "Sign UP failed")
            }
        }
    }

    fun logIN(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "email and password can not be null")
            return
        }
        inProcess.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signIn.value = true
                        auth.currentUser?.uid?.let { getUserData(it) }
                    } else {
                        handleException(task.exception, customMessage = "Login failed ")
                    }
                    inProcess.value = false
                }
            } catch (e: Exception) {
                handleException(e, customMessage = "Login failed ")
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it)
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (String) -> Unit) {
        inProcess.value = true
        val userId = auth.currentUser?.uid ?: return
        val ref = storage.reference.child("profile_images/$userId.jpg")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ref.putFile(uri).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        onSuccess(downloadUri.toString())
                        inProcess.value = false
                    }
                }.addOnFailureListener {
                    handleException(it)
                    inProcess.value = false
                }
            } catch (e: Exception) {
                handleException(e)
                inProcess.value = false
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {

        Log.e("Live Chat App", "live Chat Exception: ", exception)
        exception?.printStackTrace()
        val errorMSG = exception?.localizedMessage ?: ""
        val message = if (customMessage.isBlank()) errorMSG else customMessage
        eventMutableState.value = Events(message)
        inProcess.value = false
    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        val userId = auth.currentUser?.uid ?: return
        val userMap = mutableMapOf<String, Any?>()
        name?.let { userMap["name"] = it }
        number?.let { userMap["number"] = it }
        imageUrl?.let { userMap["imageUrl"] = it }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firestore.collection("users").document(userId).set(userMap)
                    .addOnSuccessListener { getUserData(userId) }
                    .addOnFailureListener { handleException(it, "Failed to update user data") }
                inProcess.value = false
            } catch (e: Exception) {
                handleException(e, "Failed to update user data")
            }
        }
    }

    fun populateMessages(chatID: String) {
        inProgressChatMessage.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firestore.collection("chats").document(chatID).collection("messages")
                    .get()
                    .addOnSuccessListener { result ->
                        // TODO: Map result to your Message data class
                        chatMessages.value = listOf() // Replace with fetched messages
                        inProgressChatMessage.value = false
                    }
                    .addOnFailureListener { handleException(it) }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun depopulateMessage() {
        chatMessages.value = listOf()
    }

    fun populateChats() {
        inProcessChats.value = true
        val userId = auth.currentUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firestore.collection("chats")
                    .whereArrayContains("users", userId)
                    .get()
                    .addOnSuccessListener { result ->
                        // TODO: Map result to your ChatData data class
                        chats.value = listOf() // Replace with fetched chats
                        inProcessChats.value = false
                    }
                    .addOnFailureListener { handleException(it) }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        // TODO: Map document to your UserData data class
                        // userData.value = ...
                        inProcess.value = false
                        populateChats()
                    }
                    .addOnFailureListener { handleException(it, "Cannot retrieve User") }
            } catch (e: Exception) {
                handleException(e, "Cannot retrieve User")
            }
        }
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signOut()
                signIn.value = false
                userData.value = null
                depopulateMessage()
                eventMutableState.value = Events("logout")
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun onAddChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must be contain digits only")
        } else {
            val userId = auth.currentUser?.uid ?: return
            inProcessChats.value = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Find user by number
                    firestore.collection("users")
                        .whereEqualTo("number", number)
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty) {
                                val otherUserId = result.documents[0].id
                                val chatId = if (userId < otherUserId) "$userId-$otherUserId" else "$otherUserId-$userId"
                                val chatData = mapOf(
                                    "users" to listOf(userId, otherUserId),
                                    "createdAt" to System.currentTimeMillis()
                                )
                                firestore.collection("chats").document(chatId).set(chatData)
                                    .addOnSuccessListener { populateChats() }
                                    .addOnFailureListener { handleException(it, "Failed to create chat") }
                            } else {
                                handleException(customMessage = "User not found")
                            }
                            inProcessChats.value = false
                        }
                        .addOnFailureListener { handleException(it, "Failed to search user") }
                } catch (e: Exception) {
                    handleException(e)
                    inProcessChats.value = false
                }
            }
        }
    }

    fun onSendReply(chatID: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val userId = auth.currentUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msgData = mapOf(
                    "senderId" to userId,
                    "message" to message,
                    "timestamp" to System.currentTimeMillis()
                )
                firestore.collection("chats").document(chatID).collection("messages")
                    .add(msgData)
                    .addOnSuccessListener { populateMessages(chatID) }
                    .addOnFailureListener { handleException(it, "Failed to send message") }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }
}
