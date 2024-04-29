package io.github.jd1378.otphelper.network

import io.github.jd1378.otphelper.data.SettingsRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: https://jixiaoyong.github.io
 * date: 2019-11-14
 * description: 网络请求工具类
 */
object NetUtils {

  private var api: ApiService? = null

  private const val TIME_OUT_DURATION = 20L
  private const val BASE_URL_PLACEHOLDER = "https://BASE_URL_PLACEHOLDER.COM"

  private var realBaseUrl = ""
  private var realUuid = ""

  private var settingsRepository: SettingsRepository? = null

  suspend fun init(settingsRepository: SettingsRepository) {
    NetUtils.settingsRepository = settingsRepository
    realBaseUrl = settingsRepository.getBaseUrl()
    realUuid = settingsRepository.getUuid()

    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY) // 设置日志级别

    val httpClient = OkHttpClient.Builder()
        .readTimeout(TIME_OUT_DURATION, TimeUnit.SECONDS)
        .writeTimeout(TIME_OUT_DURATION, TimeUnit.SECONDS)
        .addInterceptor {
          val request = it.request()
          it.proceed(
              it.request().newBuilder()
                  .url(request.url.toString().replace(BASE_URL_PLACEHOLDER, realBaseUrl, true))
                  .addHeader("X-UUID", getUuid())
                  .build(),
          )
        }
        .addInterceptor(logging)
        .build()

    val retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(BASE_URL_PLACEHOLDER)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    api = retrofit.create(ApiService::class.java)
  }

  fun getApiOrNull(): ApiService? {
    if (realBaseUrl.isEmpty() || realUuid.isEmpty()) {
      return null
    }
    return api
  }

  suspend fun updateBaseUrlAndUuid(baseUrl: String, uuid: String) {
    realBaseUrl = baseUrl
    realUuid = uuid

    settingsRepository?.setBaseUrl(baseUrl)
    settingsRepository?.setUuid(uuid)
  }

  private fun getUuid(): String {
    return realUuid
  }

  sealed class NetworkState {
    object Succeeded : NetworkState()
    object Normal : NetworkState()
    object Loading : NetworkState()
    object Error : NetworkState()
  }

}
