package com.oakraw.thailand_covid.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.oakraw.thailand_covid.R
import com.oakraw.thailand_covid.model.CaseInfo
import com.oakraw.thailand_covid.service.PreferenceHelper.IS_FIRST_TIME
import com.oakraw.thailand_covid.service.PreferenceHelper.defaultPrefs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_tutorial.view.*
import kotlinx.android.synthetic.main.view_widget_template1.*
import kotlinx.android.synthetic.main.view_widget_template1.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import com.oakraw.thailand_covid.service.PreferenceHelper.get
import com.oakraw.thailand_covid.service.PreferenceHelper.set


class MainActivity : AppCompatActivity() {
    private lateinit var remoteConfig: FirebaseRemoteConfig
    val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initObserver()
        viewModel.fetch()
    }

    private fun initView() {
        titleHeal.text = "เข้ารักษาใหม่"
        containerUpdateDetail.isVisible = false

        swipeRefresh.setOnRefreshListener {
            viewModel.fetch()
        }

        layoutShare.setOnClickListener {
            captureAndShare()
        }

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                updateRemoteConfig()
            }
    }

    private fun initObserver() {
        viewModel.isLoading.observe(this, {
            swipeRefresh.isRefreshing = it
        })
        viewModel.todayCaseInfo.observe(this, { caseInfo ->
            textNewCase.text = caseInfo.newConfirmedDisplay
            textDate.text = caseInfo.updatedDisplay
            textDeath.text = caseInfo.newDeathsDisplay
            textHeal.text = caseInfo.newHospitalizedDisplay

            textTotalCase.text = caseInfo.casesDisplay
            textActiveHospitalized.text = caseInfo.hospitalizedDisplay
            textTotalDeath.text = caseInfo.deathsDisplay

            textCredit.text = "ข้อมูลจาก ${caseInfo.devBy}"
            textLastUpdated.text = "อัพเดตล่าสุดเมื่อ ${caseInfo.updateDate}"

            showTutorial(caseInfo)
        })
    }

    private fun captureAndShare() {
        textCaptureCredit.isVisible = true

        Handler(Looper.getMainLooper()).postDelayed({
            val bitmap = layoutCapture.drawToBitmap()
            shareBitmap(bitmap)
            textCaptureCredit.isVisible = false
        }, 500)
    }

    private fun updateRemoteConfig() {
        remoteConfig.getBoolean("show_covid").apply {
            imageTotalCase.isInvisible = !this
            imageNewCase.isInvisible = !this
        }
        textHeader.text = remoteConfig.getString("app_headline")
    }

    private fun showTutorial(caseInfo: CaseInfo) {
        val prefs = defaultPrefs(this)
        if (prefs[IS_FIRST_TIME, true]!!) {
            prefs[IS_FIRST_TIME] = false

            val fade = Fade()
            fade.duration = 1000
            TransitionManager.beginDelayedTransition(containerTutorial, fade)

            val viewExplain = layoutInflater.inflate(R.layout.view_tutorial, containerTutorial)

            viewExplain.apply {
                this.titleHeal.text = "เข้ารักษาใหม่"
                this.containerUpdateDetail.isVisible = false
                this.textNewCase.text = caseInfo.newConfirmedDisplay
                this.textDate.text = caseInfo.updatedDisplay
                this.textDeath.text = caseInfo.newDeathsDisplay
                this.textHeal.text = caseInfo.newHospitalizedDisplay
                this.layoutShouldBeClicked.setOnClickListener {
                    containerTutorial.removeAllViews()
                    captureAndShare()
                }
            }
        }

    }

    private fun shareBitmap(bitmap: Bitmap) {
        val cachePath = File(externalCacheDir, "th_cov/")
        cachePath.mkdirs()

        Log.d("", "")

        val file = File(cachePath, "capture.png")
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val myImageFileUri: Uri =
            FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)

        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri)
        intent.type = "image/png"
        startActivity(Intent.createChooser(intent, "Share with"))
    }
}