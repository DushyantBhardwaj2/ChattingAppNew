# 🏗️ Architecture Documentation

## Overview

This chat application follows **Clean Architecture** principles with **MVVM pattern**, ensuring maintainability, testability, and scalability.

## 📐 Architecture Layers

```
┌─────────────────────────────────────────┐
│                 UI Layer                │
│            (Jetpack Compose)            │
│  ┌─────────────────────────────────────┐ │
│  │        Composable Screens           │ │
│  │  • LoginScreen                      │ │
│  │  • ChatListScreen                   │ │
│  │  • SingleChatScreen                 │ │
│  │  • Profile                          │ │
│  └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────┐
│             ViewModel Layer             │
│         (Business Logic)                │
│  ┌─────────────────────────────────────┐ │
│  │           LCViewModel               │ │
│  │  • Authentication logic             │ │
│  │  • Chat management                  │ │
│  │  • Real-time data handling          │ │
│  │  • State management                 │ │
│  └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────┐
│             Data Layer                  │
│            (Repository)                 │
│  ┌─────────────────────────────────────┐ │
│  │         Firebase Services           │ │
│  │  • FirebaseAuth                     │ │
│  │  • Cloud Firestore                  │ │
│  │  • Real-time listeners              │ │
│  └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

## 🎯 Design Patterns

### MVVM (Model-View-ViewModel)

#### **View (Composables)**
- **Responsibility**: UI rendering and user interactions
- **Components**: Screen composables, custom UI components
- **Communication**: Observes ViewModel state, sends user actions

```kotlin
@Composable
fun ChatListScreen(navController: NavController, vm: LCViewModel) {
    val chats = vm.chats.value
    val inProgress = vm.inProcessChats.value
    
    if (inProgress) {
        commonProgressBar()
    } else {
        LazyColumn {
            items(chats) { chat ->
                // Render chat items
            }
        }
    }
}
```

#### **ViewModel**
- **Responsibility**: Business logic, state management, data transformation
- **Lifecycle**: Survives configuration changes
- **Communication**: Exposes observable state, handles user actions

```kotlin
@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore
) : ViewModel() {
    var chats = mutableStateOf<List<ChatData>>(listOf())
    var userData = mutableStateOf<UserData?>(null)
    
    fun populateChats() {
        // Business logic for fetching chats
    }
}
```

#### **Model (Data Layer)**
- **Responsibility**: Data management, API calls, caching
- **Components**: Data classes, Firebase services, repositories

```kotlin
data class ChatData(
    val chatId: String? = null,
    val user1: UserData = UserData(),
    val user2: UserData = UserData(),
    val createdAt: Long = 0L
)
```

### Repository Pattern

```kotlin
// Future implementation for better separation
interface ChatRepository {
    suspend fun getChats(userId: String): List<ChatData>
    suspend fun sendMessage(chatId: String, message: Message)
    fun observeMessages(chatId: String): Flow<List<Message>>
}
```

## 🔧 Dependency Injection (Hilt)

### Module Configuration

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object HiltModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
```

### Benefits
- **Testability**: Easy to mock dependencies
- **Scalability**: Easy to add new dependencies
- **Maintainability**: Centralized dependency management

## 🗂️ Package Structure

```
com.example.chattingapp/
├── 📁 Screens/                    # UI Layer
│   ├── LoginScreen.kt
│   ├── SignUpScreen.kt
│   ├── ChatListScreen.kt
│   ├── SingleChatScreen.kt
│   ├── Profile.kt
│   └── BottomNavigationMenu.kt
├── 📁 data/                       # Data Models
│   ├── UserData.kt
│   ├── ChatData.kt
│   ├── Message.kt
│   ├── Events.kt
│   ├── Constants.kt
│   └── ProfileIcons.kt
├── 📁 ui/theme/                   # Theme System
│   ├── Color.kt
│   ├── Theme.kt
│   ├── ChatTheme.kt
│   └── Type.kt
├── LCViewModel.kt                 # Business Logic
├── HiltModule.kt                  # Dependency Injection
├── LCApplication.kt               # Application Class
├── MainActivity.kt                # Main Activity
└── Util.kt                       # Utility Functions
```

## 🔄 Data Flow

### Authentication Flow
```
User Action → ViewModel → Firebase Auth → UI State Update
```

### Real-time Messaging Flow
```
Firestore Listener → ViewModel → Compose State → UI Recomposition
```

### Navigation Flow
```
User Action → Navigation Controller → Screen Transition
```

## 🎨 State Management

### Compose State
- **MutableState**: For UI state that triggers recomposition
- **RememberSaveable**: For state that survives configuration changes
- **LaunchedEffect**: For side effects and coroutines

```kotlin
@Composable
fun SingleChatScreen() {
    var reply by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    LaunchedEffect(chatMessage.value.size) {
        if (chatMessage.value.isNotEmpty()) {
            listState.animateScrollToItem(chatMessage.value.size - 1)
        }
    }
}
```

## 🔐 Security Architecture

### Authentication
- Firebase Authentication with email/password
- Secure token management
- Automatic session handling

### Data Security
- Firestore security rules (to be implemented)
- Input validation and sanitization
- Secure data transmission

## 🚀 Performance Optimizations

### Real-time Updates
- **Smart Listeners**: Automatic cleanup to prevent memory leaks
- **Selective Updates**: Only refresh changed data
- **Efficient Queries**: Optimized Firestore queries

### UI Performance
- **Lazy Loading**: Efficient list rendering
- **State Hoisting**: Minimal recompositions
- **Theme Caching**: Fast theme switching

## 🔮 Future Architecture Improvements

### Repository Layer
```kotlin
interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<UserData>
    suspend fun signUp(userData: UserData): Result<UserData>
}

interface ChatRepository {
    fun observeChats(userId: String): Flow<List<ChatData>>
    suspend fun sendMessage(chatId: String, message: Message): Result<Unit>
}
```

### Use Cases (Clean Architecture)
```kotlin
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, message: String): Result<Unit> {
        return chatRepository.sendMessage(chatId, Message(message = message))
    }
}
```

### Local Database (Room)
```kotlin
@Entity
data class CachedMessage(
    @PrimaryKey val id: String,
    val chatId: String,
    val sendBy: String,
    val message: String,
    val timeStamp: String,
    val synced: Boolean = false
)
```

## 📊 Architecture Benefits

### Current Implementation
- ✅ **Separation of Concerns**: Clear layer separation
- ✅ **Testability**: ViewModels can be unit tested
- ✅ **Maintainability**: Easy to modify and extend
- ✅ **Scalability**: Can handle feature additions
- ✅ **Reactive UI**: Real-time updates with Compose

### Future Improvements
- 🔄 **Repository Pattern**: Better data layer abstraction
- 🔄 **Use Cases**: Business logic encapsulation
- 🔄 **Local Caching**: Offline support with Room
- 🔄 **Error Handling**: Comprehensive error management
- 🔄 **Testing**: Unit and integration tests

## 🛠️ Development Guidelines

### Adding New Features
1. **Data Model**: Create/update data classes
2. **Repository**: Add data access methods
3. **ViewModel**: Implement business logic
4. **UI**: Create/update Composables
5. **Navigation**: Update navigation graph
6. **Testing**: Add unit tests

### Code Organization
- Group related functionality in packages
- Keep Composables focused and reusable
- Extract common logic to utility functions
- Follow naming conventions consistently

---

This architecture ensures the app is **maintainable**, **testable**, and **scalable** while following Android development best practices.
