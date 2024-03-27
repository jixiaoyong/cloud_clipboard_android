package io.github.jd1378.otphelper.ui.screens.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.data.SettingsRepository
import io.github.jd1378.otphelper.network.NetUtils
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
  val isAutoCopyEnabled: Boolean = false,
  val isPostNotifEnabled: Boolean = true,
  val isSetupFinished: Boolean = true,
)

@HiltViewModel
class HomeViewModel
@Inject
constructor(
  private val savedStateHandle: SavedStateHandle,
  private val settingsRepository: SettingsRepository
) : ViewModel() {

  val uiState: StateFlow<HomeUiState> =
      combine(
              settingsRepository.getIsAutoCopyEnabledStream(),
              settingsRepository.getIsPostNotifEnabledStream(),
              settingsRepository.getIsSetupFinishedStream(),
      ) { isAutoCopyEnabled,
          isPostNotifEnabled,
          isSetupFinished ->
        HomeUiState(isAutoCopyEnabled, isPostNotifEnabled, isSetupFinished)
      }
          .stateIn(
                  scope = viewModelScope,
                  started = SharingStarted.WhileSubscribed(5_000),
                  initialValue = HomeUiState(),
          )

  init {
    viewModelScope.launch {
      NetUtils.init(settingsRepository)
    }
  }

  fun onAutoCopyToggle() {
    val newValue = !uiState.value.isAutoCopyEnabled
    viewModelScope.launch {
      settingsRepository.setIsAutoCopyEnabled(newValue)
      if (!newValue && !uiState.value.isPostNotifEnabled) {
        settingsRepository.setIsPostNotifEnabled(true)
      }
    }
  }

  fun onPostNotifToggle() {
    val currentValue = uiState.value.isPostNotifEnabled
    viewModelScope.launch {
      if (currentValue) {
        settingsRepository.setIsAutoCopyEnabled(true)
      }
      settingsRepository.setIsPostNotifEnabled(!currentValue)
    }
  }

  fun onSendTestNotifPressed(context: Context) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
      PackageManager.PERMISSION_GRANTED) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val name = context.getString(R.string.code_detected_channel_name)
        val descriptionText = context.getString(R.string.code_detected_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel("test_chan", name, importance).apply {
              description = descriptionText
            }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
      }

      var notification =
          NotificationCompat.Builder(context, "test_chan")
              .setSmallIcon(R.drawable.ic_launcher_foreground)
              .setContentTitle(context.getString(R.string.test_notification_title))
              .setContentText(context.getString(R.string.test_notification_content))
              .build()

      NotificationManagerCompat.from(context).notify(10, notification)
    }
  }
}
