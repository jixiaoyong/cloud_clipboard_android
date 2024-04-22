package io.github.jd1378.otphelper.data

import io.github.jd1378.otphelper.data.local.PreferenceDataStoreConstants
import io.github.jd1378.otphelper.data.local.PreferenceDataStoreHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository
@Inject
constructor(private val preferenceDataStoreHelper: PreferenceDataStoreHelper) {

  fun getIsAutoCopyEnabledStream(): Flow<Boolean> {
    return preferenceDataStoreHelper.getPreference(
        PreferenceDataStoreConstants.IS_AUTO_COPY_ENABLED, false,
    )
  }

  fun getIsPostNotifEnabledStream(): Flow<Boolean> {
    return preferenceDataStoreHelper.getPreference(
        PreferenceDataStoreConstants.IS_POST_NOTIF_ENABLED, true,
    )
  }

  fun getIsSetupFinishedStream(): Flow<Boolean> {
    return preferenceDataStoreHelper.getPreference(
        PreferenceDataStoreConstants.IS_SETUP_FINISHED, false,
    )
  }

  suspend fun getIsAutoCopyEnabled(): Boolean {
    return preferenceDataStoreHelper.getFirstPreference(
        PreferenceDataStoreConstants.IS_AUTO_COPY_ENABLED, false,
    )
  }

  suspend fun getIsPostNotifEnabled(): Boolean {
    return preferenceDataStoreHelper.getFirstPreference(
        PreferenceDataStoreConstants.IS_POST_NOTIF_ENABLED, true,
    )
  }

  fun getIsAutoSyncStream(): Flow<Boolean> {
    return preferenceDataStoreHelper.getPreference(
        PreferenceDataStoreConstants.IS_AUTO_SYNC, true,
    )
  }

  suspend fun getIsAutoSync(): Boolean {
    return preferenceDataStoreHelper.getFirstPreference(
        PreferenceDataStoreConstants.IS_AUTO_SYNC, true,
    )
  }

  suspend fun getClipboardConfigSync(): Int {
    return preferenceDataStoreHelper.getFirstPreference(
        PreferenceDataStoreConstants.CLIPBOARD_CONFIG, 0,
    )
  }

  fun getBaseUrlStream(): Flow<String> {
    return preferenceDataStoreHelper.getPreference(
        PreferenceDataStoreConstants.BASE_URL, "",
    )
  }

  fun getUuidStream(): Flow<String> {
    return preferenceDataStoreHelper.getPreference(
        PreferenceDataStoreConstants.UUID, "",
    )
  }

  suspend fun getBaseUrl(): String {
    return preferenceDataStoreHelper.getFirstPreference(
        PreferenceDataStoreConstants.BASE_URL, "",
    )
  }

  suspend fun getUuid(): String {
    return preferenceDataStoreHelper.getFirstPreference(
        PreferenceDataStoreConstants.UUID, "",
    )
  }

  suspend fun setIsAutoSync(newValue: Boolean) {
    preferenceDataStoreHelper.putPreference(
        PreferenceDataStoreConstants.IS_AUTO_SYNC, newValue,
    )
  }

  suspend fun setIsAutoCopyEnabled(newValue: Boolean) {
    preferenceDataStoreHelper.putPreference(
        PreferenceDataStoreConstants.IS_AUTO_COPY_ENABLED, newValue,
    )
  }

  suspend fun setIsPostNotifEnabled(newValue: Boolean) {
    preferenceDataStoreHelper.putPreference(
        PreferenceDataStoreConstants.IS_POST_NOTIF_ENABLED, newValue,
    )
  }

  suspend fun setIsSetupFinished(newValue: Boolean) {
    preferenceDataStoreHelper.putPreference(
        PreferenceDataStoreConstants.IS_SETUP_FINISHED, newValue,
    )
  }

  suspend fun setBaseUrl(newValue: String) {
    preferenceDataStoreHelper.putPreference(
        PreferenceDataStoreConstants.BASE_URL, newValue,
    )
  }

  suspend fun setUuid(newValue: String) {
    preferenceDataStoreHelper.putPreference(
        PreferenceDataStoreConstants.UUID, newValue,
    )
  }

  suspend fun setClipboardConfigSync(newValue: Int) {
    return preferenceDataStoreHelper.putPreference(
        PreferenceDataStoreConstants.CLIPBOARD_CONFIG, newValue,
    )
  }
}
