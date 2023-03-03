package com.github.freeman.bootcamp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpProvider {

    // Timeout for the network requests
    private const val REQUEST_TIMEOUT = 3L

    private var okHttpClient: OkHttpClient? = null

    fun getOkHttpClient(): OkHttpClient {
        return if (okHttpClient == null) {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
            this.okHttpClient = okHttpClient
            okHttpClient
        } else {
            okHttpClient!!
        }
    }

    fun clearOkHttpClient() {
        okHttpClient = null
    }
}