package uk.ac.tees.mad.canteenmenu.utils


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import uk.ac.tees.mad.canteenmenu.screens.scheduleDailyNotification

private const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("canteen_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(NOTIFICATIONS_ENABLED_KEY, false)

        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }

            val hour = intent.getIntExtra("notification_hour", 9)
            val (title, message) = when (hour) {
                9 -> Pair(
                    "Greab Special Deal!",
                    "Start your day with our delicious breakfast specials!"
                )
                17 -> Pair(
                    "Are you hungry?",
                    "Grab a tasty deal from the Canteen Menu!"
                )
                else -> Pair(
                    "Eat and Repeat🔁!",
                    "Don't miss out the snacks and specials!"
                )
            }

            val notification = NotificationCompat.Builder(context, "daily_special_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            with(NotificationManagerCompat.from(context)) {
                notify(hour, notification)
            }

            scheduleDailyNotification(context)
        }
    }
}