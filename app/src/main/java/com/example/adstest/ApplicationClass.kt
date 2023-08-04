package com.example.adstest

import android.annotation.SuppressLint
import android.app.Application
import com.google.android.gms.ads.MobileAds
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

class ApplicationClass: Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private var appOpenAdManager: AppOpenAdManager? = null
    private var currentActivity: Activity? = null
    var showToastEnabled: Boolean = false

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) { }
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager.getInstance(applicationContext)
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        showToastEnabled = sharedPrefs.getBoolean(TOAST_APP_OPEN_KEY, false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun onMoveToForeground() {
        if (interstitialBoolean == false ) {
            appOpenAdManager?.showAdIfAvailable(currentActivity!!)
        }
    }

    override fun onActivityCreated(activity: Activity, @Nullable bundle: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        if (!appOpenAdManager!!.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    class AppOpenAdManager private constructor(private val context: Context) {
        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false
        private var loadTime: Long = 0

        fun loadAd(context: Context) {
            if (isLoadingAd || isAdAvailable) {
                return
            }
            isLoadingAd = true
            val adUnitIds = listOf(
                Ad_Ids.getAppOpenId1().toString(),
                Ad_Ids.getAppOpenId2().toString(),
                Ad_Ids.getAppOpenId3().toString()
            )

            loadAdWithId(context.applicationContext, adUnitIds, 1)
        }

        private fun loadAdWithId(context: Context, adUnitIds: List<String>, adNumber: Int) {
            if (adNumber > adUnitIds.size) {
                Log.d(LOG_TAG, "All ad units failed to load.")
                if ((context.applicationContext as ApplicationClass).showToastEnabled) {
                    Toast.makeText(context, "Ad failed to load", Toast.LENGTH_SHORT).show()
                }
                isLoadingAd = false
                return
            }

            val adUnitId = adUnitIds[adNumber - 1]
            val request: AdRequest = AdRequest.Builder().build()
            AppOpenAd.load(
                context, adUnitId, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        Log.d(LOG_TAG, "Ad number $adNumber was loaded.")
                        if ((context.applicationContext as ApplicationClass).showToastEnabled) {
                            Toast.makeText(context, "Appopen Ad $adNumber is loaded successfully", Toast.LENGTH_SHORT).show()
                        }
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.d(LOG_TAG, "Failed to load Ad number $adNumber - Error: ${loadAdError.message}")
                        loadAdWithId(context, adUnitIds, adNumber + 1)
                    }
                })
        }

        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        private val isAdAvailable: Boolean
            private get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

        fun showAdIfAvailable(activity: Activity) {
            showAdIfAvailable(activity, object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {}
            })
        }

        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener
        ) {
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.")

                if ((activity.application as ApplicationClass).showToastEnabled) {
                    Toast.makeText(activity,"The app open ad is already showing.",Toast.LENGTH_SHORT).show()
                }
                return
            }

            if (!isAdAvailable) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.")

                if ((activity.application as ApplicationClass).showToastEnabled) {
                    Toast.makeText(activity,"The app open ad is not ready yet.",Toast.LENGTH_SHORT).show()
                }
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }
            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Log.d(LOG_TAG, "Ad dismissed fullscreen content.")

                    if ((activity.application as ApplicationClass).showToastEnabled) {
                        Toast.makeText(activity,"Ad dismissed fullscreen content.",Toast.LENGTH_SHORT).show()
                    }
                    appOpenAd = null
                    isShowingAd = false
                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    Log.d(LOG_TAG, adError.message)
                    if ((activity.application as ApplicationClass).showToastEnabled) {
                        Toast.makeText(activity,adError.message,Toast.LENGTH_SHORT).show()
                    }
                    appOpenAd = null
                    isShowingAd = false
                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }
            }
            isShowingAd = true
            appOpenAd!!.show(activity)
        }

        companion object {
            @SuppressLint("StaticFieldLeak")
            @Volatile
            private var instance: AppOpenAdManager? = null

            fun getInstance(context: Context): AppOpenAdManager {
                if (instance == null) {
                    synchronized(AppOpenAdManager::class.java) {
                        if (instance == null) {
                            instance = AppOpenAdManager(context)
                        }
                    }
                }
                return instance as AppOpenAdManager
            }

            private const val LOG_TAG = "AppOpenAdManager"
        }

    }

    companion object {

        var interstitialBoolean:Boolean? = false

        private const val PREFS_NAME = "MyPrefsFile"
        private const val TOAST_APP_OPEN_KEY = "toast_app_open"

    }

    fun getAppOpenAdManager(): AppOpenAdManager? {
        return appOpenAdManager
    }

}