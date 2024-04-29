package io.github.jd1378.otphelper.utils

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

/**
 * @author : jixiaoyong
 * @description ：TODO
 *
 * @email : jixiaoyong1995@gmail.com
 * @date : 24/4/2024
 */
object Toaster {

  fun show(context: Context, text: Any?, duration: Int = Toast.LENGTH_SHORT) {
    val msg = text?.toString() ?: return
    Handler(Looper.getMainLooper()).post {
      Toast.makeText(context, msg, duration).show()
    }
  }

}
