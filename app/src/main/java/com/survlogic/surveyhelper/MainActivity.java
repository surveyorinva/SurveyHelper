package com.survlogic.surveyhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.survlogic.surveyhelper.activity.appEntry.controller.WelcomeController;
import com.survlogic.surveyhelper.activity.appEntry.inter.WelcomeControllerListener;
import com.survlogic.surveyhelper.activity.appSettings.SettingsActivity;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.utils.RemoteConfigLoader;

public class MainActivity extends AppCompatActivity implements RemoteConfigLoader.RemoteConfigLoaderListener, WelcomeControllerListener {

    private static final String TAG = "MainActivity";
    private Context mContext;
    private RemoteConfigLoader remoteConfigLoader;

    private WelcomeController mWelcomeController;
    @Override
    public void refreshUI(AppSettings settings) {

    }

    @Override
    public void isLoaded(boolean isLoaded) {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        i.putExtra(getResources().getString(R.string.KEY_PARENT_ACTIVITY),getString(R.string.CLASS_MAIN));
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Started..........................................................>");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mContext = MainActivity.this;
        remoteConfigLoader = new RemoteConfigLoader(mContext, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Menu Created------------------------------------------>");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
