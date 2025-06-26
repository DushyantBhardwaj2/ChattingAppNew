package com.example.chattingapp

import android.net.Uri
import android.os.Message
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.chattingapp.data.CHATS
import com.example.chattingapp.data.ChatData
import com.example.chattingapp.data.ChatUser
import com.example.chattingapp.data.Events
import com.example.chattingapp.data.MESSAGE
import com.example.chattingapp.data.USER_NODE
import com.example.chattingapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {
    var inProcess = mutableStateOf(false)
    var inProcessChats = mutableStateOf(false)
    var eventMutableState = mutableStateOf<Events<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val inProgressChatMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null
    val chatMessages = mutableStateOf<List<com.example.chattingapp.data.Message>>(listOf())

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }


    fun signUP(name: String, number: String, email: String, password: String) {
        inProcess.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "please fill all field")
            return
        }
        inProcess.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("TAG", "signUP: User Logged IN")
                            signIn.value = true
                            createOrUpdateProfile(name, number)
                        } else {
                            handleException(it.exception, customMessage = "Sign UP failed")
                        }
                    }
            } else {
                handleException(customMessage = "User with Number already exist")
                inProcess.value = false

            }
        }

    }

    fun logIN(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "email and password can not be null")
            return
        } else {
            inProcess.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        signIn.value = true
                        inProcess.value = false
                        auth.currentUser?.uid?.let {
                            getUserData(it)
                        }
                    } else {
                        handleException(exception = it.exception, customMessage = "Login failed ")
                    }
                }
        }

    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProcess.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("image/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
            inProcess.value = false

        }
            .addOnFailureListener {
                handleException(it)
            }
    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {

        Log.e("Live Chat App", "live Chat Exception: ", exception)
        exception?.printStackTrace()
        val errorMSG = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrBlank()) errorMSG else customMessage
        eventMutableState.value = Events(message)
        inProcess.value = false
    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        var uid = auth.currentUser?.uid
        var userData = UserData(
            userID = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )
        uid.let {
            inProcess.value = true
            if (uid != null) {
                db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        val updatedData = mutableMapOf<String, Any>()
                        name?.let { updatedData["name"] = it }
                        number?.let { updatedData["number"] = it }
                        imageUrl?.let { updatedData["imageUrl"] = it }

                        db.collection(USER_NODE).document(uid).update(updatedData)
                            .addOnSuccessListener {
                                inProcess.value = false
                                getUserData(uid)
                            }
                            .addOnFailureListener { exception ->
                                handleException(exception, "Failed to update user data")
                            }


                    } else {
                        db.collection(USER_NODE).document(uid).set(userData)
                        inProcess.value = false
                        getUserData(uid)
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
                            it.toObject<com.example.chattingapp.data.Message>()
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
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo(
                    "user1.userID", userData.value?.userID
                ),
                Filter.equalTo(
                    "user2.userID", userData.value?.userID
                )
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)

            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProcessChats.value = false
            }
        }

    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot retrieve User")
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProcess.value = false
                populateChats()
            }
        }
    }

    fun logout() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        depopulateMessage()
        currentChatMessageListener=null
        eventMutableState.value = Events("logout")

    }

    fun onAddChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must be contain digits only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "Number not found")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userID,
                                        userData.value?.number,
                                        userData.value?.imageUrl,
                                        userData.value?.number
                                    ), ChatUser(
                                        chatPartner.userID,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.number
                                    )
                                )
                                db.collection(CHATS).document(id).set(chat)
                            }
                        }
                        .addOnFailureListener { handleException(it) }
                } else {
                    handleException(customMessage = "chats already exists")
                }
            }

        }
    }

    fun onSendReply(chatID: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = com.example.chattingapp.data.Message(userData.value?.userID, message, time)
        db.collection(CHATS).document(chatID).collection(MESSAGE).document().set(msg)
    }

}
