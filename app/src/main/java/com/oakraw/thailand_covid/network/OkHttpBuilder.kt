package com.nocnoc.app.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class OkHttpBuilder {
    fun build(vararg interceptors: Interceptor) = OkHttpClient.Builder().apply {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        addInterceptor(logging)
        interceptors.asList().forEach { interceptor ->
            addInterceptor(interceptor)
        }
    }.build()
}