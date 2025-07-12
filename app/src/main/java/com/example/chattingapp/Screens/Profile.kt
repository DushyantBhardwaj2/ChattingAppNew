package com.example.chattingapp.Screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chattingapp.CommonImage
import com.example.chattingapp.DestinationScreen
import com.example.chattingapp.LCViewModel
import com.example.chattingapp.commonDivider
import com.example.chattingapp.ui.theme.ChatTheme
import com.example.chattingapp.commonProgressBar
import com.example.chattingapp.navigateTO
import com.example.chattingapp.navigateToAuthScreen

@Composable
fun Profile(
    navController: NavController,
    vm: LCViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val inProgress = vm.inProcess.value
    if (inProgress) {
        commonProgressBar()
    } else {
        val userData = vm.userData.value
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var number by rememberSaveable { mutableStateOf(userData?.number ?: "") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Back", Modifier.clickable {
                    navigateTO(navController, route = DestinationScreen.ChatList.route)
                }, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(text = "Save", Modifier.clickable {
                    vm.createOrUpdateProfile(name = name, number = number)
                    // Update the local userData state after saving
                    vm.userData.value = vm.userData.value?.copy(name = name, number = number)
                }, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            commonDivider()
            ProfileImage(profileIcon = userData?.profileIcon ?: 0, vm = vm, name = name)
            commonDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "NAME", modifier = Modifier.width(100.dp))
                TextField(value = name, onValueChange = { name = it })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "NUMBER", modifier = Modifier.width(100.dp))
                TextField(value = number, onValueChange = { number = it })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Dark Mode")
                androidx.compose.material3.Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onThemeChange(it) }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Logout",
                    Modifier.clickable {
                        vm.logout()
                        navigateToAuthScreen(navController = navController, route = DestinationScreen.Login.route)
                    },
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.PROFILE,
            navController = navController
        )
    }
}

@Composable
fun ProfileImage(profileIcon: Int, vm: LCViewModel, name: String) {
    var showIconPicker by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clickable { showIconPicker = true },
        contentAlignment = Alignment.Center
    ) {
        if (profileIcon == 0) {
            // Show first letter of name as fallback
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.firstOrNull()?.toString()?.uppercase() ?: "?",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Image(
                painter = painterResource(id = profileIcon),
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
        }
        
        if (showIconPicker) {
            ProfileIconPicker(
                onIconSelected = { selectedIcon ->
                    vm.createOrUpdateProfile(profileIcon = selectedIcon)
                    vm.userData.value = vm.userData.value?.copy(profileIcon = selectedIcon)
                    showIconPicker = false
                },
                onDismiss = { showIconPicker = false }
            )
        }
        
        if (vm.inProcess.value) {
            commonProgressBar()
        }
    }
}

@Composable
fun ProfileIconPicker(
    onIconSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val icons = listOf(
        com.example.chattingapp.data.ProfileIcons.getIconByIndex(0),
        com.example.chattingapp.data.ProfileIcons.getIconByIndex(1),
        com.example.chattingapp.data.ProfileIcons.getIconByIndex(2),
        com.example.chattingapp.data.ProfileIcons.getIconByIndex(3),
        com.example.chattingapp.data.ProfileIcons.getIconByIndex(4)
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Profile Icon") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(icons) { icon ->
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "Profile Icon Option",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.3f))
                            .clickable { onIconSelected(icon) }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}