package com.survlogic.surveyhelper.activity.appEntry.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appEntry.inter.EntryControllerListener;
import com.survlogic.surveyhelper.activity.appIntro.IntroActivity;
import com.survlogic.surveyhelper.activity.appLogin.LoginActivity;
import com.survlogic.surveyhelper.activity.staff.StaffActivity;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

public class EntryController {

    private static final String TAG = "EntryController";

    private Context mContext;
    private Activity mActivity;

    private PreferenceLoader mPreferenceLoader;
    private AppSettings settings = new AppSettings();

    private EntryControllerListener mEntryListener;
    private static final int INTENT_REQUEST_FIREBASE_LOGIN = 1000;

    public EntryController(Context context, EntryControllerListener entryListener) {
        Log.d(TAG, "Entry Controller: Started-------------------------------------------------->");
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mEntryListener = entryListener;

        init();
    }

    private void init(){
        mPreferenceLoader = new PreferenceLoader(mContext);
        settings = new AppSettings(mPreferenceLoader.getSettings());




    }

    public void startHomeActivityWorkflow(){
        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            Log.d(TAG, "Entry Controller: User Logged in at: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

            startMainActivity();

        }else{
            Log.d(TAG, "Entry Controller: User Not Logged in!");

            startLoginActivityForResults();
        }

    }

    public void startStartupActivityWorkflow(){
        startIntroActivity();
    }

    private void startIntroActivity(){
        Intent intent = new Intent(mActivity, IntroActivity.class);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mActivity.finish();

    }

    private void startLoginActivityForResults(){
        Intent i = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivityForResult(i,INTENT_REQUEST_FIREBASE_LOGIN);
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void startMainActivity(){
        //todo access level choice to select which section of app the user will be taken.

        Intent intent = new Intent(mActivity, StaffActivity.class);
        intent.putExtra(mActivity.getResources().getString(R.string.KEY_PARENT_ACTIVITY),R.string.CLASS_ENTRY);
        mActivity.startActivity(intent);

        mActivity.overridePendingTransition(R.anim.anim_entry_show_entry_splash, android.R.anim.fade_out);
        mActivity.finish();
    }


}
