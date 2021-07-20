package com.oakraw.thailand_covid.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun Int.toCurrencyFormat(): String {
    val formatter = DecimalFormat("#,###,###")
    return formatter.format(this)
}

fun Date.display(pattern: String): String {
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(this)
}
