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
import io.github.jd1378.otphelper.ui.screens.home.HomeUiState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : jixiaoyong
 * @description ：TODO
 *
 * @email : jixiaoyong1995@gmail.com
 * @date : 27/3/2024
 */
data class NetworkConfigUiState(
  val isAutoSync: Boolean = false,
  val baseUrl: String = "",
  val uuid: String = "",
)

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
      ) { isAutoSync,
          baseUrl,
          uuid ->
        NetworkConfigUiState(isAutoSync, baseUrl, uuid)
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
}

