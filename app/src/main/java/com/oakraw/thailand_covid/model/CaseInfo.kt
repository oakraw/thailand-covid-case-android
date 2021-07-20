package com.oakraw.thailand_covid.model

import com.google.gson.annotations.SerializedName
import com.oakraw.thailand_covid.utils.display
import com.oakraw.thailand_covid.utils.toCurrencyFormat
import java.util.*

data class CaseInfo(
    @SerializedName("Confirmed")
    val confirmed: Int? = null,
    @SerializedName("DevBy")
    val devBy: String? = null,
    @SerializedName("Hospitalized")
    val hospitalized: Int? = null,
    @SerializedName("NewConfirmed")
    val newConfirmed: Int? = null,
    @SerializedName("NewDeaths")
    val newDeaths: Int? = null,
    @SerializedName("NewHospitalized")
    val newHospitalized: Int? = null,
    @SerializedName("NewRecovered")
    val newRecovered: Int? = null,
    @SerializedName("UpdateDate")
    val updateDate: String? = null,
    val active: Int? = null,
    val activePerOneMillion: Double? = null,
    val cases: Int? = null,
    val casesPerOneMillion: Int? = null,
    val continent: String? = null,
    val country: String? = null,
    val critical: Int? = null,
    val criticalPerOneMillion: Double? = null,
    val deaths: Int? = null,
    val deathsPerOneMillion: Int? = null,
    val oneCasePerPeople: Int? = null,
    val oneDeathPerPeople: Int? = null,
    val oneTestPerPeople: Int? = null,
    val population: Int? = null,
    val recovered: Int? = null,
    val recoveredPerOneMillion: Double? = null,
    val tests: Int? = null,
    val testsPerOneMillion: Int? = null,
    val todayCases: Int? = null,
    val todayDeaths: Int? = null,
    val todayRecovered: Int? = null,
    val undefined: Int? = null,
    val updated: Long? = null
) {
    val newConfirmedDisplay: String
        get() = newConfirmed?.toCurrencyFormat() ?: "-"
    val updatedDisplay: String
        get() = updated?.let { Date(it) }?.display("dd/MM") ?: ""
    val newDeathsDisplay: String
        get() = newDeaths?.toCurrencyFormat() ?: "-"
    val deathsDisplay: String
        get() = deaths?.toCurrencyFormat() ?: "-"
    val hospitalizedDisplay: String
        get() = hospitalized?.toCurrencyFormat() ?: "-"
    val newHospitalizedDisplay: String
        get() = newHospitalized?.toCurrencyFormat() ?: "-"
    val casesDisplay: String
        get() = cases?.toCurrencyFormat() ?: "-"
}