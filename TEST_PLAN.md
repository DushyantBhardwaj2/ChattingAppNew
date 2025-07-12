# Quick Test Plan for Chat App

## Pre-Test Setup
1. Make sure Firebase project is properly configured
2. Ensure Firestore security rules allow authenticated users to read/write
3. Build and install the app on your device/emulator

## Test Scenario 1: New User Registration
**Steps:**
1. Open the app
2. You should see the SignUp screen
3. Enter test data:
   - Name: "Test User 1"
   - Phone: "1234567890"
   - Email: "testuser1@example.com"
   - Password: "password123"
4. Tap "Sign UP"

**Expected Result:**
- Loading indicator appears
- App navigates to ChatList screen
- Shows "No Chats Available" message
- Bottom navigation shows ChatList and Profile tabs

## Test Scenario 2: Profile Management
**Steps:**
1. From ChatList, tap the Profile tab in bottom navigation
2. Profile screen should load with user data
3. Tap on the profile icon to change it
4. Select a different icon
5. Update name or phone number if desired
6. Navigate back to ChatList

**Expected Result:**
- Profile loads with correct user data
- Icon picker works
- Changes are saved and persist
- Navigation between screens works

## Test Scenario 3: Chat Creation
**Steps:**
1. Create a second test user (repeat Scenario 1 with different details):
   - Name: "Test User 2"
   - Phone: "0987654321"
   - Email: "testuser2@example.com"
   - Password: "password123"
2. Login as first user
3. In ChatList, tap the + (FAB) button
4. Enter the second user's phone number: "0987654321"
5. Tap "ADD CHAT"

**Expected Result:**
- Dialog appears for adding chat
- After entering valid phone number, chat is created
- New chat appears in the chat list
- Can tap on chat to enter messaging screen

## Test Scenario 4: Real-time Updates
**Steps:**
1. Have two devices/emulators with different users logged in
2. Create a chat between them
3. Send messages from one device
4. Check if messages appear on the other device in real-time

**Expected Result:**
- Messages appear instantly on both devices
- Chat list updates with new messages
- Real-time synchronization works

## Debugging Tips

### If chat list doesn't load:
1. Check logs for "PopulateChats" messages
2. Verify user is authenticated (check "LCViewModel" logs)
3. Check Firestore rules and data structure

### If profile screen doesn't respond:
1. Check if user data exists in Firestore
2. Verify profile update operations in logs
3. Check for permission errors

### If authentication fails:
1. Verify Firebase configuration (google-services.json)
2. Check internet connectivity
3. Verify Firebase Authentication is enabled in console

### Log Commands:
```bash
# View all app logs
adb logcat | grep "com.example.chattingapp"

# View specific component logs
adb logcat | grep -E "(LCViewModel|PopulateChats|Firebase)"

# Clear logs and start fresh
adb logcat -c
```

## Success Criteria
✅ **Authentication**: Users can register and login
✅ **Navigation**: All screens load and bottom navigation works  
✅ **Chat List**: Shows existing chats or "No Chats Available"
✅ **Profile**: Displays user data and allows updates
✅ **Chat Creation**: Users can add new chats with valid phone numbers
✅ **Real-time**: Changes sync across devices/sessions

## If All Tests Pass:
🎉 **Your chat app is working correctly!**

## If Tests Fail:
1. Check the detailed logs as mentioned above
2. Review the `RUNTIME_FIXES_SUMMARY.md` file for technical details
3. Verify Firebase console settings
4. Ensure all code changes from the fixes were applied correctly

---

**Note**: Make sure to test with real phone numbers or consistent test numbers, as the app uses phone numbers to identify and match users for chat creation.
