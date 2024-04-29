package io.github.jd1378.otphelper.ui.screens.network_config

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.data.SettingsRepository
import io.github.jd1378.otphelper.network.NetUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : jixiaoyong
 * @description ：设置网络同步参数
 *
 * @email : jixiaoyong1995@gmail.com
 * @date : 27/3/2024
 */
data class NetworkConfigUiState(
  val isAutoSync: Boolean = false,
  val baseUrl: String = "",
  val uuid: String = "",
  val asyncDuration: Int = defaultAsyncDuration
) {

  companion object {
    const val defaultAsyncDuration = 10
    val asyncDurationSeconds = arrayOf(5, defaultAsyncDuration, 15, 30, 60, 120, 300, 600)
  }
}

@HiltViewModel
class NetworkConfigViewModel
@Inject
constructor(
  private val savedStateHandle: SavedStateHandle,
  private val settingsRepository: SettingsRepository
) : ViewModel() {


  val uiState: StateFlow<NetworkConfigUiState> =
      combine(
          settingsRepository.getIsAutoSyncStream(),
          settingsRepository.getBaseUrlStream(),
          settingsRepository.getUuidStream(),
          settingsRepository.getAutoSyncDuration(),
      ) { isAutoSync, baseUrl, uuid, duration ->
        NetworkConfigUiState(isAutoSync, baseUrl, uuid, duration)
      }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5_000),
              initialValue = NetworkConfigUiState(),
          )


  fun onSaveUrlAndUuid(context: Context, baseUrl: String, uuid: String) {
    viewModelScope.launch {
      NetUtils.updateBaseUrlAndUuid(baseUrl, uuid)
      Toast.makeText(context, R.string.finish, Toast.LENGTH_LONG).show()
    }
  }

  fun onSaveAutoSync(isAutoSync: Boolean) {
    viewModelScope.launch {
      settingsRepository.setIsAutoSync(isAutoSync)
    }
  }

  fun onSaveAsyncDuration(seconds: Int) {
    viewModelScope.launch {
      settingsRepository.setAutoSyncDuration(seconds)
    }
  }
}

fun Int.secondToFriendlyString(): String {
  val second = this
  val secondStr = if (second % 60 > 0) "${second % 60}秒" else ""
  return when {
    second < 60 -> "${second}秒"
    second < 3600 -> "${second / 60}分钟$secondStr"
    else -> {
      val minuteStr = if (second / 60 > 0) "${second / 60}分钟" else ""
      "${second / 3600}小时$minuteStr$secondStr"
    }
  }
}
