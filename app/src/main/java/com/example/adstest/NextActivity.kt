package com.example.adstest

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NextActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)
    }

    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences(Splash.PREFS_NAME, Context.MODE_PRIVATE)
        val isBannerToastEnabled = sharedPrefs.getBoolean(Splash.TOAST_BANNER_KEY, false)
        PreLoadAdmobBannerAd.ShowAdmobBanner(findViewById(R.id.banner_ad_container_next),isBannerToastEnabled)
        PreLoadAdmobNativeAd.ShowNativeAd(this,findViewById(R.id.adViewContainer_next))
    }

}