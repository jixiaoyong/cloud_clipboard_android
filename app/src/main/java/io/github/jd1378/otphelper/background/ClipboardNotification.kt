package io.github.jd1378.otphelper.background

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import io.github.jd1378.otphelper.BuildConfig
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.data.SettingsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jixiaoyong
 * @description ：创建自定义的通知
 *
 * @email : jixiaoyong1995@gmail.com
 * @date : 29/4/2024
 */
@Singleton
class ClipboardNotification
@Inject constructor(private val settingsRepository: SettingsRepository) {

  companion object {
    const val NOTIFICATION_ID = 0x001
    const val CHANNEL_ID = "MyForegroundServiceChannel"
    const val KEY_TEXT_REPLY = "KEY_TEXT_REPLY"
    const val ACTION_GET_CLIPBOARD = "${BuildConfig.APPLICATION_ID}.ACTION_GET_CLIPBOARD"
    const val ACTION_COPY_CLIPBOARD = "${BuildConfig.APPLICATION_ID}.ACTION_COPY_CLIPBOARD"
    const val ACTION_CLIPBOARD_PAUSE = "${BuildConfig.APPLICATION_ID}.ACTION_CLIPBOARD_PAUSE"
    const val ACTION_CLIPBOARD_RESUME = "${BuildConfig.APPLICATION_ID}.ACTION_CLIPBOARD_RESUME"
  }

  private var currentClipboard = ""
  var isAsyncEnabled = true
    private set(value) {
      GlobalScope.launch {
        settingsRepository.setIsAutoSync(field)
      }
      field = value
    }

  init {
    GlobalScope.launch {
      isAsyncEnabled = settingsRepository.getIsAutoSyncStream().first()
    }
  }

  fun createNotification(
    context: Context,
    clipboard: String? = null,
    isLoading: Boolean = false,
    isAsyncEnabled: Boolean? = null,
  ): Notification {

    currentClipboard = clipboard ?: currentClipboard
    this.isAsyncEnabled = isAsyncEnabled ?: this.isAsyncEnabled

    val intent = Intent(context, ClipboardManagerReceiver::class.java)

    val replyLabel: String = "回复"
    val remoteInput: androidx.core.app.RemoteInput =
        androidx.core.app.RemoteInput.Builder(KEY_TEXT_REPLY).run {
          setLabel(replyLabel)
          build()
        }
    val replyPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(
            context,
            0,
            intent.apply { action = ACTION_GET_CLIPBOARD },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )

    val replyPendingIntentCopy: PendingIntent =
        PendingIntent.getBroadcast(
            context,
            0,
            intent.apply { action = ACTION_COPY_CLIPBOARD },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )

    val remoteView = RemoteViews(context.packageName, R.layout.notification_clipboard_controller)
    remoteView.setOnClickPendingIntent(R.id.fetchClipboard, replyPendingIntentCopy)
    remoteView.setOnClickPendingIntent(
        R.id.pauseBtn,
        PendingIntent.getBroadcast(
            context,
            0,
            intent.apply { action = ACTION_CLIPBOARD_PAUSE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        ),
    )
    remoteView.setOnClickPendingIntent(
        R.id.resumeBtn,
        PendingIntent.getBroadcast(
            context,
            0,
            intent.apply { action = ACTION_CLIPBOARD_RESUME },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        ),
    )
    remoteView.setCharSequence(R.id.lastClipboard, "setText", "当前云剪贴板内容:$currentClipboard")
    remoteView.setViewVisibility(R.id.progress_circular, if (isLoading) View.VISIBLE else View.GONE)
    remoteView.setTextColor(
        R.id.fetchClipboard,
        (if (isLoading) 0xff3C3C3C else 0xff2576F7).toInt(),
    )
    remoteView.setViewVisibility(
        R.id.pauseBtn,
        if (this.isAsyncEnabled) View.VISIBLE else View.GONE,
    )
    remoteView.setViewVisibility(
        R.id.resumeBtn,
        if (!this.isAsyncEnabled) View.VISIBLE else View.GONE,
    )

    val action1: NotificationCompat.Action =
        NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_foreground,
            "发送新内容到剪贴板",
            replyPendingIntent,
        )
            .addRemoteInput(remoteInput)
            .build()

    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("发送内容到云端服务器")
        .setContentText("点击展开通知以复制/上传剪贴板内容")
        .setCustomBigContentView(remoteView)
        .addAction(action1)
        .setOngoing(true)
        .setAutoCancel(false)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .build()
  }

}
