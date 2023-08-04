package com.example.adstest

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class PreloadAdmobInterstitialAd {

    companion object {

        fun loadInterstitialAd(context: Context?, isInterstitialToastsEnabled: Boolean) {
            val adUnitIds = arrayOf(
                Ad_Ids.getInterstialId1(),
                Ad_Ids.getInterstialId2(),
                Ad_Ids.getInterstialId3()
            )

            if (Splash.mInterstitialAd != null) {
                return
            }

            var adUnitIndex = 0

            fun loadAd() {
                if (adUnitIndex >= adUnitIds.size) {
                    Log.e("tag", "All interstitial ads failed to load")
                    if (isInterstitialToastsEnabled) {
                        Toast.makeText(context?.applicationContext, "Interstitial ads failed to load", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val interstitialAdId = adUnitIds[adUnitIndex]

                if (!interstitialAdId.isNullOrEmpty()) {
                    val adRequest: AdRequest = AdRequest.Builder().build()
                    InterstitialAd.load(
                        requireNotNull(context), interstitialAdId, adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                Splash.mInterstitialAd = interstitialAd
                                Log.i("tag", "Interstitial Ad loaded successfully")
                                val loadedAdIndex = adUnitIndex
                                val message = "Interstitial Ad ${loadedAdIndex + 1} is loaded successfully"
                                if (isInterstitialToastsEnabled) {
                                    Toast.makeText(context?.applicationContext, message, Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                Log.d("tag", "Interstitial Ad failed to load: ${loadAdError.message}")
                                Splash.mInterstitialAd = null
                                adUnitIndex++
                                loadAd()
                            }
                        })
                } else {
                    Log.e("tag", "Interstitial Ad ID not available")
                }
            }
            loadAd()
        }
    }
}