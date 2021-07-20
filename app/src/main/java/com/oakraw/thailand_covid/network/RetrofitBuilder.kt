package com.nocnoc.app.network

import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit

class RetrofitBuilder(
    val okHttpClient: OkHttpClient,
    val converterFactory: Converter.Factory? = null,
    val adapterFactory: CallAdapter.Factory? = null
) {

    inline fun <reified T> build(baseUrl: String): T {
        val builder = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)

        converterFactory?.let { builder.addConverterFactory(converterFactory) }
        adapterFactory?.let { builder.addCallAdapterFactory(adapterFactory) }

        return builder.build()
            .create(T::class.java)
    }
}