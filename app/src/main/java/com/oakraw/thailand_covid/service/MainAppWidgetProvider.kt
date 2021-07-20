package com.oakraw.thailand_covid.service

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.view.isInvisible
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.haroldadmin.cnradapter.NetworkResponse
import com.oakraw.thailand_covid.R
import com.oakraw.thailand_covid.model.CaseInfo
import com.oakraw.thailand_covid.repository.ApiRepository
import com.oakraw.thailand_covid.ui.main.MainActivity
import com.oakraw.thailand_covid.utils.display
import com.oakraw.thailand_covid.utils.toCurrencyFormat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_widget_template1.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class MainAppWidgetProvider : AppWidgetProvider(), KoinComponent {
    private var shouldShowCovid: Boolean = false
    private var remoteConfig: FirebaseRemoteConfig? = null
    val apiRepository: ApiRepository by inject()

    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        fetchRemoteConfig()
        GlobalScope.launch(Dispatchers.IO) {
            val response = apiRepository.getTodayCaseInfo()
            when (response) {
                is NetworkResponse.Success -> {
                    appWidgetIds.forEach { appWidgetId ->
                        renderWidget(context,
                                appWidgetManager, appWidgetId, response.body)
                    }
                }
            }
        }
    }

    private fun fetchRemoteConfig() {
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig?.setConfigSettingsAsync(configSettings)
        shouldShowCovid = remoteConfig?.getBoolean("show_covid") ?: false
    }

    private fun renderWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, caseInfo: CaseInfo) {
        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.view_widget_template1
        ).apply {
            setOnClickPendingIntent(R.id.root, pendingIntent)
        }

        views.setTextViewText(R.id.textNewCase, caseInfo.newConfirmedDisplay)
        views.setTextViewText(R.id.textDate, caseInfo.updatedDisplay)
        views.setTextViewText(R.id.textDeath, caseInfo.newDeathsDisplay)
        views.setTextViewText(R.id.textHeal, caseInfo.hospitalizedDisplay)

        views.setViewVisibility(R.id.imageNewCase, if(shouldShowCovid) View.VISIBLE else View.INVISIBLE)
        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}