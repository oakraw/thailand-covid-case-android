package com.oakraw.thailand_covid.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oakraw.thailand_covid.repository.ApiRepository
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.oakraw.thailand_covid.model.CaseInfo
import kotlinx.coroutines.launch

class MainViewModel(val apiRepository: ApiRepository): ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val todayCaseInfo = MutableLiveData<CaseInfo>()

    fun fetch() {
        viewModelScope.launch {
            isLoading.value = true
            val response = apiRepository.getTodayCaseInfo()
            when (response) {
                is NetworkResponse.Success -> {
                    todayCaseInfo.postValue(response.body)
                }
            }
            isLoading.value = false
        }
    }
}