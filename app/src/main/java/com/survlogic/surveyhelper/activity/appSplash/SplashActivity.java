package com.survlogic.surveyhelper.activity.appSplash;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appEntry.EntryActivity;


public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity_main);
        Log.d(TAG, "Splash Activity Started---------------------------------------------------->");

        Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
        startActivity(intent);
        finish();
    }

}
