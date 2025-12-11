package uk.ac.tees.mad.canteenmenu.screens

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import uk.ac.tees.mad.canteenmenu.CanteenViewModel
import uk.ac.tees.mad.canteenmenu.ui.theme.BackgroundLight
import uk.ac.tees.mad.canteenmenu.ui.theme.CardBackground
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangeDark
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangePrimary
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangeSecondary
import uk.ac.tees.mad.canteenmenu.ui.theme.TextPrimary
import uk.ac.tees.mad.canteenmenu.ui.theme.TextSecondary

private const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"

@Composable
fun Profile(
    viewModel : CanteenViewModel,
    navController: NavController
) {
    val userData = viewModel.userData.collectAsState().value
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("canteen_prefs", Context.MODE_PRIVATE) }
    var notificationsEnabled by remember { mutableStateOf(prefs.getBoolean(NOTIFICATIONS_ENABLED_KEY, false)) }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }
    var showAlarmPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, true).apply()
            notificationsEnabled = true
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                showAlarmPermissionDialog = true
            } else {
                scheduleDailyNotification(context)
            }
        } else {
            prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, false).apply()
            notificationsEnabled = false
            cancelDailyNotification(context)
        }
    }

    Scaffold(
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundLight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    Icons.Rounded.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            navController.popBackStack()
                        })
            }
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(OrangeSecondary),
                contentAlignment = Alignment.Center
            ) {
                if (!userData?.profilePhoto.isNullOrEmpty()){
                    AsyncImage(
                        model = userData.profilePhoto,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Icon",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userData?.name?:"John Doe",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = userData?.email?:"john.doe@example.com",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileOptionItem("Edit Profile", Icons.Default.Edit, {
                navController.navigate(Routes.EDIT_PROFILE)
            })
            ProfileOptionItem("Order History", Icons.Default.ShoppingCart, {navController.navigate(Routes.ORDER_HISTORY){
                popUpTo(0)
            } })
            ProfileOptionItem("Wallet", Icons.Rounded.Wallet, {
                navController.navigate(Routes.WALLET)
            })
            NotificationOptionItem(
                title = "Notifications",
                icon = Icons.Rounded.Notifications,
                isEnabled = notificationsEnabled,
                onToggle = {
                    val newEnabled = !notificationsEnabled
                    notificationsEnabled = newEnabled
                    prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, newEnabled).apply()
                    if (newEnabled) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                showNotificationPermissionDialog = true
                                return@NotificationOptionItem
                            }
                        }
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                            showAlarmPermissionDialog = true
                            return@NotificationOptionItem
                        }
                        scheduleDailyNotification(context)
                    } else {
                        cancelDailyNotification(context)
                    }
                }
            )
            ProfileOptionItem("Logout", Icons.Default.ExitToApp, {
                viewModel.logOut()
                navController.navigate(Routes.AUTHENTICATION){
                    popUpTo(0)
                }
            }, isLogout = true)
            Spacer(modifier = Modifier.height(16.dp))
            Text("The app will show notifications three times in a day at 9:00 AM, 5:00 PM, and 8:00 PM.",
                style = MaterialTheme.typography.bodySmall.copy( color = TextSecondary), modifier = Modifier.padding( 16.dp) )
        }
    }

    if (showNotificationPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationPermissionDialog = false },
            title = { Text("Notification Permission") },
            text = { Text("This app needs permission to send notifications to remind you of daily specials.") },
            confirmButton = {
                Button(onClick = {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    showNotificationPermissionDialog = false
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showNotificationPermissionDialog = false
                    prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, false).apply()
                    notificationsEnabled = false
                    cancelDailyNotification(context)
                }) {
                    Text("Deny")
                }
            }
        )
    }

    if (showAlarmPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showAlarmPermissionDialog = false },
            title = { Text("Exact Alarm Permission") },
            text = { Text("To send notifications at exact times, please enable exact alarms in app settings.") },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                    showAlarmPermissionDialog = false
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showAlarmPermissionDialog = false
                    prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, false).apply()
                    notificationsEnabled = false
                    cancelDailyNotification(context)
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileOptionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isLogout: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLogout) OrangeDark else CardBackground
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isLogout) Color.White else OrangePrimary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isLogout) Color.White else TextPrimary,
                    fontWeight = if (isLogout) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun NotificationOptionItem(
    title: String,
    icon: ImageVector,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isEnabled) OrangePrimary else TextSecondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OrangePrimary,
                    checkedTrackColor = OrangeSecondary
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "CanteenMenu – Profile Screen")
@Composable
fun ProfileScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color(0xFFFF6D00),
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Profile Picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE0B2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "John Doe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "john.doe@example.com",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(32.dp))

        // Profile Options
        ProfileOptionItem("Edit Profile", Icons.Default.Edit, {})
        ProfileOptionItem("Order History", Icons.Default.ShoppingCart, {})
        ProfileOptionItem("Wallet", Icons.Rounded.Wallet, {})

        // Notification Toggle
        var notificationsEnabled by remember { mutableStateOf(true) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = if (notificationsEnabled) Color(0xFFFF6D00) else Color.Gray
                )
                Spacer(Modifier.width(16.dp))
                Text("Notifications", fontSize = 18.sp, color = Color.Black)
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFF6D00),
                        checkedTrackColor = Color(0xFFFFCC80)
                    )
                )
            }
        }

        // Logout (red)
        ProfileOptionItem("Logout", Icons.AutoMirrored.Filled.ExitToApp, {}, isLogout = true)

        Spacer(Modifier.height(16.dp))
        Text(
            text = "The app will show notifications three times a day at 9:00 AM, 5:00 PM, and 8:00 PM.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}
