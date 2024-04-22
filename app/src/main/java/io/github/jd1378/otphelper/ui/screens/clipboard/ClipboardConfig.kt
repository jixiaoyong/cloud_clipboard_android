package io.github.jd1378.otphelper.ui.screens.clipboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

/**
 * @author : jixiaoyong
 * @description ：配置同步剪贴板内容
 *
 * @email : jixiaoyong1995@gmail.com
 * @date : 22/4/2024
 */
fun NavGraphBuilder.addClipboardConfigGraph(upPress: () -> Unit) {
  composable(MainDestinations.CLIPBOARD_CONFIG) {
    val viewModel = hiltViewModel<ClipboardConfigViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    ClipboardConfig(upPress, uiState, viewModel::updateClipboardConfig)
  }
}

@Composable
fun ClipboardConfig(
  upPress: () -> Unit,
  uiState: ClipboardConfigUiState,
  onSaveClipBoardConfig: ((isUploadClipboard: Boolean?, isDownloadClipboard: Boolean?) -> Unit)? = null
) {

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TitleBar(
            upPress = upPress,
            text = stringResource(R.string.clipboard_config),
        )
      },
  ) { padding ->

    Column(
        modifier = Modifier
                .padding(padding)
                .padding(horizontal = 15.dp),
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 15.dp),
      ) {
        Text(
            stringResource(R.string.clipboard_auto_upload),
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = uiState.isUploadClipboard,
            onCheckedChange = {
              onSaveClipBoardConfig?.invoke(it, null)
            },
        )
      }

      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 15.dp),
      ) {
        Text(
            stringResource(R.string.clipboard_auto_download),
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = uiState.isDownloadClipboard,
            onCheckedChange = {
              onSaveClipBoardConfig?.invoke(null, it)
            },
        )
      }
    }

  }
}

@Preview
@Composable
private fun Preview() {
  val uiState = ClipboardConfigUiState()
  ClipboardConfig(upPress = { /*TODO*/ }, uiState = uiState)
}
