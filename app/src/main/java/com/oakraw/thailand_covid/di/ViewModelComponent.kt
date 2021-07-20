package com.oakraw.thailand_covid.di

import com.oakraw.thailand_covid.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelDependency = module {
    viewModel { MainViewModel(get()) }
}