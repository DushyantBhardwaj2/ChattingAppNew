package com.example.chattingapp.data

data class UserData(
    var userID: String?="",
    var name: String?="",
    var number: String?="",
    var imageUrl: String?=""
){
    fun toMap()= mapOf(
        "userId" to userID,
        "name" to name,
        "number" to number,
        "imageUrl" to imageUrl
    )
}
data class ChatData(
    val chatId : String?="",
     val user1:ChatUser= ChatUser()
    ,val user2:ChatUser=ChatUser()
)


data class ChatUser(
    val userID : String?="",
    val name:String?="",
    val imageUrl: String?="",
    val number: String?=""
)
data class  Message(
    val sendBy:String?="",
    val message: String?="",
    val timeStamp:String?=""
)