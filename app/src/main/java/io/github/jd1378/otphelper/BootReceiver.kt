package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import io.github.jd1378.otphelper.utils.NotificationHelper
import android.content.pm.PackageManager

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    if (context.checkSelfPermission(
          android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
      ) ==
      PackageManager.PERMISSION_GRANTED) {
      context.startService(Intent(context, NotificationListener::class.java))
      if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
        if (NotificationManagerCompat.getEnabledListenerPackages(context)
              .contains(context.packageName)) {
          context.startService(Intent(context, NotificationListener::class.java))
        }
        when (Build.VERSION.SDK_INT) {
          Build.VERSION_CODES.Q,
          Build.VERSION_CODES.R -> {
            NotificationHelper.sendPermissionRevokedNotif(context)
          }
        }
      }
    }
  }
}
