package uk.ac.tees.mad.canteenmenu.screens

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import uk.ac.tees.mad.canteenmenu.CanteenViewModel
import uk.ac.tees.mad.canteenmenu.ui.theme.BackgroundLight
import uk.ac.tees.mad.canteenmenu.ui.theme.CardBackground
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangeDark
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangePrimary
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangeSecondary
import uk.ac.tees.mad.canteenmenu.ui.theme.TextPrimary
import uk.ac.tees.mad.canteenmenu.ui.theme.TextSecondary
import uk.ac.tees.mad.canteenmenu.utils.NotificationReceiver
import java.util.Calendar

private const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"

@Composable
fun Home(viewModel: CanteenViewModel, navController: NavHostController) {
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
            // Proceed to check alarm permission and schedule
        } else {
            prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, false).apply()
            notificationsEnabled = false
        }
    }

    val alarmManager = remember { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    fun checkAndSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showNotificationPermissionDialog = true
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                showAlarmPermissionDialog = true
                return
            }
        }

        scheduleDailyNotification(context)
    }

    LaunchedEffect(notificationsEnabled) {
        if (notificationsEnabled) {
            checkAndSchedule()
        }
    }

    val menuItems = viewModel.menuItems.collectAsState().value
    val dailySpecial = viewModel.dailySpecial.collectAsState().value
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(notificationsEnabled, onNotificationsToggle = {
            val newEnabled = !notificationsEnabled
            prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, newEnabled).apply()
            notificationsEnabled = newEnabled
            if (newEnabled) {
                checkAndSchedule()
            } else {
                cancelDailyNotification(context)
            }
        }) },
        bottomBar = { BottomNavigationBar() },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            dailySpecial?.let { special ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .height(180.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = special.imageUrl,
                            contentDescription = special.name,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Top Picks for You",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = OrangeDark
                                )
                            )
                            Text(
                                text = special.name,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "$${special.price}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(menuItems) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "$${item.price}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OrangeDark
                                    )
                                )
                            }
                        }
                    }
                }
            }
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
                    // Try scheduling after returning, though it may require app restart
                    checkAndSchedule()
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showAlarmPermissionDialog = false
                    prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, false).apply()
                    notificationsEnabled = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TopBar(notificationsEnabled: Boolean, onNotificationsToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Canteen Menu",
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onNotificationsToggle,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(34.dp)
        ) {
            Icon(
                Icons.Rounded.Notifications,
                contentDescription = "Toggle Notifications",
                tint = if (notificationsEnabled) OrangePrimary else TextSecondary
            )
        }
        Icon(
            Icons.Rounded.Wallet,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(34.dp)
        )
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = OrangePrimary) {
        NavigationBarItem(
            selected = true,
            onClick = { /* Home */ },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Orders */ },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Orders") },
            label = { Text("Orders") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Profile */ },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}

fun scheduleDailyNotification(context: Context) {
    val prefs = context.getSharedPreferences("canteen_prefs", Context.MODE_PRIVATE)
    if (!prefs.getBoolean(NOTIFICATIONS_ENABLED_KEY, false)) return

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 16)
        set(Calendar.MINUTE, 22)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    } else {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}

fun cancelDailyNotification(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}