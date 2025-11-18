package uk.ac.tees.mad.canteenmenu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.canteenmenu.screens.Splash
import uk.ac.tees.mad.canteenmenu.screens.navigation
import uk.ac.tees.mad.canteenmenu.ui.theme.CanteenMenuTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        setContent {
            CanteenMenuTheme {
                navigation()
            }
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "daily_special_channel",
                "Daily Specials",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for daily specials"
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

