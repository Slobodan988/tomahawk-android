package helper;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import entry.data.UlazniPodaci;


/**
 * Created by mkrstic on 11/5/15.
 */
public class BannerHelper {



    public Activity activityInstance;


    // za banner
    private AdView adView;

    // za facebook banner
    private com.facebook.ads.AdView FBadView;

    private LinearLayout layoutForBanner;


    boolean ucitanAdmobBanner = false;
    boolean ucitanFBBanner = false;


    public BannerHelper(Activity activityInstance, LinearLayout layoutForBanner) {
        this.activityInstance = activityInstance;
        this.layoutForBanner = layoutForBanner;


    }

    public void onDestroy()
    {

        if(FBadView != null)
        {
            FBadView.destroy();
        }
    }







    public void UcitajBannere()
    {



//        try {
            if (layoutForBanner.getChildCount() > 0) {
                //layoutForBanner.removeAllViews();

                Log.i("Reklame", "Banner vec postoji, ne dodaj nov");

                return;
            }
            else
            {
                Log.i("Reklame", "Banner ne postoji, probaj da ucitas nov");
            }
//        }
//        catch(Exception e)
//        {
//
//        }

        if(adView!=null) {
            adView.destroy();
        }


        adView = new AdView(this.activityInstance);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(UlazniPodaci.ADMOB_BANNER);

        ucitanAdmobBanner = false;
        ucitanFBBanner = false;
        // layout.addView(adView);

        // Create an ad request. Check logcat output for the hashed device
        // ID to
        // get test ads on a physical device.
        final AdRequest adRequest;

        adRequest = new AdRequest.Builder()

        //.addTestDevice("FC7008A0A62CB42339AC8C39639B7163")


                .build();


        // Start loading the ad in the background.
       // adView.loadAd(adRequest);

        // varijanta sa listenerom

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                // Save app state before going to the ad overlay.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                Log.i("Reklame", "NIJE Ucitan admob baner");
            }

            @Override
            public void onAdLoaded() {


                Log.i("Reklame", "Ucitan admob baner");


                // izmena
                if (layoutForBanner.getChildCount() == 0) {
                    layoutForBanner.addView(adView);
                }
            }

            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdLeftApplication() {

            }
        });

        if(FBadView!=null) {
            FBadView.destroy();
        }

        FBadView = new com.facebook.ads.AdView(this.activityInstance, UlazniPodaci.FACEBOOK_BANNER, com.facebook.ads.AdSize.BANNER_HEIGHT_50);


        FBadView.setAdListener(new com.facebook.ads.AdListener() {


            @Override

            public void onError(Ad ad, AdError error) {

                // Ad failed to load.

                // Add code to hide the ad's view

                Log.i("Reklame", "NIJE Ucitan FACEBOOK baner");


                try {
                    adView.loadAd(adRequest);
                }
                catch(Exception e)
                {

                }
                Log.i("Reklame", "Ucitaj admob banner");

            }


            @Override

            public void onAdLoaded(Ad ad) {

                // Ad was loaded

                // Add code to show the ad's view

                Log.i("Reklame", "Ucitan FACEBOOK baner");

                if (layoutForBanner.getChildCount() == 0) {
                    layoutForBanner.addView(FBadView);
                }

            }


            @Override

            public void onAdClicked(Ad ad) {

                // Use this function to detect when an ad was clicked.

                Log.i("Reklame", "KLIKNUT FACEBOOK baner");

            }


        });

        //s4
        //AdSettings.addTestDevice("66eb7d0b616a176ad457064258fe0968");
        //mali samsung
        //AdSettings.addTestDevice("b1cd80f6f6ccb70700e432b602f39268");
        FBadView.loadAd();

    }

    public void HideBanner()
    {
        if(layoutForBanner!=null)
        {
            layoutForBanner.setVisibility(View.GONE);
        }
    }
    public void ShowBanner()
    {
        if(layoutForBanner!=null)
        {
            layoutForBanner.setVisibility(View.VISIBLE);
        }
    }







}
