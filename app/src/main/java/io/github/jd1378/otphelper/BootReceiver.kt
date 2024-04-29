package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import io.github.jd1378.otphelper.background.ClipboardWatcherService

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    if (context.checkSelfPermission(
                android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
        ) ==
      PackageManager.PERMISSION_GRANTED) {
      context.startService(Intent(context, NotificationListener::class.java))
    }
    context.startService(Intent(context, ClipboardWatcherService::class.java))
  }
}
