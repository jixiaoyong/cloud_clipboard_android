package io.github.jd1378.otphelper.background

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.network.NetUtils
import io.github.jd1378.otphelper.utils.Clipboard
import io.github.jd1378.otphelper.utils.Toaster
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author : jixiaoyong
 * @description ：后台监听并处理上传和下载剪贴板内容的事件
 *
 * @email : jixiaoyong1995@gmail.com
 * @date : 22/4/2024
 */
@AndroidEntryPoint
class ClipboardManagerReceiver : BroadcastReceiver() {

  @Inject
  lateinit var clipboardNotification: ClipboardNotification

  override fun onReceive(context: Context, intent: Intent) {
    val notificationManager: NotificationManager =
        context.getSystemService(NotificationManager::class.java)

    Log.d("TAG", "onReceive: action:${intent.action}")

    when (intent.action) {
      ClipboardNotification.ACTION_GET_CLIPBOARD -> {
        // 获取输入框输入的内容
        val input: Bundle? = RemoteInput.getResultsFromIntent(intent)
        if (null != input) {
          val content = input.getCharSequence("KEY_TEXT_REPLY")?.toString() ?: return
          Log.d("TAG", "onReceive: $content")
          GlobalScope.launch {
            val response = try {
              NetUtils.getApiOrNull()?.save(content) ?: return@launch
            } catch (e: Exception) {
              Log.e("TAG", "onReceive: e", e)
              return@launch
            }
            Toaster.show(context, response.message)
            if (response.isSuccess() && response.data != null) {
              val notification = clipboardNotification.createNotification(context, content)
              notificationManager.notify(ClipboardNotification.NOTIFICATION_ID, notification)
            }
          }
        }
      }

      ClipboardNotification.ACTION_COPY_CLIPBOARD -> {
        val notification =
            clipboardNotification.createNotification(context, isLoading = true)
        notificationManager.notify(ClipboardNotification.NOTIFICATION_ID, notification)

        GlobalScope.launch {
          val response = try {
            NetUtils.getApiOrNull()?.latest() ?: return@launch
          } catch (e: Exception) {
            Log.e("TAG", "onReceive: e", e)
            return@launch
          }
          val content = response.data?.text ?: ""
          if (response.isSuccess()) {
            Log.d("TAG", "onStartCommand latest: $response")
            Toaster.show(context, response.message)
            Clipboard.copyCodeToClipboard(context, response.data?.text ?: "")
            val notification = clipboardNotification.createNotification(context, content)
            notificationManager.notify(ClipboardNotification.NOTIFICATION_ID, notification)
          }
        }
      }

      ClipboardNotification.ACTION_CLIPBOARD_RESUME -> {
        val notification =
            clipboardNotification.createNotification(context, isAsyncEnabled = true)
        notificationManager.notify(ClipboardNotification.NOTIFICATION_ID, notification)
      }

      ClipboardNotification.ACTION_CLIPBOARD_PAUSE -> {
        val notification =
            clipboardNotification.createNotification(context, isAsyncEnabled = false)
        notificationManager.notify(ClipboardNotification.NOTIFICATION_ID, notification)
      }
    }

  }

}


