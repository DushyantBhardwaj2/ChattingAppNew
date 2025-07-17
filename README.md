# 💬 Modern Chat Application

[![Android CI](https://github.com/DushyantBhardwaj2/ChattingAppNew/actions/workflows/android.yml/badge.svg)](https://github.com/DushyantBhardwaj2/ChattingAppNew/actions/workflows/android.yml)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![GitHub release](https://img.shields.io/github/v/release/DushyantBhardwaj2/ChattingAppNew)](https://github.com/DushyantBhardwaj2/ChattingAppNew/releases)

A feature-rich, real-time chat application built with **Jetpack Compose** and **Firebase**, showcasing modern Android development practices and clean architecture.

## 🚀 **Live Demo**
- **APK Download**: [Release v1.0](https://github.com/DushyantBhardwaj2/ChattingAppNew/releases) *(Available for testing)*
- **Screenshots**: See gallery below 📸

---

## 🎯 **Key Features**

### 🔐 **Authentication & Security**
- Firebase Authentication with email/password
- Secure user registration with phone number validation
- Automatic duplicate account prevention

### 💬 **Real-time Messaging**
- Instant message delivery using Firestore real-time listeners
- Auto-scroll to latest messages
- Empty message validation
- Message timestamps

### 🎨 **Modern UI/UX**
- **Dark/Light Theme Support** with automatic system detection
- Material Design 3 components
- Smooth animations and transitions
- Responsive layout for all screen sizes

### 👤 **Profile Management**
- Customizable profile icons (15+ options)
- Real-time profile synchronization across chats
- Profile picture updates reflected instantly

### 🧭 **Navigation**
- Proper back stack management
- Bottom navigation with intuitive flow
- Clean navigation between auth and main screens

---

## 🛠️ **Technical Architecture**

### **Architecture Pattern**
- **MVVM (Model-View-ViewModel)** with Clean Architecture principles
- **Repository Pattern** for data management
- **Unidirectional Data Flow**

### **Tech Stack**
```kotlin
// UI Framework
Jetpack Compose 2024.12.01
Material Design 3

// Dependency Injection
Hilt 2.55

// Backend Services
Firebase Auth 23.1.0
Cloud Firestore 25.1.1
Firebase Analytics 22.0.0

// Navigation
Navigation Compose 2.8.5

// Build System
Kotlin 2.1.0
Gradle 8.9
```

### **Key Libraries**
- **State Management**: Compose State & ViewModel
- **Async Operations**: Kotlin Coroutines
- **Image Loading**: Coil 3.0
- **Logging**: Android Log with structured debugging

---

## 📱 **Screenshots**

| Authentication | Chat List | Dark Mode Chat | Profile Settings |
|----------------|-----------|----------------|------------------|
| ![Auth](screenshots/auth.png) | ![Chat List](screenshots/chat_list.png) | ![Dark Chat](screenshots/dark_chat.png) | ![Profile](screenshots/profile.png) |

*Screenshots showcase both light and dark themes*

---

## 🏗️ **Project Structure**

```
app/src/main/java/com/example/chattingapp/
├── 📁 Screens/              # UI Composables
│   ├── 🔐 LoginScreen.kt
│   ├── 🔐 SignUpScreen.kt
│   ├── 💬 ChatListScreen.kt
│   ├── 💬 SingleChatScreen.kt
│   ├── 👤 Profile.kt
│   └── 🧭 BottomNavigationMenu.kt
├── 📁 data/                 # Data Models & Constants
│   ├── 👤 UserData.kt
│   ├── 💬 ChatData.kt
│   ├── 💬 Message.kt
│   └── 🎨 ProfileIcons.kt
├── 📁 ui/theme/             # Theme System
│   ├── 🎨 Color.kt          # Light/Dark colors
│   ├── 🎨 Theme.kt          # Material 3 theme
│   └── 🎨 ChatTheme.kt      # Chat-specific theming
├── 🧠 LCViewModel.kt        # Business Logic
├── 🔧 HiltModule.kt         # Dependency Injection
├── 🛠️ Util.kt              # Utility Functions
└── 📱 MainActivity.kt       # App Entry Point
```

---

## ⚡ **Performance Optimizations**

### **Real-time Efficiency**
- **Smart Listeners**: Automatic cleanup to prevent memory leaks
- **Selective Updates**: Only refresh changed user profiles
- **Optimized Queries**: Efficient Firestore queries with proper indexing

### **UI Performance**
- **Lazy Loading**: Chat lists with efficient scrolling
- **State Management**: Minimal recompositions
- **Theme Caching**: Fast theme switching

---

## 🚀 **Getting Started**

### **Prerequisites**
- Android Studio Hedgehog | 2023.1.1+
- JDK 21
- Android SDK 35
- Firebase project setup

### **Installation**
```bash
# 1. Clone the repository
git clone https://github.com/DushyantBhardwaj2/ChattingAppNew.git

# 2. Open in Android Studio
cd ChattingAppNew

# 3. Add your google-services.json
# Place your Firebase config file in app/

# 4. Build and run
./gradlew assembleDebug
```

### **Firebase Setup**
1. Create a new Firebase project
2. Enable Authentication (Email/Password)
3. Enable Cloud Firestore
4. Download `google-services.json`
5. Place in `app/` directory

---

## 🧪 **Testing**

### **Manual Testing Checklist**
- ✅ User registration and login
- ✅ Real-time message sending/receiving  
- ✅ Profile updates and synchronization
- ✅ Theme switching (Light/Dark)
- ✅ Navigation and back stack
- ✅ Empty message validation

### **Test Coverage Areas**
- Authentication flows
- Real-time data synchronization
- UI state management
- Theme consistency
- Error handling

---

## 🔮 **Future Enhancements**

### **Planned Features**
- 📷 **Image/File Sharing**: Send photos and documents
- 🔔 **Push Notifications**: Firebase Cloud Messaging
- 📱 **Online Status**: Real-time user presence
- 🔍 **Message Search**: Find conversations quickly
- 🔐 **End-to-End Encryption**: Enhanced security
- 📊 **Message Analytics**: Read receipts and delivery status

### **Technical Improvements**
- Unit & Integration testing with JUnit5
- CI/CD pipeline with GitHub Actions
- Code coverage reporting
- Performance monitoring with Firebase Performance

---

## 🐛 **Known Issues & Solutions**

| Issue | Status | Solution |
|-------|--------|----------|
| Large chat history loading | 🔄 Planned | Implement pagination |
| Offline message handling | 🔄 Planned | Add local caching |
| Network error recovery | ✅ Handled | Comprehensive error handling |

---

## 📈 **Project Metrics**

- **Lines of Code**: ~2,000 (Kotlin)
- **Build Time**: < 30 seconds
- **APK Size**: 12.8 MB (Release)
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 15 (API 35)

---

## 🤝 **Contributing**

Contributions are welcome! Please feel free to submit a Pull Request.

### **Development Guidelines**
1. Follow [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
2. Maintain MVVM architecture pattern
3. Add appropriate comments and documentation
4. Test changes thoroughly

---

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 **Developer**

**Dushyant Bhardwaj**
- 📧 Email: [your.email@example.com](mailto:your.email@example.com)
- 💼 LinkedIn: [linkedin.com/in/dushyantbhardwaj](https://linkedin.com/in/dushyantbhardwaj)
- 🐙 GitHub: [@DushyantBhardwaj2](https://github.com/DushyantBhardwaj2)

---

## ⭐ **Show Your Support**

Give a ⭐️ if this project helped you learn Android development!

---

**Built with ❤️ using Modern Android Development practices**