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

            val notification = NotificationCompat.Builder(context, "daily_special_channel")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle("Daily Special Available!")
                .setContentText("Check out today's special in the Canteen Menu app!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            with(NotificationManagerCompat.from(context)) {
                notify(1, notification)
            }
        }

        if (enabled) {
            scheduleDailyNotification(context)
        }
    }
}