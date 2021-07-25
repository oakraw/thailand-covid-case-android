package com.oakraw.thailand_covid.service

import com.oakraw.thailand_covid.R

class AppWidgetTemplate3Provider: BaseAppWidgetProvider() {
    override var layoutId: Int = R.layout.view_widget_template3
    override var providerClass: Class<*> = AppWidgetTemplate3Provider::class.java
}