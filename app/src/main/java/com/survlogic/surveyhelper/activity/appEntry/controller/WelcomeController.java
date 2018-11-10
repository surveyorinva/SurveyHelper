package com.survlogic.surveyhelper.activity.appEntry.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.survlogic.surveyhelper.activity.appEntry.inter.WelcomeControllerListener;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.utils.PreferenceLoader;
import com.survlogic.surveyhelper.utils.UniversalImageLoader;

public class WelcomeController {
    private static final String TAG = "WelcomeController";

    private Context mContext;
    private Activity mActivity;

    private PreferenceLoader preferenceLoader;
    private AppSettings settings;
    private WelcomeControllerListener mControllerListener;

    public WelcomeController(Context context, WelcomeControllerListener controlListener) {
        Log.d(TAG, "-------------------Entry Controller Started-------------------------------->");
        this.mContext = context;
        this.mActivity = (Activity) context;
        this.mControllerListener = controlListener;

        initController();

    }

    public AppSettings getSettings() {
        return settings;
    }

    public boolean isAppFirstRun(){
        return settings.isAppFirstRun();
    }


    private void initController(){
        loadFirestoreSettingsInstance();
        loadImageLoaderInstance();

        preferenceLoader = new PreferenceLoader(mContext);
        settings = new AppSettings(preferenceLoader.getSettings());

        mControllerListener.refreshUI(settings);

    }

    private void loadFirestoreSettingsInstance(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

    }

    private void loadImageLoaderInstance(){
        UniversalImageLoader imageLoader = new UniversalImageLoader(mActivity);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }




}
