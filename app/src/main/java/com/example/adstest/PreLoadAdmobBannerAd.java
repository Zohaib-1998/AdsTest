package com.example.adstest;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class PreLoadAdmobBannerAd {
    public static AdView adView1;
    public static AdView adView2;
    static FrameLayout frameLayout;
    private static int adUnitIndex = 0;
    private static boolean areBannerToastsEnabled = true;

    private static final String[] adUnitIds = {
            Ad_Ids.INSTANCE.getBannerId1(),
            Ad_Ids.INSTANCE.getBannerId2(),
            Ad_Ids.INSTANCE.getBannerId3()
    };

    public static void setBannerToastsEnabled(boolean enabled) {
        areBannerToastsEnabled = enabled;
    }

    private static void showToast(Activity activity, String message) {
        if (areBannerToastsEnabled) {
            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void LoadAdmobBanner(Activity activity, boolean areBannerToastsEnabled) {
        setBannerToastsEnabled(areBannerToastsEnabled);
        String adUnitId = adUnitIds[adUnitIndex];
        adView1 = new AdView(activity);
        adView1.setAdUnitId(adUnitId);

        AdSize adSize = BannerAdSize(activity);
        adView1.setAdSize(adSize);

        AdListener adListener = new AdListener() {

            @Override
            public void onAdLoaded() {
                int loadedAdIndex = adUnitIndex;
                String message = "Banner Ad " + (loadedAdIndex + 1) + " is loaded successfully";
                showToast(activity, message);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                if (adUnitIndex < adUnitIds.length - 1) {
                    adUnitIndex++;
                    LoadAdmobBanner(activity, areBannerToastsEnabled);
                } else {
                    showToast(activity, "Banner ads failed to load");
                }
            }
        };

        adView1.setAdListener(adListener);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);

        showToast(activity, "Banner request is called");
    }

    public static void ShowAdmobBanner(FrameLayout admobContainer, boolean areBannerToastsEnabled) {
        setBannerToastsEnabled(areBannerToastsEnabled);
        frameLayout = admobContainer;

        if (adView1 != null) {
            adView2 = adView1;
            adView1 = null;
            admobContainer.removeAllViews();
            admobContainer.addView(adView2);
            adView2.setVisibility(View.VISIBLE);
        }
        LoadAdmobBanner((Activity) admobContainer.getContext(), areBannerToastsEnabled);
    }

    private static AdSize BannerAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}