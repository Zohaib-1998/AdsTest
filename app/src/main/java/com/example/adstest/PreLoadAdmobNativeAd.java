package com.example.adstest;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class PreLoadAdmobNativeAd {

    private static NativeAd mNativeAd1;
    private static NativeAd mNativeAd2;
    private static int adUnitIndex = 0;
    private static boolean areNativeToastsEnabled = true;
    private static final String[] adUnitIds = {
            Ad_Ids.INSTANCE.getNativeId1(),
            Ad_Ids.INSTANCE.getNativeId2(),
            Ad_Ids.INSTANCE.getNativeId3()
    };

    public static void setNativeToastEnabled(boolean enabled) {
        areNativeToastsEnabled = enabled;
    }

    private static void showToast(Activity activity, String message) {
        if (areNativeToastsEnabled) {
            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void LoadNativeAd(final Activity activity, boolean areNativeToastsEnabled) {
        setNativeToastEnabled(areNativeToastsEnabled);
        String adUnitId = adUnitIds[adUnitIndex];
        AdLoader.Builder builder = new AdLoader.Builder(activity, adUnitId);

        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                boolean isDestroyed = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    isDestroyed = activity.isDestroyed();
                }
                if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }

                mNativeAd1 = nativeAd;

                String message = "Native Ad " + (adUnitIndex + 1) + " is loaded successfully";
                if (areNativeToastsEnabled) {
                    showToast(activity, message);
                }
            }
        });

        AdLoader adLoader = builder.withAdListener(new AdListener() {

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (areNativeToastsEnabled) {
                    showToast(activity, "Ad Impression");
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (adUnitIndex < adUnitIds.length - 1) {
                    adUnitIndex++;
                    LoadNativeAd(activity,areNativeToastsEnabled);
                } else {
                    showToast(activity, "Native ads failed to load");
                }
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

        if (areNativeToastsEnabled) {
            showToast(activity, "Native is called");
        }
    }

    public static void ShowNativeAd(Activity activity, FrameLayout adFrame) {
        if (mNativeAd1 != null) {
            mNativeAd2 = mNativeAd1;
            mNativeAd1 = null;
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.native_ad, null);
            populateNativeAdView(mNativeAd2, adView);
            adFrame.removeAllViews();
            adFrame.addView(adView);
        }
        LoadNativeAd(activity,areNativeToastsEnabled);
    }

    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        MediaContent mediaContent = nativeAd.getMediaContent();
        if (mediaContent != null) {
            MediaView mediaView = adView.getMediaView();
            mediaView.setMediaContent(mediaContent);
            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            adView.setMediaView(mediaView);

            VideoController videoController = mediaContent.getVideoController();
            if (videoController.hasVideoContent()) {
                videoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });
            }
        } else {
            adView.getMediaView().setVisibility(View.GONE);
        }

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }
}
