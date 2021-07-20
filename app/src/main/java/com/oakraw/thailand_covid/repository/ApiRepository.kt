package com.oakraw.thailand_covid.repository

import com.oakraw.thailand_covid.network.ApiService

class ApiRepository(private val service: ApiService) {
    suspend fun getTodayCaseInfo() = service.getTodayCaseInfo()
}