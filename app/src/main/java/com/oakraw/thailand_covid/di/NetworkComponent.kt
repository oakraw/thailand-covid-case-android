package com.oakraw.thailand_covid.di

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.nocnoc.app.network.OkHttpBuilder
import com.nocnoc.app.network.RetrofitBuilder
import com.oakraw.thailand_covid.network.ApiService
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory


val NetworkDependency = module {
    single(named("okHttpAuth")) {
        OkHttpBuilder().build()
    }
    single<CallAdapter.Factory>(named("coroutineFactory")) { CoroutineCallAdapterFactory() }
    single<CallAdapter.Factory>(named("networkResponseFactory")) { NetworkResponseAdapterFactory() }
    single<Converter.Factory>(named("gsonFactory")) { GsonConverterFactory.create() }
    single() {
        RetrofitBuilder(
            get(named("okHttpAuth")),
            get(named("gsonFactory")),
            get(named("networkResponseFactory"))
        )
    }

    single<ApiService> { get<RetrofitBuilder>().build("https://static.easysunday.com/") }
}
