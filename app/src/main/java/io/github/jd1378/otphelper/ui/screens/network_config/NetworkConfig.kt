package io.github.jd1378.otphelper.ui.screens.network_config

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
 * @description ：设置网络信息
 *
 * @email : jixiaoyong1995@gmail.com
 * @date : 27/3/2024
 */
fun NavGraphBuilder.addNetworkConfigGraph(upPress: () -> Unit) {
  composable(
      MainDestinations.NETWORK_CONFIG,
  ) {
    val viewModel = hiltViewModel<NetworkConfigViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    NetworkConfig(upPress, uiState, viewModel::onSaveUrlAndUuid, viewModel::onSaveAutoSync)
  }
}

@Composable
fun NetworkConfig(
  upPress: () -> Unit,
  uiState: NetworkConfigUiState,
  onSaveUrlAndUuid: ((Context, String, String) -> Unit)? = null,
  onSaveIsAutoSync: ((Boolean) -> Unit)? = null
) {
  val context = LocalContext.current

  var baseUrl by remember { mutableStateOf(uiState.baseUrl) }
  var uuid by remember { mutableStateOf(uiState.uuid) }

  LaunchedEffect(uiState) {
    // 优先使用本地缓存的值,点击保存按钮之后才将最新值保存到缓存中
    baseUrl = uiState.baseUrl
    uuid = uiState.uuid
  }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TitleBar(
            upPress = upPress,
            text = stringResource(R.string.network_config),
        )
      },
  ) { padding ->
    Column(
            Modifier
                    .padding(padding)
                    .padding(horizontal = 15.dp)
                    .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

      TextField(
          value = baseUrl,
          modifier = Modifier.fillMaxWidth(),
          onValueChange = { baseUrl = it },
          label = { Text(stringResource(R.string.net_info_input_server_url)) },
      )
      TextField(
          value = uuid,
          modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 10.dp),
          onValueChange = { uuid = it },
          label = { Text(stringResource(R.string.net_info_input_uuid)) },
      )

      Button(
          onClick = {
            onSaveUrlAndUuid?.invoke(context, baseUrl, uuid)
          },
          enabled = baseUrl.isNotBlank() && uuid.isNotBlank(),
          modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 10.dp),
      ) {
        Text(stringResource(R.string.save))
      }
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.net_info_sync_statue) +
                if (uiState.isAutoSync) stringResource(R.string.net_info_sync_open)
                else stringResource(R.string.net_info_sync_off),
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = uiState.isAutoSync,
            onCheckedChange = {
              onSaveIsAutoSync?.invoke(it)
            },
        )
      }
    }
  }
}


@Preview
@Composable
private fun PervNetworkConfig() {
  NetworkConfig({}, NetworkConfigUiState(isAutoSync = true))
}
