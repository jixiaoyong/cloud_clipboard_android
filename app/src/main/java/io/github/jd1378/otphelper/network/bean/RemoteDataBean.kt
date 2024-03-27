package io.github.jixiaoyong.wanandroid.api.bean


import com.google.gson.annotations.SerializedName

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: https://jixiaoyong.github.io
 * date: 2019-11-05
 * description: 网络数据基本结构
 */
data class RemoteDataBean<T>(
  @SerializedName("data")
  var `data`: T? = null,
  @SerializedName("code")
  var code: Int = 0, // 0
  @SerializedName("message")
  var message: String = ""
) {

  fun isSuccess() = 200 == code
}
