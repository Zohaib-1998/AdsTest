package com.example.adstest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd

class Splash : AppCompatActivity() {
    private val delay = 5000
    private var progressBar: ProgressBar? = null
    private var getStartedButton: Button? = null

    private lateinit var bannerToastToggleButton: ToggleButton
    private lateinit var nativeToastToggleButton: ToggleButton
    private lateinit var interstitialToastToggleButton: ToggleButton
    private lateinit var appopenToastToggleButton: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(Constants.isNetworkAvailable(this)){
            RemoteConfigHelperClass.fetchRemoteConfig(this) {
                PreLoadAppOpenAd()
                PreLoadAdmobNativeAd.LoadNativeAd(this,nativeToastToggleButton.isChecked)
                PreLoadAdmobBannerAd.LoadAdmobBanner(this,bannerToastToggleButton.isChecked)
                PreloadAdmobInterstitialAd.loadInterstitialAd(this,interstitialToastToggleButton.isChecked)
            }
        }

        progressBar = findViewById(R.id.splashProgressBar)
        getStartedButton = findViewById(R.id.btn)
        bannerToastToggleButton = findViewById(R.id.toggleBanner)
        nativeToastToggleButton = findViewById(R.id.toggleNative)
        interstitialToastToggleButton = findViewById(R.id.toggleInterstitial)
        appopenToastToggleButton = findViewById(R.id.toggleAppOpen)

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isBannerToastEnabled = sharedPrefs.getBoolean(TOAST_BANNER_KEY, false)
        val isNativeToastsEnabled = sharedPrefs.getBoolean(TOAST_NATIVE_KEY, false)
        val isInterstitialToastsEnabled = sharedPrefs.getBoolean(TOAST_INTERSTITIAL_KEY, false)
        val isAppOpenToastsEnabled = sharedPrefs.getBoolean(TOAST_APP_OPEN_KEY, false)
        bannerToastToggleButton.isChecked = isBannerToastEnabled
        nativeToastToggleButton.isChecked = isNativeToastsEnabled
        interstitialToastToggleButton.isChecked = isInterstitialToastsEnabled
        appopenToastToggleButton.isChecked = isAppOpenToastsEnabled

        bannerToastToggleButton.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean(TOAST_BANNER_KEY, isChecked).apply()
        }

        nativeToastToggleButton.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean(TOAST_NATIVE_KEY, isChecked).apply()
        }

        interstitialToastToggleButton.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean(TOAST_INTERSTITIAL_KEY, isChecked).apply()
        }

        appopenToastToggleButton.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean(TOAST_APP_OPEN_KEY, isChecked).apply()
            (application as ApplicationClass).showToastEnabled = isChecked
        }

        val handler = Handler()
        handler.postDelayed({
            progressBar!!.visibility = View.GONE
            getStartedButton!!.visibility = View.VISIBLE
            getStartedButton!!.setOnClickListener {
                if (Constants.isNetworkAvailable(this)) {
                    showInterstitialAd()
                } else {
                    StartActivity()
                }
            }
        }, delay.toLong())
    }

    private fun StartActivity(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    companion object {

        var mInterstitialAd: InterstitialAd? = null

        const val PREFS_NAME = "MyPrefsFile"
        const val TOAST_BANNER_KEY = "toast_banner"
        const val TOAST_NATIVE_KEY = "toast_native"
        const val TOAST_INTERSTITIAL_KEY = "toast_interstitial"
        const val TOAST_APP_OPEN_KEY = "toast_app_open"

    }

    private fun PreLoadAppOpenAd() {
        val appOpenAdManager = (application as ApplicationClass).getAppOpenAdManager()
        appOpenAdManager?.loadAd(this)
    }

    private fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            ApplicationClass.interstitialBoolean = true
            mInterstitialAd!!.show(this@Splash)
            mInterstitialAd!!.setFullScreenContentCallback(object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    Log.d("TAG", "Ad was clicked.")
                    if (interstitialToastToggleButton.isChecked) {
                        Toast.makeText(applicationContext,"Inter Ad was clicked.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAdDismissedFullScreenContent() {
                    Log.d("TAG", "Ad dismissed fullscreen content.")

                    if (interstitialToastToggleButton.isChecked) {
                        Toast.makeText(applicationContext, "Inter Ad dismissed fullscreen content.", Toast.LENGTH_SHORT).show()
                    }
                    mInterstitialAd = null
                    ApplicationClass.interstitialBoolean = false
                    StartActivity()
                    PreloadAdmobInterstitialAd.loadInterstitialAd(applicationContext,interstitialToastToggleButton.isChecked)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("TAG", "Ad failed to show fullscreen content.")

                    if (interstitialToastToggleButton.isChecked) {
                        Toast.makeText(applicationContext,"Inter Ad failed to show fullscreen content.", Toast.LENGTH_SHORT).show()
                    }
                    mInterstitialAd = null
                    ApplicationClass.interstitialBoolean = false
                }

                override fun onAdImpression() {
                    Log.d("TAG", "Ad recorded an impression.")
                    if (interstitialToastToggleButton.isChecked) {
                        Toast.makeText(applicationContext,"Inter Ad recorded an impression.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("TAG", "Ad showed fullscreen content.")
                    if (interstitialToastToggleButton.isChecked) {
                        Toast.makeText(applicationContext,"Inter Ad showed fullscreen content.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            StartActivity()
        }
    }

}