package com.example.adstest

import android.app.Activity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

object RemoteConfigHelperClass {
    private var nativeAdId1: String? = null
    private var nativeAdId2: String? = null
    private var nativeAdId3: String? = null

    private var bannerAdId1: String? = null
    private var bannerAdId2: String? = null
    private var bannerAdId3: String? = null

    private var interstitialAdId1: String? = null
    private var interstitialAdId2: String? = null
    private var interstitialAdId3: String? = null

    private var appOpenAdId1: String? = null
    private var appOpenAdId2: String? = null
    private var appOpenAdId3: String? = null

    fun fetchRemoteConfig(activity: Activity, onConfigFetched: () -> Unit) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf(
            "NATIVE_ID_1" to "ca-app-pub-3940256099942544/2247696110",
            "NATIVE_ID_2" to "ca-app-pub-3940256099942544/2247696110",
            "NATIVE_ID_3" to "ca-app-pub-3940256099942544/2247696110",

            "BANNER_ID_1" to "ca-app-pub-3940256099942544/6300978111",
            "BANNER_ID_2" to "ca-app-pub-3940256099942544/6300978111",
            "BANNER_ID_3" to "ca-app-pub-3940256099942544/6300978111",

            "INTERSTITIAL_ID_1" to "ca-app-pub-3940256099942544/1033173712",
            "INTERSTITIAL_ID_2" to "ca-app-pub-3940256099942544/1033173712",
            "INTERSTITIAL_ID_3" to "ca-app-pub-3940256099942544/1033173712",

            "APP_OPEN_ID_1" to "ca-app-pub-3940256099942544/3419835294",
            "APP_OPEN_ID_2" to "ca-app-pub-3940256099942544/3419835294",
            "APP_OPEN_ID_3" to "ca-app-pub-3940256099942544/3419835294"
        )
        remoteConfig.setDefaultsAsync(defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    nativeAdId1 = remoteConfig.getString("NATIVE_ID_1")
                    nativeAdId2 = remoteConfig.getString("NATIVE_ID_2")
                    nativeAdId3 = remoteConfig.getString("NATIVE_ID_3")

                    bannerAdId1 = remoteConfig.getString("BANNER_ID_1")
                    bannerAdId2 = remoteConfig.getString("BANNER_ID_2")
                    bannerAdId3 = remoteConfig.getString("BANNER_ID_3")

                    interstitialAdId1 = remoteConfig.getString("INTERSTITIAL_ID_1")
                    interstitialAdId2 = remoteConfig.getString("INTERSTITIAL_ID_2")
                    interstitialAdId3 = remoteConfig.getString("INTERSTITIAL_ID_3")

                    appOpenAdId1 = remoteConfig.getString("APP_OPEN_ID_1")
                    appOpenAdId2 = remoteConfig.getString("APP_OPEN_ID_2")
                    appOpenAdId3 = remoteConfig.getString("APP_OPEN_ID_3")

                    onConfigFetched()
                } else {
                    nativeAdId1 = remoteConfig.getString("NATIVE_ID_1")
                    nativeAdId2 = remoteConfig.getString("NATIVE_ID_2")
                    nativeAdId3 = remoteConfig.getString("NATIVE_ID_3")

                    bannerAdId1 = remoteConfig.getString("BANNER_ID_1")
                    bannerAdId2 = remoteConfig.getString("BANNER_ID_2")
                    bannerAdId3 = remoteConfig.getString("BANNER_ID_3")

                    interstitialAdId1 = remoteConfig.getString("INTERSTITIAL_ID_1")
                    interstitialAdId2 = remoteConfig.getString("INTERSTITIAL_ID_2")
                    interstitialAdId3 = remoteConfig.getString("INTERSTITIAL_ID_3")

                    appOpenAdId1 = remoteConfig.getString("APP_OPEN_ID_1")
                    appOpenAdId2 = remoteConfig.getString("APP_OPEN_ID_2")
                    appOpenAdId3 = remoteConfig.getString("APP_OPEN_ID_3")
                    onConfigFetched()
                }
            }
    }

    fun getNativeAdId1(): String? {
        return nativeAdId1
    }

    fun getNativeAdId2(): String? {
        return nativeAdId2
    }

    fun getNativeAdId3(): String? {
        return nativeAdId3
    }

    fun getBannerAdId1(): String? {
        return bannerAdId1
    }

    fun getBannerAdId2(): String? {
        return bannerAdId2
    }

    fun getBannerAdId3(): String? {
        return bannerAdId3
    }

    fun getInterstitialAdId1(): String? {
        return interstitialAdId1
    }

    fun getInterstitialAdId2(): String? {
        return interstitialAdId2
    }

    fun getInterstitialAdId3(): String? {
        return interstitialAdId3
    }

    fun getAppOpenAdId1(): String? {
        return appOpenAdId1
    }

    fun getAppOpenAdId2(): String? {
        return appOpenAdId2
    }

    fun getAppOpenAdId3(): String? {
        return appOpenAdId3
    }
}