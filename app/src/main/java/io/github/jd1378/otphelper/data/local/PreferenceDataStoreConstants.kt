package io.github.jd1378.otphelper.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferenceDataStoreConstants {
  val IS_AUTO_COPY_ENABLED = booleanPreferencesKey("IS_AUTO_COPY_ENABLED")
  val IS_POST_NOTIF_ENABLED = booleanPreferencesKey("IS_POST_NOTIF_ENABLED")
  val IS_SETUP_FINISHED = booleanPreferencesKey("IS_SETUP_FINISHED")
  val IS_AUTO_SYNC = booleanPreferencesKey("IS_AUTO_SYNC")
  val CLIPBOARD_CONFIG = intPreferencesKey("CLIPBOARD_CONFIG")
  val IGNORED_NOTIF_SET = stringSetPreferencesKey("IGNORED_NOTIF_SET")
  val BASE_URL = stringPreferencesKey("BASE_URL")
  val UUID = stringPreferencesKey("UUID")
}
