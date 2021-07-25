package com.oakraw.thailand_covid.service

import com.oakraw.thailand_covid.R

class AppWidgetTemplate1Provider: BaseAppWidgetProvider() {
    override var layoutId: Int = R.layout.view_widget_template1
    override var providerClass: Class<*> = AppWidgetTemplate1Provider::class.java
}