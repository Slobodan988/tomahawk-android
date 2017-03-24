package org.tomahawk.tomahawk_android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.tomahawk.tomahawk_android.R;

import static org.tomahawk.tomahawk_android.activities.TomahawkMainActivity.bitmapForSplash;
import static org.tomahawk.tomahawk_android.activities.TomahawkMainActivity.bitmapForSplashHeight;
import static org.tomahawk.tomahawk_android.activities.TomahawkMainActivity.bitmapForSplashWidth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Intent intent = new Intent(this, TomahawkMainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }


}