package com.survlogic.surveyhelper.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.survlogic.surveyhelper.BuildConfig;
import com.survlogic.surveyhelper.R;

public class RemoteConfigLoader {

    private static final String TAG = "RemoteConfigLoader";

    public interface RemoteConfigLoaderListener{
        void isLoaded(boolean isLoaded);
    }

    private Context mContext;
    private Activity mActivity;
    private PreferenceLoader mPreferenceLoader;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private RemoteConfigLoaderListener mReturnListener;

    public RemoteConfigLoader(Context mContext, RemoteConfigLoaderListener listener) {
        Log.d(TAG, "RemoteConfigLoader: Welcome to the Remote Configure Loader----------------->");
        this.mContext = mContext;
        this.mActivity = (Activity) mContext;
        this.mReturnListener = listener;

        initRemoteConfig();

    }

    private void initRemoteConfig(){
        mPreferenceLoader = new PreferenceLoader(mContext);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchRemoteConfigSettings();

    }


    /**
     * Remote Configuration in Firestore
     */

    private void fetchRemoteConfigSettings(){
        long cacheExpiration = getRemoteConfigCacheExpireTime(); // 1 hour in seconds.
        Log.d(TAG, "RemoteConfigLoader: Setting Remote Config Cache Expire Time to: " + cacheExpiration);

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "fetch complete ");
                        mFirebaseRemoteConfig.activateFetched();
                        mReturnListener.isLoaded(true);

                    }
                }).addOnFailureListener(mActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO
                Log.d(TAG, "to_delete: Error Message");
            }
        });


    }

    public void fetchRemoteConfigTheme(){
        mPreferenceLoader.setRemoteConfigThemeCompanyPages(0,
                                                    mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_THEME_STAFF_COMPANY_TOP_PAGE_0)),
                                                    mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_THEME_STAFF_COMPANY_BOTTOM_PAGE_0)),true);

        mPreferenceLoader.setRemoteConfigThemeCompanyPages(1,
                mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_THEME_STAFF_COMPANY_TOP_PAGE_1)),
                mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_THEME_STAFF_COMPANY_BOTTOM_PAGE_1)),true);


        mPreferenceLoader.setRemoteConfigThemeCompanyPages(2,
                mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_THEME_STAFF_COMPANY_TOP_PAGE_2)),
                mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_THEME_STAFF_COMPANY_BOTTOM_PAGE_2)),true);


    }

    public void fetchRemoteConfigAnnouncements(){
        mPreferenceLoader.setRemoteConfigAnnouncements(
                                                    mFirebaseRemoteConfig.getBoolean(mActivity.getResources().getString(R.string.CONFIG_KEY_FEED_REWARD)),
                                                    mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_KEY_FEE_REWARD_DETAILS)),
                                                    mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_KEY_FEED_REWARD_TOPIC)),true);

        mPreferenceLoader.setAnnouncementShowReward(true,true);
    }

    public void fetchRemoteConfigFeedStyle(){
        mPreferenceLoader.setRemoteConfigFeedStyle(mFirebaseRemoteConfig.getBoolean(mActivity.getResources().getString(R.string.CONFIG_KEY_FEED_STYLE_EVENT_STYLE_LIGHT)),true);
    }

    public void fetchRemoteConfigFeedDefaultPublicRoom(){
        mPreferenceLoader.setRemoteConfigFeedDefaultPublicRoom(mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_KEY_FEED_DEFAULT_PUBLIC_ROOM)),true);
    }

    private long getRemoteConfigCacheExpireTime(){
        Log.d(TAG, "Remote Config: Cache Time: " + mPreferenceLoader.getSettings().getAppRemoteConfigCacheExpireTime());

        return mPreferenceLoader.getSettings().getAppRemoteConfigCacheExpireTime();

    }

    /**
     * Public Functions for Remote Config
     */

    public long fetchRemoteConfigCacheExpireTime(){
        return mFirebaseRemoteConfig.getLong(mActivity.getResources().getString(R.string.CONFIG_KEY_APP_CACHE_EXP));
    }

    public boolean fetchRemoteConfigApplicationAvailable(){
        return mFirebaseRemoteConfig.getBoolean(mActivity.getResources().getString(R.string.CONFIG_KEY_APP_ACCESS));

    }

    public String fetchRemoteConfigApplicationAvailableReason(){
        return mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_KEY_APP_ACCESS_REASON));
    }

    public String fetchRemoteConfigURLTermsOfUse(){
        return mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_KEY_APP_LEGAL_TERMS_OF_USE));
    }

    public String fetchRemoteConfigURLPrivacyPolicy(){
        return mFirebaseRemoteConfig.getString(mActivity.getResources().getString(R.string.CONFIG_KEY_APP_LEGAL_PRIVACY_POLICY));
    }

    public boolean fetchRemoteConfigSignUpEnabled(){
        return mFirebaseRemoteConfig.getBoolean(mActivity.getResources().getString(R.string.CONFIG_KEY_APP_SIGN_UP_ALLOWED));
    }




}
