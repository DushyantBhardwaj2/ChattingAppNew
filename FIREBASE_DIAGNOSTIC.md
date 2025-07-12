// Firebase Console Data Check Script
// Run this in Firebase Console > Firestore > Rules tab temporarily

// Add this as a temporary rule to export data:
service cloud.firestore {
  match /databases/{database}/documents {
    // Temporary rule for debugging - REMOVE after testing
    match /{document=**} {
      allow read, write: if true;
    }
  }
}

/* 
DIAGNOSTIC CHECKLIST:

1. Check Users Collection:
   - Go to Firestore Database > users collection
   - Verify each user document has these fields:
     ✓ userID (string)
     ✓ name (string) 
     ✓ number (string) ← MOST IMPORTANT for phone search
     ✓ profileIcon (number)

2. Check Chats Collection:
   - Go to Firestore Database > chats collection  
   - Verify each chat document has:
     ✓ users (array of 2 user IDs)
     ✓ createdAt (timestamp)
     ✓ lastMessage (string)
     ✓ lastMessageTime (timestamp)

3. Common Issues:
   ❌ Users missing "number" field
   ❌ Number stored as number instead of string
   ❌ Chat "users" field not an array
   ❌ Duplicate chat documents

4. Test Users:
   - Note down phone numbers of 2 test users
   - Ensure both have completed signup process
   - Try creating chat between them
*/
