package io.github.jd1378.otphelper.ui.screens.clipboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClipboardConfigViewModel
@Inject
constructor(
  private val settingsRepository: SettingsRepository
) : ViewModel() {
  private val _uiState: MutableStateFlow<ClipboardConfigUiState> =
      MutableStateFlow(ClipboardConfigUiState())

  val uiState = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      val state = settingsRepository.getClipboardConfigSync()
      _uiState.emit(ClipboardConfigUiState.fromInt(state))
    }
  }


  fun updateClipboardConfig(
    isUploadClipboard: Boolean? = null,
    isDownloadClipboard: Boolean? = null
  ) {
    viewModelScope.launch {
      val oldState = _uiState.value
      val newState = oldState.copy(
          isUploadClipboard = isUploadClipboard ?: oldState.isUploadClipboard,
          isDownloadClipboard = isDownloadClipboard ?: oldState.isDownloadClipboard,
      )
      _uiState.emit(newState)
      settingsRepository.setClipboardConfigSync(newState.toInt())
    }
  }
}

data class ClipboardConfigUiState(
  val isUploadClipboard: Boolean = false,
  val isDownloadClipboard: Boolean = false
) {

  fun toInt(): Int {
    val binaryUpload = isUploadClipboard.toBinaryInt() * 0b10
    val binaryDownload = isDownloadClipboard.toBinaryInt()
    return binaryDownload + binaryUpload
  }

  companion object {
    fun fromInt(value: Int): ClipboardConfigUiState {
      val isUploadClipboard = (value and 0b10) == 0b10
      val isDownloadClipboard = (value and 0b1) == 0b1
      return ClipboardConfigUiState(isUploadClipboard, isDownloadClipboard)
    }
  }
}

fun Boolean.toBinaryInt(): Int = if (this) 0x1 else 0x0

