package com.oakraw.thailand_covid.service

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


abstract class BaseAppWidgetProvider : AppWidgetProvider(), KoinComponent {
    private var shouldShowCovid: Boolean = false
    private var remoteConfig: FirebaseRemoteConfig? = null
    private val apiRepository: ApiRepository by inject()
    private val REFRESH_TAG = "REFRESH_TAG"
    abstract var layoutId: Int
    abstract var providerClass: Class<*>

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == REFRESH_TAG) {
            Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show();

            val updateIntent = Intent(context, providerClass)
            updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(
                    ComponentName(
                        context,
                        providerClass
                    )
                )

            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(updateIntent)
        }
    }

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
                        renderWidget(
                            context,
                            appWidgetManager, appWidgetId, response.body
                        )
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

    private fun renderWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        caseInfo: CaseInfo
    ) {
        Log.d("appWidgetId", appWidgetId.toString())
        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
            .let { intent ->
                PendingIntent.getActivity(context, 0, intent, 0)
            }

        val views: RemoteViews = RemoteViews(
            context.packageName,
            layoutId
        ).apply {
            setOnClickPendingIntent(R.id.root, pendingIntent)
            setTextViewText(R.id.textNewCase, caseInfo.newConfirmedDisplay)
            setTextViewText(R.id.textDate, caseInfo.updatedDisplay)
            setTextViewText(R.id.textDeath, caseInfo.newDeathsDisplay)
            setTextViewText(R.id.textHeal, caseInfo.hospitalizedDisplay)
            setTextViewText(R.id.textUpdateTimestamp, Date().display("dd/MM/YY hh:mm") ?: "")
            setOnClickPendingIntent(R.id.buttonRefresh, getPendingSelfIntent(context, REFRESH_TAG));


            setViewVisibility(
                R.id.imageNewCase,
                if (shouldShowCovid) View.VISIBLE else View.INVISIBLE
            )
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    protected fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

}