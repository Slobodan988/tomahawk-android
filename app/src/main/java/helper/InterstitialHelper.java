package helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;


import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.supersonic.mediationsdk.logger.SupersonicError;
import com.supersonic.mediationsdk.sdk.InterstitialListener;
import com.supersonic.mediationsdk.sdk.Supersonic;
import com.supersonic.mediationsdk.sdk.SupersonicFactory;

import org.tomahawk.tomahawk_android.R;

import entry.data.UlazniPodaci;


/**
 * Created by mkrstic on 2/26/16.
 */
public class InterstitialHelper {


    //ChartBoost
    boolean chartboostInitialized = false;

    Dialog admobAdMobContinueDialog;

    private final String LOG_TAG = "interstitial_helper_log";
    boolean logEnabled = false;

    SharedPreferences sp;

    private final String KEY_TIME_WHEN_ACTIVITY_SHOWED_LAST_AD = "KEY_TIME_WHEN_ACTIVITY_SHOWED_LAST_AD";


    //this is used when logic for interstitials is that time bettwen ads must not be less than MIN_TIME_BETWEEN_ADS
    private final long MIN_TIME_BETWEEN_ADS = 60000;



    //locations for interstitial
    public static final String INTERSTITIAL_ON_ENTRY = "INTERSTITIAL_ON_ENTRY";

    public static final String INTERSTITIAL_ON_TUTORIAL = "INTERSTITIAL_ON_TUTORIAL";

    public static final String INTERSTITIAL_ON_DRAWER_CATEGORY = "INTERSTITIAL_ON_DRAWER_CATEGORY";


    //private boolean interstitialOnNonEntryLocationCalled = false;


    private boolean interstitialShown = false;

    public Activity activityInstance = null;


    //SuperSonic
    private boolean superSonicInitialized = false;
    private String mUserIdd = "UserRingtones";
    private Supersonic mMediationAgent;


    //AdMobInterstitital
    private com.google.android.gms.ads.InterstitialAd ad_mob_interstitialAd;

    // Facebook interstitial
    private boolean faceInterstitialLoaded =false;
    private com.facebook.ads.InterstitialAd FBInterstitialAd;

//
//    //ChartBoost
//    boolean chartboostInitialized = false;


    public void setInterstitialShown(boolean interstitialShown) {
        this.interstitialShown = interstitialShown;

        if(interstitialShown)
        {
           //((GlavnaActivity)this.activityInstance).setujDefaultStanje();
        }

        else
        {
            long currentTimeMillis = System.currentTimeMillis();
            sp.edit().putLong(KEY_TIME_WHEN_ACTIVITY_SHOWED_LAST_AD, currentTimeMillis).apply();
        }

    }

    public InterstitialHelper(Activity activityInstance, boolean logEnabled) {

        this.logEnabled = logEnabled;

        LogToConsole("----> Interstitial helper initialized");


        this.activityInstance = activityInstance;
        this.interstitialShown = false;

        long currentTimeMillis = System.currentTimeMillis();

        sp = PreferenceManager.getDefaultSharedPreferences(activityInstance);
        sp.edit().putLong(KEY_TIME_WHEN_ACTIVITY_SHOWED_LAST_AD, (currentTimeMillis - MIN_TIME_BETWEEN_ADS - 100)).apply();



        loadInterstitialsOnStart();



    }

    private void loadChartboostAppInterstitial()
    {

        LogToConsole("----> ChartBoost loading");

        if(!chartboostInitialized)
        {

            Chartboost.startWithAppId(this.activityInstance, UlazniPodaci.CHARTBOOST_APP_ID, UlazniPodaci.CHARTBOOST_APP_SIGNATURE);
            setListenersForChartBoost();
            Chartboost.onCreate(this.activityInstance);
            chartboostInitialized = true;

        }
        else if(!Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT))
        {
            Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
        }


    }

    private void setListenersForChartBoost()
    {

        //trenutno delegat ne radi kako treba istestirati za sledece update
        Chartboost.setDelegate(new ChartboostDelegate() {

            @Override
            public boolean shouldRequestInterstitial(String location) {
                //Log.i(TAG, "SHOULD REQUEST INTERSTITIAL '"+ (location != null ? location : "null"));
                return true;
            }

            @Override
            public boolean shouldDisplayInterstitial(String location) {

                return true;
            }

            @Override
            public void didCacheInterstitial(String location) {

                LogToConsole("chartboost --> didCacheInterstitial with location " + location);
            }

            @Override
            public void didFailToLoadInterstitial(String location, CBError.CBImpressionError error) {

                LogToConsole("chartboost --> didFailToLoadInterstitial with location " + location + " and error " + error.toString());

            }

            @Override
            public void didDismissInterstitial(String location) {


                LogToConsole("chartboost --> didDismissInterstitial with location " + location);

                Handler hn = new Handler();

                hn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setInterstitialShown(false);
                    }
                }, 500);

                Chartboost.cacheInterstitial(location);


            }

            @Override
            public void didCloseInterstitial(String location) {


                Handler hn = new Handler();

                hn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setInterstitialShown(false);
                    }
                }, 500);

                LogToConsole("chartboost --> didCloseInterstitial with location " + location);




            }

            @Override
            public void didClickInterstitial(String location) {

                Handler hn = new Handler();

                hn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setInterstitialShown(false);
                    }
                }, 500);

                LogToConsole("chartboost --> didClickInterstitial with location " + location);


            }

            @Override
            public void didDisplayInterstitial(String location) {

                setInterstitialShown(true);

                LogToConsole("chartboost --> didDisplayInterstitial with location " + location);

                //Log.i(TAG, "DID DISPLAY INTERSTITIAL: " +  (location != null ? location : "null"));
            }

//            @Override
//            public boolean shouldRequestMoreApps(String location) {
//                //Log.i(TAG, "SHOULD REQUEST MORE APPS: " +  (location != null ? location : "null"));
//                return true;
//            }

//            @Override
//            public boolean shouldDisplayMoreApps(String location) {
//                //Log.i(TAG, "SHOULD DISPLAY MORE APPS: " +  (location != null ? location : "null"));
//                return true;
//            }
//
//            @Override
//            public void didFailToLoadMoreApps(String location, CBError.CBImpressionError error) {
//                //Log.i(TAG, "DID FAIL TO LOAD MOREAPPS " +  (location != null ? location : "null")+ " Error: "+ error.name());
//                //Toast.makeText(getApplicationContext(), "MORE APPS REQUEST FAILED - " + error.name(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void didCacheMoreApps(String location) {
//                //Log.i(TAG, "DID CACHE MORE APPS: " +  (location != null ? location : "null"));
//            }
//
//            @Override
//            public void didDismissMoreApps(String location) {
//                //Log.i(TAG, "DID DISMISS MORE APPS " +  (location != null ? location : "null"));
//            }
//
//            @Override
//            public void didCloseMoreApps(String location) {
//                //Log.i(TAG, "DID CLOSE MORE APPS: "+  (location != null ? location : "null"));
//            }
//
//            @Override
//            public void didClickMoreApps(String location) {
//                //Log.i(TAG, "DID CLICK MORE APPS: "+  (location != null ? location : "null"));
//            }
//
//            @Override
//            public void didDisplayMoreApps(String location) {
//                //Log.i(TAG, "DID DISPLAY MORE APPS: " +  (location != null ? location : "null"));
//            }
//
//            @Override
//            public void didFailToRecordClick(String uri, CBError.CBClickError error) {
//                //Log.i(TAG, "DID FAILED TO RECORD CLICK " + (uri != null ? uri : "null") + ", error: " + error.name());
//                //Toast.makeText(getApplicationContext(), "FAILED TO RECORD CLICK " + (uri != null ? uri : "null") + ", error: " + error.name(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public boolean shouldDisplayRewardedVideo(String location) {
//                //Log.i(TAG, String.format("SHOULD DISPLAY REWARDED VIDEO: '%s'",  (location != null ? location : "null")));
//                return true;
//            }
//
//            @Override
//            public void didCacheRewardedVideo(String location) {
//                //Log.i(TAG, String.format("DID CACHE REWARDED VIDEO: '%s'",  (location != null ? location : "null")));
//            }
//
//            @Override
//            public void didFailToLoadRewardedVideo(String location,
//                                                   CBError.CBImpressionError error) {
//                //Log.i(TAG, String.format("DID FAIL TO LOAD REWARDED VIDEO: '%s', Error:  %s",  (location != null ? location : "null"), error.name()));
//                //Toast.makeText(getApplicationContext(), String.format("DID FAIL TO LOAD REWARDED VIDEO '%s' because %s", location, error.name()), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void didDismissRewardedVideo(String location) {
////			Log.i(TAG, String.format("DID DISMISS REWARDED VIDEO '%s'",  (location != null ? location : "null")));
//
////
////
////		    [Chartboost cacheRewardedVideo:CBVideoLokacija];
//                Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
//            }
//
//            @Override
//            public void didCloseRewardedVideo(String location) {
//                //Log.i(TAG, String.format("DID CLOSE REWARDED VIDEO '%s'",  (location != null ? location : "null")));
//            }
//
//            @Override
//            public void didClickRewardedVideo(String location) {
//                //Log.i(TAG, String.format("DID CLICK REWARDED VIDEO '%s'",  (location != null ? location : "null")));
//            }
//
//            @Override
//            public void didCompleteRewardedVideo(String location, int reward) {
//                //Log.i(TAG, String.format("DID COMPLETE REWARDED VIDEO '%s' FOR REWARD %d",  (location != null ? location : "null"), reward));
//
//
//            }
//
//            @Override
//            public void didDisplayRewardedVideo(String location) {
//
//            }
        });

    }



    private void loadInterstitialsOnStart()
    {
        loadChartboostAppInterstitial();

        loadFacebookInterstitial();

        loadGoogleAdmobInterstitial();

        loadSupersonic();

    }


    private void loadSupersonic()
    {


        if(mMediationAgent == null)
        {
            mMediationAgent = SupersonicFactory.getInstance();

            setListenersForSuperSonic();

            AsyncTask at = new AsyncTask()
            {
                @Override
                protected Object doInBackground(Object[] params)
                {
                    try
                    {
                        mUserIdd = AdvertisingIdClient.getAdvertisingIdInfo(InterstitialHelper.this.activityInstance).getId();

                        LogToConsole(" mUserId = " + mUserIdd);

                    }
                    catch (Exception exp)
                    {


                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    Log.i("Reklame", "********************  protected void onPostExecute(Object o)  *********************");
                    Log.i("Reklame", " mUserId " + mUserIdd);
                    mMediationAgent.initInterstitial(InterstitialHelper.this.activityInstance, UlazniPodaci.SUPER_SONIC_APP_ID, mUserIdd);
                    superSonicInitialized = true;
                }
            };

            at.execute("nesto");


        }

        if(superSonicInitialized && mMediationAgent!=null)
        {
            mMediationAgent.loadInterstitial();
        }




    }

    private void setListenersForSuperSonic()
    {
        InterstitialListener mInterstitialListener = new InterstitialListener() {
            public void onInterstitialInitSuccess()
            {
                LogToConsole("----> SuperSonic onInterstitialInitSuccess");
                loadSupersonic();
            }
            /**
             * Invoked when Interstitial initialization process is failed.
             * @param supersonicError - An Object which represents the reason of initialization failure.
             */
            public void onInterstitialInitFailed(SupersonicError supersonicError)
            {
                LogToConsole("----> SuperSonic onInterstitialInitFailed " + supersonicError);
            }
            /**
             Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            public void onInterstitialReady()
            {
                LogToConsole("----> SuperSonic onInterstitialReady");
            }
            /**
             invoked when there is no Interstitial Ad available after calling load function.
             */
            public void onInterstitialLoadFailed(SupersonicError supersonicError)
            {
                LogToConsole("----> SuperSonic onInterstitialLoadFailed " + supersonicError);
            }
            /*
             * Invoked when the ad was opened and shown successfully.
             */
            public void onInterstitialShowSuccess()
            {
                LogToConsole("SuperSonic onInterstitialShowSuccess ");
            }
            /**
             * Invoked when Interstitial ad failed to show.
             * @param supersonicError - An object which represents the reason of showInterstitial failure.
             */
            public void onInterstitialShowFailed(SupersonicError supersonicError)
            {
                LogToConsole("----> SuperSonic onInterstitialInitFailed " + supersonicError);
            }
            /*
             * Invoked when the end user clicked on the interstitial ad.
             */
            public void onInterstitialClick()
            {
                LogToConsole("----> SuperSonic onInterstitialClick");
            }
            /*
             * Invoked when the ad is closed and the user is about to return to the application.
             */
            public void onInterstitialClose()
            {

                LogToConsole("----> SuperSonic onInterstitialClose");
                Handler hn = new Handler();

                hn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setInterstitialShown(false);
                    }
                }, 500);


                loadSupersonic();
            }
            /**
             Invoked when the Interstitial Ad Unit is opened
             */
            public void onInterstitialOpen()
            {
                LogToConsole("----> SuperSonic onInterstitialOpen");
            }
        };

        mMediationAgent.setInterstitialListener(mInterstitialListener);
    }



    private void loadGoogleAdmobInterstitial() {


        LogToConsole("----> AdMOb loading....");


        if(ad_mob_interstitialAd == null)
        {

            MobileAds.initialize(this.activityInstance, UlazniPodaci.ADMOB_APP_ID);
            ad_mob_interstitialAd = new com.google.android.gms.ads.InterstitialAd(this.activityInstance);
            ad_mob_interstitialAd.setAdUnitId(UlazniPodaci.ADMOB_INTERSTITIAL);


            setListenersForAdMobInterstitial();
        }

        if (!ad_mob_interstitialAd.isLoading() && !ad_mob_interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder()
                    // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // .addTestDevice("F12D83BA2273FA17891174C3413D2816")
                    .build();
            ad_mob_interstitialAd.loadAd(adRequest);
        }



    }

    private void setListenersForAdMobInterstitial()
    {


        ad_mob_interstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                LogToConsole("----> Ad mob loaded");


            }

            @Override
            public void onAdOpened() {
                setInterstitialShown(true);

                LogToConsole("----> Ad mob opened");

            }

            @Override
            public void onAdClosed() {


                Handler hn = new Handler();

                hn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setInterstitialShown(false);
                    }
                }, 500);

                LogToConsole("----> Ad mob closed");
                loadGoogleAdmobInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                LogToConsole("----> Ad mob failed to load");
            }
        });

    }



//    //facebook ads
    private void loadFacebookInterstitial() {


        LogToConsole("----> faceInterstitial loading....");

        FBInterstitialAd = new com.facebook.ads.InterstitialAd(this.activityInstance, UlazniPodaci.FACEBOOK_INTERSTITIAL);
        setListenersForFacebookInterstitial();

        faceInterstitialLoaded = false;
        FBInterstitialAd.loadAd();
    }
//
//
    private void setListenersForFacebookInterstitial()
    {
        FBInterstitialAd.setAdListener(new com.facebook.ads.InterstitialAdListener() {

            @Override
            public void onAdClicked(Ad arg0) {
                // TODO Auto-generated method stub
                LogToConsole("----> faceInterstitial  onAdClicked");
            }

            @Override
            public void onAdLoaded(Ad arg0) {

                if (arg0 == FBInterstitialAd) {
                    faceInterstitialLoaded = true;
                    LogToConsole("----> faceInterstitial  onAdLoaded");
                }

            }

            @Override
            public void onError(Ad arg0, AdError arg1) {

                faceInterstitialLoaded = false;

                LogToConsole("----> faceInterstitial  onError: " + arg1);
            }

            @Override
            public void onInterstitialDismissed(Ad arg0) {

                Handler hn = new Handler();

                hn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setInterstitialShown(false);
                    }
                }, 500);


                LogToConsole("----> faceInterstitial  onInterstitialDismissed");
                loadFacebookInterstitial();

            }

            @Override
            public void onInterstitialDisplayed(Ad arg0) {
                // TODO Auto-generated method stub


                setInterstitialShown(true);
                LogToConsole("----> faceInterstitial  onInterstitialDisplayed");


            }

        });
//
    }



    public  void CallInterstitial(Activity actForPresentingInterstitial, String interstitialLocation) {


        LogToConsole("----> Interstitial helper CallInterstitial called for location " + interstitialLocation);


        initializeAdMobContinueDialog(actForPresentingInterstitial);



        if(interstitialShown || admobAdMobContinueDialog.isShowing() || this.activityInstance == null || actForPresentingInterstitial == null)
        {

            LogToConsole("----> Interstitial helper can't show interstitial");

            if(interstitialShown)
            {
                LogToConsole("----> Interstitial is allready shown");
            }
            else if(admobAdMobContinueDialog.isShowing())
            {
                LogToConsole("----> Interstitial admobAdMobContinueDialog is active");
            }
            else if(this.activityInstance == null)
            {
                LogToConsole("----> Activity instance for interstitial helper is null");
            }
            else if(actForPresentingInterstitial == null)
            {
                LogToConsole("----> Activity for presenting interstitial helper is null");
            }


            return;
        }




            long currentTimeMillis = System.currentTimeMillis();


            long lastInterstitialShowTime = sp.getLong(KEY_TIME_WHEN_ACTIVITY_SHOWED_LAST_AD, (currentTimeMillis - MIN_TIME_BETWEEN_ADS - 100));


            //this time is used when logic for interstitials is that time bettwen ads must not be less than MIN_TIME_BETWEEN_ADS
            //it can be used for example
            //if (interstitialLocation.equals(INTERSTITIAL_ON_RESUME) && (difference > MIN_TIME_BETWEEN_ADS))

            long difference = currentTimeMillis - lastInterstitialShowTime;

            LogToConsole("----> Difference is ---> between time last shown ad was shown and current time is: " + difference);


        if (interstitialLocation.equals(INTERSTITIAL_ON_ENTRY))
        {


            if (Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT))
            {
                Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);


            }

            else if (faceInterstitialLoaded && FBInterstitialAd!=null )
            {


                loadChartboostAppInterstitial();
                FBInterstitialAd.show();

            }

            else if (ad_mob_interstitialAd != null && ad_mob_interstitialAd.isLoaded())
            {


                loadChartboostAppInterstitial();
                loadFacebookInterstitial();



                ShowAdMobContinueDialog();

            }

            else if (mMediationAgent.isInterstitialReady())
            {

                loadChartboostAppInterstitial();
                loadFacebookInterstitial();
                loadGoogleAdmobInterstitial();
                LogToConsole("Supersonic spreman");
                mMediationAgent.showInterstitial();
            }


            else
            {

                loadChartboostAppInterstitial();
                loadFacebookInterstitial();

                loadGoogleAdmobInterstitial();

                loadSupersonic();


            }
        }
        else if (interstitialLocation.equals(INTERSTITIAL_ON_TUTORIAL) )
        {



            if (Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT))
            {
                Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);


            }


            else if (faceInterstitialLoaded && FBInterstitialAd!=null )
            {


                loadChartboostAppInterstitial();
                FBInterstitialAd.show();

            }

            else if (ad_mob_interstitialAd != null && ad_mob_interstitialAd.isLoaded())
            {

                loadChartboostAppInterstitial();
                loadFacebookInterstitial();


                ad_mob_interstitialAd.show();


            }

            else if (mMediationAgent.isInterstitialReady())
            {

                loadChartboostAppInterstitial();
                loadFacebookInterstitial();
                loadGoogleAdmobInterstitial();
                LogToConsole("Supersonic spreman");
                mMediationAgent.showInterstitial();
            }


            else
            {

                loadChartboostAppInterstitial();

                loadFacebookInterstitial();

                loadGoogleAdmobInterstitial();

                loadSupersonic();


            }


        }
        else if (interstitialLocation.equals(INTERSTITIAL_ON_DRAWER_CATEGORY) &&  (difference > MIN_TIME_BETWEEN_ADS))
        {


            if (Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT))
            {
                Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);


            }


            else if (faceInterstitialLoaded && FBInterstitialAd!=null )
            {

                loadChartboostAppInterstitial();

                FBInterstitialAd.show();

            }

            else if (ad_mob_interstitialAd != null && ad_mob_interstitialAd.isLoaded())
            {

                loadChartboostAppInterstitial();

                loadFacebookInterstitial();



                ad_mob_interstitialAd.show();

            }

            else if (mMediationAgent.isInterstitialReady())
            {

                loadChartboostAppInterstitial();

                loadFacebookInterstitial();
                loadGoogleAdmobInterstitial();
                LogToConsole("Supersonic spreman");
                mMediationAgent.showInterstitial();
            }


            else
            {

                loadChartboostAppInterstitial();

                loadFacebookInterstitial();

                loadGoogleAdmobInterstitial();

                loadSupersonic();


            }


        }

    }




    public void onResume(Activity act)
    {
        if (mMediationAgent != null) {
            mMediationAgent.onResume (act);
        }

        Chartboost.onResume(act);


    }


    public void onPause(Activity act)
    {

        if (mMediationAgent != null) {
            mMediationAgent.onPause(act);
        }

        Chartboost.onPause(act);



    }

    public boolean onBackPressed()
    {
        return Chartboost.onBackPressed();

    }


    public void onDestroy()
    {

        if (FBInterstitialAd != null) {
            FBInterstitialAd.destroy();
        }

        Chartboost.onDestroy(this.activityInstance);

        LogToConsole("----> Interstitial helper onDestroy called");

        this.activityInstance = null;


    }

    public void onStart(Activity act)
    {
        Chartboost.onStart(act);

        loadChartboostAppInterstitial();

    }

    public void onStop(Activity act)
    {
        Chartboost.onStop(act);
    }


    //aditional for admob
    private void initializeAdMobContinueDialog(Activity actInstanceForDialog)
    {


        if(admobAdMobContinueDialog == null) {

            LogToConsole("----> Initializing admobAdMobContinueDialog...");
            admobAdMobContinueDialog = new Dialog(actInstanceForDialog, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            admobAdMobContinueDialog.setContentView(R.layout.admob_continue_dialog);


            admobAdMobContinueDialog.setCancelable(false);
            admobAdMobContinueDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            admobAdMobContinueDialog.dismiss();






            View continueButton = admobAdMobContinueDialog.findViewById(R.id.specialButton);

            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(ad_mob_interstitialAd != null && ad_mob_interstitialAd.isLoaded())
                    {
                        ad_mob_interstitialAd.show();
                    }
                    Handler hn = new Handler();
                    hn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            HideAdMobContinueDialog();
                        }
                    }, 700);

                }
            });

            LogToConsole("----> admobAdMobContinueDialog initialized");
        }
    }

    private void ShowAdMobContinueDialog()
    {

        LogToConsole("----> ShowAdMobContinueDialog called");
        admobAdMobContinueDialog.show();
        PokreniGiftAnim();
    }
    private void HideAdMobContinueDialog()
    {

        LogToConsole("----> HideAdMobContinueDialog called");
        admobAdMobContinueDialog.dismiss();
        ZaustaviGiftAnim();
    }


    private void LogToConsole(String stringForLog)
    {
        if(logEnabled)
        {

            Log.w(LOG_TAG, stringForLog);
        }
    }

    public void PokreniGiftAnim()
    {

        try {


            final ImageView gift = ((ImageView) admobAdMobContinueDialog.findViewById(R.id.specialButton));

            gift.setClickable(false);

            TextView continueText = ((TextView) admobAdMobContinueDialog.findViewById(R.id.continueTV));


            gift.setImageResource(R.drawable.button_continue_animation);



            gift.clearAnimation();
            continueText.clearAnimation();





            AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;

//            continueText.startAnimation(fadeIn);

            fadeIn.setDuration(1000);
            fadeIn.setFillAfter(true);

            fadeIn.setStartOffset(500);



            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation)
                {
                    gift.setClickable(true);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });



            AnimationDrawable anim = (AnimationDrawable) gift
                    .getDrawable();


            continueText.setAnimation(fadeIn);

//
//            viewForAlpha.clearAnimation();
//            viewForAlpha.setAnimation(animAlphaUp);
//
//            viewForTranslation.clearAnimation();
//            viewForTranslation.setAnimation(animTranslateUp);

//            AnimationSet as = new AnimationSet(true);
//
//            as.addAnimation(anim);
//            as.addAnimation(fadeIn);

         //  as.start();


            anim.start();
            fadeIn.start();


        }
        catch(Exception e)
        {

        }
    }

    public void ZaustaviGiftAnim()
    {

        try {


            ImageView gift = ((ImageView) admobAdMobContinueDialog.findViewById(R.id.specialButton));
            // gift.setImageResource(R.drawable.wave_animation);
//
//		AnimationDrawable anim = (AnimationDrawable) gifts
//				.getDrawable();
//		anim.start();

            gift.clearAnimation();
        }
        catch(Exception e)
        {

        }

    }




}
