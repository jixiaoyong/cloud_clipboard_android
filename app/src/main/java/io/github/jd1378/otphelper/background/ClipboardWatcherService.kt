package io.github.jd1378.otphelper.background

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.data.SettingsRepository
import io.github.jd1378.otphelper.network.NetUtils
import io.github.jd1378.otphelper.network.bean.CloudClipboardBean
import io.github.jd1378.otphelper.utils.Clipboard
import io.github.jd1378.otphelper.utils.Toaster
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ClipboardWatcherService : Service() {

  var readRemoteClipboardJob: Job? = null
  var lastClipboard: CloudClipboardBean? = null
  val notificationManager: NotificationManager by lazy { getSystemService(NotificationManager::class.java) }

  @Inject
  lateinit var clipboardNotification: ClipboardNotification

  @Inject
  lateinit var settingsRepository: SettingsRepository

  override fun onCreate() {
    super.onCreate()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
          ClipboardNotification.CHANNEL_ID,
          "My Foreground Service",
          NotificationManager.IMPORTANCE_DEFAULT,
      )
      notificationManager.createNotificationChannel(channel)
    }

  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val clipboardManager =
        getSystemService(Activity.CLIPBOARD_SERVICE) as? ClipboardManager
    val hasPrimaryClip = clipboardManager?.hasPrimaryClip()
    val clipData = clipboardManager?.primaryClip
    val clipboardContent = if (clipData != null && clipData.itemCount > 0) {
      clipData.getItemAt(0).text.toString()
    } else {
      ""
    }

    Log.d("TAG", "onStartCommand: $hasPrimaryClip $clipboardContent")


    readRemoteClipboardJob = GlobalScope.launch {
      // 定时任务
      while (true) {
        val delayDurationSecond = settingsRepository.getAutoSyncDuration().first()
        delay(delayDurationSecond * 1_000L)

        if (clipboardNotification.isAsyncEnabled) {
          var notification =
              clipboardNotification.createNotification(
                  this@ClipboardWatcherService,
                  isLoading = true,
              )
          notificationManager.notify(ClipboardNotification.NOTIFICATION_ID, notification)
          Log.d("TAG", "onStartCommand latest: NetUtils.getApiOrNull()?.latest() ")

          val response = NetUtils.getApiOrNull()?.latest() ?: continue
          if (response.isSuccess() && lastClipboard?.updatedTime != response.data?.updatedTime) {
            lastClipboard = response.data
            val onlineClipboard = response.data?.text

            Log.d("TAG", "onStartCommand latest: $response clipboardContent $onlineClipboard")
            Toaster.show(applicationContext, onlineClipboard)
            Clipboard.copyCodeToClipboard(applicationContext, onlineClipboard ?: "")
          }
          notification = clipboardNotification.createNotification(
              this@ClipboardWatcherService,
              response.data?.text ?: "",
          )
          notificationManager.notify(ClipboardNotification.NOTIFICATION_ID, notification)
        }
      }

    }

    val newMessageNotification = clipboardNotification.createNotification(this, clipboardContent)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      startForeground(
          ClipboardNotification.NOTIFICATION_ID,
          newMessageNotification,
          ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
      )
    } else {
      startForeground(ClipboardNotification.NOTIFICATION_ID, newMessageNotification)
    }


    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    readRemoteClipboardJob?.cancel()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }
}
