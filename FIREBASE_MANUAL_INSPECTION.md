# Firebase Console Manual Inspection Guide

## 🔍 How to Check Your Firebase Data Structure

### **Step 1: Access Firebase Console**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click on **"Firestore Database"** in the left sidebar
4. Make sure you're in the **"Data"** tab

### **Step 2: Check Users Collection**

#### **Expected Structure:**
```
📁 users/
  📄 [userId1]/
    userID: "string_value"
    name: "string_value"
    number: "string_value"  ← CRITICAL for phone search
    profileIcon: number_value
  📄 [userId2]/
    userID: "string_value"
    name: "string_value"
    number: "string_value"
    profileIcon: number_value
```

#### **What to Check:**
1. **Collection exists**: Look for a collection named `users`
2. **Document count**: How many user documents are there?
3. **Required fields**: Each user document should have:
   - ✅ `userID` (string)
   - ✅ `name` (string)
   - ✅ `number` (string) ← **MOST IMPORTANT**
   - ✅ `profileIcon` (number)

#### **Common Issues:**
- ❌ Missing `number` field
- ❌ `number` stored as number instead of string
- ❌ Empty or null values
- ❌ Inconsistent field names

### **Step 3: Check Chats Collection**

#### **Expected Structure:**
```
📁 chats/
  📄 [chatId]/
    users: ["userId1", "userId2"]  ← Array of exactly 2 user IDs
    createdAt: timestamp_value
    lastMessage: "string_value"
    lastMessageTime: timestamp_value
```

#### **What to Check:**
1. **Collection exists**: Look for a collection named `chats`
2. **Document count**: How many chat documents are there?
3. **Required fields**: Each chat document should have:
   - ✅ `users` (array with exactly 2 strings)
   - ✅ `createdAt` (timestamp)
   - ✅ `lastMessage` (string)
   - ✅ `lastMessageTime` (timestamp)

#### **Common Issues:**
- ❌ `users` field not an array
- ❌ `users` array with wrong number of elements
- ❌ Missing timestamp fields

## 📝 Data Collection Checklist

### **Users Collection:**
- [ ] Collection exists
- [ ] Has documents (count: ___)
- [ ] All users have `userID` field
- [ ] All users have `name` field
- [ ] All users have `number` field (**CRITICAL**)
- [ ] All users have `profileIcon` field
- [ ] Phone numbers are stored as strings (not numbers)

### **Chats Collection:**
- [ ] Collection exists
- [ ] Has documents (count: ___)
- [ ] All chats have `users` array field
- [ ] All `users` arrays have exactly 2 elements
- [ ] All chats have `createdAt` timestamp
- [ ] All chats have `lastMessage` string
- [ ] All chats have `lastMessageTime` timestamp

## 🔧 Quick Fixes

### **If Users Collection Issues:**

#### **Missing `number` field:**
1. Click on the user document
2. Click "Add field"
3. Field name: `number`
4. Field type: `string`
5. Value: The user's phone number

#### **Wrong data type for `number`:**
1. Click on the user document
2. Click on the `number` field
3. Change type to `string`
4. Save

### **If Chats Collection Issues:**

#### **Delete all chats to start fresh:**
1. Click on the `chats` collection
2. Select all documents
3. Click "Delete"
4. Confirm deletion

## 📱 Testing Phone Numbers

### **Test Users Setup:**
Create at least 2 test users with these fields:
```
User 1:
- name: "Test User 1"
- number: "1234567890"
- profileIcon: 2131165354

User 2:  
- name: "Test User 2"
- number: "0987654321"
- profileIcon: 2131165355
```

## 🚨 Red Flags

### **Critical Issues That Break Chat Creation:**
1. ❌ **No `number` field** - App cannot find users by phone
2. ❌ **`number` as numeric type** - String comparison fails
3. ❌ **Empty `users` collection** - No one to chat with
4. ❌ **Malformed `users` array in chats** - Query fails

### **Less Critical Issues:**
1. ⚠️ Missing `profileIcon` - App uses default
2. ⚠️ Empty chats collection - Normal for new users
3. ⚠️ Missing `lastMessage` - App handles gracefully

## 🔄 After Manual Check

1. **Run the app** with your device connected
2. **Monitor logs** using Android Studio Logcat
3. **Filter by tags**: `FirebaseDiagnostic`, `AddChat`, `PopulateChats`
4. **Compare** manual findings with diagnostic logs

The diagnostic tool will automatically check your data structure when the app starts and provide detailed logs about any issues found.
