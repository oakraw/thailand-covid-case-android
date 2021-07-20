package com.oakraw.thailand_covid.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.oakraw.thailand_covid.model.CaseInfo
import com.oakraw.thailand_covid.model.ErrorResponse
import retrofit2.http.GET

interface ApiService {
    @GET("covid-19/getTodayCases.json")
    suspend fun getTodayCaseInfo(): NetworkResponse<CaseInfo, ErrorResponse>
}