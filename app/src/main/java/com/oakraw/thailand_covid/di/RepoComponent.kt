package com.oakraw.thailand_covid.di

import com.oakraw.thailand_covid.repository.ApiRepository
import org.koin.dsl.module

val RepoDependency = module {
    single { ApiRepository(get()) }
}
