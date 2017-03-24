/* == This file is part of Tomahawk Player - <http://tomahawk-player.org> ===
 *
 *   Copyright 2012, Enno Gottschalk <mrmaffen@googlemail.com>
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
package org.tomahawk.tomahawk_android.fragments;

import com.uservoice.uservoicesdk.UserVoice;

import org.tomahawk.tomahawk_android.R;
import org.tomahawk.tomahawk_android.activities.TomahawkMainActivity;
import org.tomahawk.tomahawk_android.adapters.FakePreferencesAdapter;
import org.tomahawk.tomahawk_android.dialogs.ConfigDialog;
import org.tomahawk.tomahawk_android.utils.FakePreferenceGroup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import entry.data.UlazniPodaci;

/**
 * {@link org.tomahawk.tomahawk_android.fragments.TomahawkListFragment} which fakes the standard
 * {@link android.preference.PreferenceFragment} behaviour. We need to fake it, because the official
 * support library doesn't provide a {@link android.preference.PreferenceFragment} class
 */
public class PreferenceInfoFragment extends TomahawkListFragment
        implements OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = PreferenceInfoFragment.class.getSimpleName();

    public static final String PREFERENCE_ID_APPVERSION = "app_version";

    public static final String PREFERENCE_ID_USERVOICE = "uservoice";

    public static final String PREFERENCE_ID_SENDLOG = "sendlog";

    public static final String PREFERENCE_ID_PLAYSTORELINK = "playstore_link";

    public static final String PREFERENCE_ID_WEBSITELINK = "website_link";

    public static final String PREFERENCE_ID_MORE_APPS = "PREFERENCE_ID_MORE_APPS";

    public static final String PREFERENCE_ID_PRIVACY_POLICY = "PREFERENCE_ID_PRIVACY_POLICY";

    /**
     * Called, when this {@link org.tomahawk.tomahawk_android.fragments.PreferenceInfoFragment}'s
     * {@link android.view.View} has been created
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);

        // Set up the set of FakePreferences to be shown in this Fragment
        List<FakePreferenceGroup> fakePreferenceGroups = new ArrayList<>();
        FakePreferenceGroup prefGroup = new FakePreferenceGroup();

       FakePreferenceGroup.FakePreference pref;

//        pref= new FakePreferenceGroup.FakePreference();
//        pref.type = FakePreferenceGroup.TYPE_PLAIN;
//        pref.id = PREFERENCE_ID_USERVOICE;
//        pref.title = getString(R.string.preferences_app_uservoice);
//        pref.summary = getString(R.string.preferences_app_uservoice_text);
//        prefGroup.addFakePreference(pref);

        pref = new FakePreferenceGroup.FakePreference();
        pref.type = FakePreferenceGroup.TYPE_PLAIN;
        pref.id = PREFERENCE_ID_PLAYSTORELINK;
        pref.title = getString(R.string.preferences_app_playstore_link);


        String originalString = getString(R.string.preferences_app_playstore_link_text);
        String replacedString = originalString.replace("{{APP_NAME}}", getString(R.string.app_name));
        //pref.summary = getString(R.string.preferences_app_playstore_link_text);
        pref.summary = replacedString;
        prefGroup.addFakePreference(pref);

        pref = new FakePreferenceGroup.FakePreference();
        pref.type = FakePreferenceGroup.TYPE_PLAIN;
        pref.id = PREFERENCE_ID_WEBSITELINK;
        pref.title = getString(R.string.preferences_app_website_link);

//        originalString = getString(R.string.preferences_app_website_link_text);
//        replacedString = originalString.replace("{{APP_NAME}}", getString(R.string.app_name));
//        pref.summary = replacedString;

        pref.summary = getString(R.string.preferences_app_website_link_text);
        prefGroup.addFakePreference(pref);


        pref = new FakePreferenceGroup.FakePreference();
        pref.type = FakePreferenceGroup.TYPE_PLAIN;
        pref.id = PREFERENCE_ID_MORE_APPS;
        pref.title = getString(R.string.more_apps);

        pref.summary = "";
        prefGroup.addFakePreference(pref);




        pref = new FakePreferenceGroup.FakePreference();
        pref.type = FakePreferenceGroup.TYPE_PLAIN;
        pref.id = PREFERENCE_ID_PRIVACY_POLICY;
        pref.title = getString(R.string.privacy_policy);

        pref.summary = "";
        prefGroup.addFakePreference(pref);


//        pref = new FakePreferenceGroup.FakePreference();
//        pref.type = FakePreferenceGroup.TYPE_PLAIN;
//        pref.id = PREFERENCE_ID_SENDLOG;
//        pref.title = getString(R.string.preferences_app_sendlog);
//        pref.summary = getString(R.string.preferences_app_sendlog_text);
//        prefGroup.addFakePreference(pref);

        pref = new FakePreferenceGroup.FakePreference();
        pref.type = FakePreferenceGroup.TYPE_PLAIN;
        pref.id = PREFERENCE_ID_APPVERSION;
        pref.title = getString(R.string.preferences_app_version);
        pref.summary = "";
        try {
            if (getActivity().getPackageManager() != null) {
                PackageInfo packageInfo = getActivity().getPackageManager()
                        .getPackageInfo(getActivity().getPackageName(), 0);
                pref.summary = packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "onViewCreated: " + e.getClass() + ": " + e.getLocalizedMessage());
        }
        prefGroup.addFakePreference(pref);

        fakePreferenceGroups.add(prefGroup);

        // Now we can push the complete set of FakePreferences into our FakePreferencesAdapter,
        // so that it can provide our ListView with the correct Views.
        FakePreferencesAdapter fakePreferencesAdapter = new FakePreferencesAdapter(
                getActivity().getLayoutInflater(), fakePreferenceGroups);
        setListAdapter(fakePreferencesAdapter);

        getListView().setOnItemClickListener(this);
        setupNonScrollableSpacer(getListView());
    }

    /**
     * Initialize
     */
    @Override
    public void onResume() {
        super.onResume();

        getListAdapter().notifyDataSetChanged();
    }

    /**
     * Called every time an item inside the {@link se.emilsjolander.stickylistheaders.StickyListHeadersListView}
     * is clicked
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this will be a view
     *                 provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FakePreferenceGroup.FakePreference fakePreference
                = (FakePreferenceGroup.FakePreference) getListAdapter().getItem(position);
        if (fakePreference.id.equals(PREFERENCE_ID_USERVOICE)) {
            //UserVoice.launchUserVoice(getActivity());
             sendEmail();


        }
        else if (fakePreference.id.equals(PREFERENCE_ID_SENDLOG))
        {
            //ne koristimo


        }
        else if (fakePreference.id.equals(PREFERENCE_ID_PLAYSTORELINK))
        {


            ((TomahawkMainActivity)getActivity()).Rate();


        } else if (fakePreference.id.equals(PREFERENCE_ID_WEBSITELINK)) {

            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(UlazniPodaci.LINK_NA_WEB_SITE));
                startActivity(i);
            }
            catch(Exception e)
            {
                //krs od uredjaja detektovan
            }
        }

        else if (fakePreference.id.equals(PREFERENCE_ID_MORE_APPS)) {


            ((TomahawkMainActivity)getActivity()).moreApps();
        }

        else if (fakePreference.id.equals(PREFERENCE_ID_PRIVACY_POLICY)) {


            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(UlazniPodaci.LINK_TO_EXTERNAL_PRIVACY_POLICY));
                startActivity(i);
            }
            catch(Exception e)
            {
                //krs od uredjaja detektovan
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        getListAdapter().notifyDataSetChanged();
    }


    protected void sendEmail() {

        try {
        Log.i("Send email", "");

         String contactEmail = "bestringtonesapps@gmail.com";

        String subject = "";

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", contactEmail, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        ((TomahawkMainActivity)getActivity()).startActivity(Intent.createChooser(emailIntent, "Send email..."));

//            startActivity(Intent.createChooser(emailIntent, "Send mail..."));


        }
        catch (android.content.ActivityNotFoundException ex) {

        }
    }
}
