# Lets Connect

A real-time chat application built with Jetpack Compose and Firebase.

## Try the App

Download the APK from [Releases](https://github.com/DushyantBhardwaj2/ChattingAppNew/releases) and test with these demo accounts:

**Demo User 1:**
- Email: `demoUser1@gmail.com`
- Password: `12345678`

**Demo User 2:**
- Email: `demoUser2@gmail.com` 
- Password: `12345678`

You can create chats between these demo users to test the real-time messaging.

## Features

**Authentication**
- Email/Password login and registration
- Google Sign-In integration
- Phone number validation to prevent duplicate accounts
- Profile setup for new users

**Messaging**
- Real-time message delivery with Firestore
- Messages appear instantly across devices
- Auto-scroll to latest messages
- Message timestamps

**User Interface**
- Built with Material Design 3 and Jetpack Compose
- 15+ profile icon options
- Responsive design for different screen sizes
- Smooth animations and transitions

**User Experience**
- Customizable user profiles
- Clean navigation between screens
- Loading indicators
- Proper error handling and messages

## Technical Details
**Architecture**
- MVVM pattern for clean separation
- Repository pattern for data layer
- Hilt for dependency injection

**Built With**
- Kotlin (100% Kotlin codebase)
- Jetpack Compose for UI
- Firebase Auth for authentication
- Firestore for real-time database
- Material Design 3 components
- Navigation Component

## Installation

**Requirements**
- Android Studio (latest version recommended)
- Android SDK 26+ (Android 8.0 or higher)
- Google Services configuration

**Setup**

1. Clone the repository
   ```bash
   git clone https://github.com/DushyantBhardwaj2/ChattingAppNew.git
   cd ChattingAppNew
   ```

2. Firebase Setup
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Add Android app with package name: `com.example.chattingapp`
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication (Email/Password + Google Sign-In)
   - Create a Firestore database with these rules:

   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
       match /chats/{chatId} {
         allow read, write: if request.auth != null && 
           request.auth.uid in resource.data.user;
       }
       match /messages/{messageId} {
         allow read, write: if request.auth != null;
       }
     }
   }
   ```

3. Google Sign-In Setup
   - Go to Authentication > Sign-in method in Firebase Console
   - Enable Google Sign-in
   - Add your SHA-1 certificate fingerprint
   - Note the Web client ID

4. Build and run
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

## Project Structure

```
app/src/main/java/com/example/chattingapp/
├── Screens/           # UI screens
│   ├── LoginScreen.kt
│   ├── SignUpScreen.kt
│   ├── ChatListScreen.kt
│   └── SingleChatScreen.kt
├── data/              # Data models
│   ├── UserData.kt
│   ├── ChatData.kt
│   └── MessageData.kt
├── LCViewModel.kt     # Main ViewModel
├── GoogleSignInHelper.kt
├── MainActivity.kt
└── Utils.kt
```

## How to Use

1. Create an account with email, password, name, and phone number
2. Login with your credentials or use Google Sign-In
3. Complete your profile and choose an icon
4. Search for other users by phone number to start chatting
5. Messages sync in real-time across devices

## Contributing

Feel free to fork this project and submit pull requests. For major changes, please open an issue first.

## Developer

Built by [Dushyant Bhardwaj](https://github.com/DushyantBhardwaj2)