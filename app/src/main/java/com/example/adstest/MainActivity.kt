package com.example.adstest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.android.gms.ads.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.next).setOnClickListener {
            StartAct()
        }

    }

    private fun StartAct(){
        startActivity(Intent(this,NextActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences(Splash.PREFS_NAME, Context.MODE_PRIVATE)
        val isBannerToastEnabled = sharedPrefs.getBoolean(Splash.TOAST_BANNER_KEY, false)
        PreLoadAdmobBannerAd.ShowAdmobBanner(findViewById(R.id.banner_ad_container),isBannerToastEnabled)
        PreLoadAdmobNativeAd.ShowNativeAd(this,findViewById(R.id.adViewContainer_main))
    }

}