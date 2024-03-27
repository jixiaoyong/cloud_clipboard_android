package io.github.jd1378.otphelper.network

import io.github.jd1378.otphelper.network.bean.CloudClipboardBean
import io.github.jixiaoyong.wanandroid.api.bean.*
import retrofit2.http.*

/**
 * Created by jixiaoyong on 2018/3/2.
 */
interface ApiService {

  /**
   * 获取最新值
   * 方法：GET
   * 参数：无
   */
  @GET("latest")
  suspend fun latest(): RemoteDataBean<CloudClipboardBean>


  /**
   * 保存最新值
   * body： 要保存的文本内容
   */
  @Headers("Content-Type:text/plain;charset=utf-8")
  @POST("submit")
  suspend fun save(@Body text: String): RemoteDataBean<CloudClipboardBean>
}
