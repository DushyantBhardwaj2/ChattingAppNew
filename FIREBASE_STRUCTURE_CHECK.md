# Firebase Data Structure Check Guide

## 🔍 How to Check Your Firebase Data Structure

### Step 1: Open Firebase Console
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click on **Firestore Database** in the left sidebar

### Step 2: Check Users Collection

Click on the **users** collection and verify each user document has:

#### ✅ Required Fields for Each User:
```
Document ID: [auto-generated-user-id]
Fields:
  ├── userID: string (should match document ID)
  ├── name: string (user's display name)
  ├── number: string (phone number - CRUCIAL for search)
  └── profileIcon: number (icon resource ID)
```

#### ❌ Common Issues in Users:
- Missing `number` field
- `number` stored as number instead of string
- `userID` doesn't match document ID
- Missing `profileIcon` field

### Step 3: Check Chats Collection

Click on the **chats** collection and verify each chat document has:

#### ✅ Required Fields for Each Chat:
```
Document ID: [userId1-userId2] (sorted alphabetically)
Fields:
  ├── users: array [userId1, userId2]
  ├── createdAt: timestamp
  ├── lastMessage: string
  └── lastMessageTime: timestamp
```

#### ❌ Common Issues in Chats:
- `users` field is not an array
- `users` array doesn't have exactly 2 items
- Missing timestamp fields
- Malformed chat ID format

### Step 4: Check Authentication
1. Go to **Authentication** in Firebase Console
2. Click on **Users** tab
3. Verify your test users are listed there

---

## 🧪 Test the Diagnostic Tool

After building and running the app, check the Android Studio Logcat for:

```
Filter: FirebaseDiagnostic
```

### Expected Log Output:
```
=== STARTING FIREBASE DATA STRUCTURE DIAGNOSTIC ===
--- Checking Users Collection ---
Total users found: X
User 1:
  Document ID: abc123...
  userID: abc123... (or ❌ MISSING)
  name: John Doe (or ❌ MISSING)
  number: 1234567890 (or ❌ MISSING)
  profileIcon: 12345 (or ❌ MISSING)
  ✅ User data structure is correct

--- Checking Chats Collection ---
Total chats found: X
Chat 1:
  Document ID: abc123-def456
  users: [abc123, def456] (or ❌ MISSING)
  createdAt: 1672531200000 (or ❌ MISSING)
  ✅ Chat data structure is correct

--- Checking Current User ---
Current authenticated user ID: abc123...
✅ Current user found in Firestore
=== DIAGNOSTIC COMPLETE ===
```

---

## 🔧 What to Do Based on Results

### If Users Collection Has Issues:
1. **Missing `number` field**: You'll need to add this manually for each user
2. **Wrong data types**: Delete and recreate problematic user documents
3. **No users found**: Complete signup process for test users

### If Chats Collection Has Issues:
1. **Wrong structure**: Delete all chat documents and start fresh
2. **Malformed arrays**: Manually fix the `users` field format
3. **No chats found**: This is normal for a fresh start

### If Current User Issues:
1. **Not authenticated**: Login to the app first
2. **User not in Firestore**: Complete the signup process

---

## 🚀 Quick Fix Commands

### Delete All Chats (Recommended):
1. Go to Firestore Database
2. Click on `chats` collection
3. Select all documents
4. Click Delete

### Fix User Data (If needed):
For each user document in the `users` collection:
1. Click on the document
2. Ensure these fields exist with correct types:
   - `userID`: string
   - `name`: string  
   - `number`: string (not number!)
   - `profileIcon`: number

---

## 📱 Testing Steps After Fix

1. **Build and run the app**
2. **Login with existing user**
3. **Check diagnostic logs**
4. **Try adding a chat with another user's phone number**
5. **Monitor the enhanced AddChat logs**

The diagnostic tool will run automatically when you start the app and show you exactly what's in your Firebase data!
