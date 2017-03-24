/* == This file is part of Tomahawk Player - <http://tomahawk-player.org> ===
 *
 *   Copyright 2012, Christopher Reichert <creichert07@gmail.com>
 *   Copyright 2013, Enno Gottschalk <mrmaffen@googlemail.com>
 *
 *   Tomahawk is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Tomahawk is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Tomahawk. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tomahawk.tomahawk_android;


import org.tomahawk.tomahawk_android.services.PlaybackService;
;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import entry.data.UlazniPodaci;
import helper.InterstitialHelper;

/**
 * This class represents the Application core.
 */

public class TomahawkApp extends Application {

    private static final String TAG = TomahawkApp.class.getSimpleName();

    public final static String PLUGINNAME_HATCHET = "hatchet";

    public final static String PLUGINNAME_USERCOLLECTION = "usercollection";

    public final static String PLUGINNAME_SPOTIFY = "spotify";

    public final static String PLUGINNAME_DEEZER = "deezer";

    public final static String PLUGINNAME_BEATSMUSIC = "beatsmusic";

    public final static String PLUGINNAME_JAMENDO = "jamendo";

    public final static String PLUGINNAME_OFFICIALFM = "officialfm";

    public final static String PLUGINNAME_SOUNDCLOUD = "soundcloud";

    public final static String PLUGINNAME_GMUSIC = "gmusic";

    public final static String PLUGINNAME_AMZN = "amazon";

    private static Context sApplicationContext;


    Activity actForAdvertisingId = null;


    String gPlayServicesUserId="";

    public static final String FLURRY_ANALYTICS_TAG = "flurry_analytics_tag";

    @Override
    public void onCreate() {
//        ACRA.init(this);
//        ACRA.getErrorReporter().setReportSender(
//                new TomahawkHttpSender(ACRA.getConfig().httpMethod(), ACRA.getConfig().reportType(),
//                        null));

        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().detectCustomSlowCalls().detectDiskReads()
                        .detectDiskWrites().detectNetwork().penaltyLog().build());
        try {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .setClassInstanceLimit(Class.forName(PlaybackService.class.getName()), 1)
                    .penaltyLog().build());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.toString());
        }

        super.onCreate();

        sApplicationContext = getApplicationContext();


        LogFlurryInitializingSteps(" ----> AppClass onCreate() called" );


        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .withListener(new FlurryAgentListener()
                {

                    @Override
                    public void onSessionStarted() {

                        LogFlurryInitializingSteps(" ----> GameAppClass FlurryAgentListener onSessionStarted" );


                    }
                })
                .build(this, UlazniPodaci.FLURRY);
    }

    public static Context getContext() {
        return sApplicationContext;
    }


    public static final String md5(final String s) {


        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            // e.printStackTrace();
        }
        catch(Exception e)
        {

        }
        return "";
    }






    public  void TryToSetFlurryUserID()
    {

        if(!gPlayServicesUserId.equals(""))
        {

            LogFlurryInitializingSteps(" ---->  User id found: " + gPlayServicesUserId);

            String idForFlurry = md5(gPlayServicesUserId);


            if(!idForFlurry.equals(""))
            {


                FlurryAgent.setUserId(idForFlurry);

                LogFlurryInitializingSteps(" ----> Flurryid found: " + idForFlurry + " and set");

            }
            else
            {
                LogFlurryInitializingSteps(" ----> Flurryid not found");
            }

        }
        else
        {
            LogFlurryInitializingSteps(" ----> User id not found");
        }
    }

    AsyncTask flurryIdTask;

    private void StopFlurryIdTask()
    {
        try
        {
            if(flurryIdTask!= null)
            {
                flurryIdTask.cancel(true);
            }
        }
        catch(Exception e)
        {

        }
    }


    public void  FlurryActCreated(Activity act)
    {
        actForAdvertisingId = act;
        StartFlurryIdTask();
    }

    public void FlurryActDestroyed()
    {
        actForAdvertisingId = null;
        StopFlurryIdTask();
    }



    private void StartFlurryIdTask()
    {

        StopFlurryIdTask();


        flurryIdTask = new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object[] params)
            {
                gPlayServicesUserId="";
                try
                {
                    if(actForAdvertisingId!= null)
                    {
                        gPlayServicesUserId = AdvertisingIdClient.getAdvertisingIdInfo(actForAdvertisingId).getId();
                    }
                    else
                    {
                        LogFlurryInitializingSteps(" ----> UnityPlayerActivity.actInstance je null" );
                        return true;
                    }


                }
                catch (Exception exp)
                {

                }


                TryToSetFlurryUserID();

                return true;
            }
        };

        flurryIdTask.execute("start");

        LogFlurryInitializingSteps(" ----> Pokrenut flurryIdTask" );
    }

    void LogFlurryInitializingSteps(String message)
    {
        //Log.i(FLURRY_ANALYTICS_TAG, message);
    }


}
