package io.github.jd1378.otphelper

import android.app.Application
import android.content.Intent
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import io.github.jd1378.otphelper.background.ClipboardWatcherService

@HiltAndroidApp
class App : Application() {

  override fun onCreate() {
    super.onCreate()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(Intent(this, ClipboardWatcherService::class.java))
    } else {
      startService(Intent(this, ClipboardWatcherService::class.java))
    }
  }

}
