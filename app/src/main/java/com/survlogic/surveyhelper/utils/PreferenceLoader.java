package com.survlogic.surveyhelper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedReflections;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.model.AppStaticSettings;
import com.survlogic.surveyhelper.model.ThemeSettings;

import java.util.Date;

public class PreferenceLoader {
    private static final String TAG = "PreferenceLoader";

    private Context mContext;
    private Activity mActivity;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private AppSettings settings;
    private ThemeSettings themeSettings;
    private AppStaticSettings staticSettings;

    public static final int REFLECTION_MORNING = 0,
                REFLECTION_EVENING = 1,
                REFLECTION_WEEKLY = 2;

    public PreferenceLoader(Context mContext) {
        this.mContext = mContext;
        this.mActivity  = (Activity) mContext;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        if(sharedPreferences.getBoolean(mContext.getString(R.string.pref_app_first_run),true)){
            createPreferences();
        }else{
            createAppSettings();
        }
    }

    private void createPreferences(){
        setAppDemo(true,true,false);

        int cacheExpireTimeDefault = mActivity.getResources().getInteger(R.integer.remote_config_cache_expire_time_default);
        long cacheExpireTimeDefaultLong = (long) cacheExpireTimeDefault;
        setRemoteConfigCacheExpireTime(cacheExpireTimeDefaultLong,true,false);

        createAppSettings();

    }

    private void createAppSettings(){
        settings = new AppSettings();

        //Application Level
        settings.setFirstRun(sharedPreferences.getBoolean(mContext.getString(R.string.pref_app_first_run),true));
        settings.setAppDemo(sharedPreferences.getBoolean(mContext.getString(R.string.pref_app_demo_mode),false));

        //Firebase
        settings.setUserVerified(sharedPreferences.getBoolean(mContext.getString(R.string.pref_firebase_user_verified),false));
        settings.setUserLoggedIn(sharedPreferences.getBoolean(mContext.getString(R.string.pref_firebase_user_logged_in),false));
        settings.setFirebaseUserID(sharedPreferences.getString(mContext.getString(R.string.pref_firebase_user_id),null));
        settings.setFirebaseUserEmail(sharedPreferences.getString(mContext.getString(R.string.pref_firebase_user_email),null));
        settings.setFirebaseUserPassword(sharedPreferences.getString(mContext.getString(R.string.pref_firebase_user_password),null));
        settings.setFirebaseUserNameFirst(sharedPreferences.getString(mContext.getString(R.string.pref_firebase_user_name_first),null));
        settings.setFirebaseUserNameLast(sharedPreferences.getString(mContext.getString(R.string.pref_firebase_user_name_last),null));
        settings.setFirebaseAccessKeySecure(sharedPreferences.getString(mContext.getString(R.string.pref_firebase_user_access_key_secure),null));

        //Remote Config
        settings.setAppRemoteConfigCacheExpireTime(sharedPreferences.getLong(mContext.getString(R.string.pref_app_remote_config_cache_expire_time),R.integer.remote_config_cache_expire_time_default));

    }

    private void createThemeSettings(){
        themeSettings = new ThemeSettings();

        themeSettings.setStaffCompanyPage0Top(sharedPreferences.getString(mContext.getString(R.string.theme_staff_company_top_page_0),""));
        themeSettings.setStaffCompanyPage0Bottom(sharedPreferences.getString(mContext.getString(R.string.theme_staff_company_bottom_page_0),""));

        themeSettings.setStaffCompanyPage1Top(sharedPreferences.getString(mContext.getString(R.string.theme_staff_company_top_page_1),""));
        themeSettings.setStaffCompanyPage1Bottom(sharedPreferences.getString(mContext.getString(R.string.theme_staff_company_bottom_page_1),""));

        themeSettings.setStaffCompanyPage2Top(sharedPreferences.getString(mContext.getString(R.string.theme_staff_company_top_page_2),""));
        themeSettings.setStaffCompanyPage2Bottom(sharedPreferences.getString(mContext.getString(R.string.theme_staff_company_bottom_page_2),""));

    }

    private void createAppStaticSettings(){
        staticSettings = new AppStaticSettings();

        staticSettings.setPromo(sharedPreferences.getBoolean(mContext.getString(R.string.pref_feed_reward),false));
        staticSettings.setPromoActive(sharedPreferences.getBoolean(mContext.getString(R.string.pref_feed_show_reward),true));
        staticSettings.setPromoHeader(sharedPreferences.getString(mContext.getString(R.string.pref_feed_reward_topic),""));
        staticSettings.setPromoUrl(sharedPreferences.getString(mContext.getString(R.string.pref_feed_reward_url),""));

    }

    public AppSettings getSettings() {
        return settings;
    }

    public ThemeSettings getThemeSettings(){
        createThemeSettings();
        return themeSettings;
    }

    public AppStaticSettings getStaticSettings(){
        createAppStaticSettings();
        return staticSettings;
    }

    public boolean isFeedEventStyleLight(){
        return sharedPreferences.getBoolean(mContext.getString(R.string.pref_feed_event_text_is_light),true);

    }

    public String getFeedPublicRoom(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_feed_default_public_room),"");
    }


    public String getFeedReflectionDailyMorningFromServer(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_morning),"");
    }

    public String getFeedReflectionDailyMorningStartTime(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_morning_start), "0:00");
    }

    public String getFeedReflectionDailyMorningEndTime(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_morning_end), "12:00");
    }

    public String getFeedReflectionDailyEveningStartTime(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_evening_start), "12:00");
    }

    public String getFeedReflectionDailyEveningEndTime(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_evening_end), "23:59");
    }

    public FeedReflections getFeedReflectionDailyMorningLocal(){
        FeedReflections reflection = new FeedReflections();

        reflection.setSummary(sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_morning),""));
        reflection.setType(mActivity.getResources().getInteger(R.integer.FEED_MESSAGE_TYPE_REFLECTION_MORNING));

        boolean isComplete = sharedPreferences.getBoolean(mContext.getString(R.string.pref_feed_reflection_daily_morning_completed),false);
        reflection.setComplete(isComplete);

        if(isComplete){
            reflection.setAnswer(sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_morning_completed_value),""));
            reflection.setCompletedOn(sharedPreferences.getLong(mContext.getString(R.string.pref_feed_reflection_daily_morning_completed_on),0));
        }

        return reflection;

    }

    public String getFeedReflectionDailyEveningFromServer(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_evening),"");
    }

    public FeedReflections getFeedReflectionDailyEveningLocal(){
        FeedReflections reflection = new FeedReflections();

        reflection.setSummary(sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_evening),""));
        reflection.setType(mActivity.getResources().getInteger(R.integer.FEED_MESSAGE_TYPE_REFLECTION_EVENING));

        boolean isComplete = sharedPreferences.getBoolean(mContext.getString(R.string.pref_feed_reflection_daily_evening_completed),false);
        reflection.setComplete(isComplete);

        if(isComplete){
            reflection.setAnswer(sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_daily_evening_completed_value),""));
            reflection.setCompletedOn(sharedPreferences.getLong(mContext.getString(R.string.pref_feed_reflection_daily_evening_completed_on),0));
        }

        return reflection;

    }

    public String getFeedReflectionWeeklyFromServer(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_weekly_a),"");
    }

    public FeedReflections getFeedReflectionWeeklyLocal(){
        FeedReflections reflection = new FeedReflections();

        reflection.setSummary(sharedPreferences.getString(mContext.getString(R.string.pref_feed_reflection_weekly_a),""));
        reflection.setType(mActivity.getResources().getInteger(R.integer.FEED_MESSAGE_TYPE_REFLECTION_WEEKLY));

        boolean isComplete = sharedPreferences.getBoolean(mContext.getString(R.string.pref_feed_reflection_weekly_a_completed),false);
        reflection.setComplete(isComplete);

        Log.d(TAG, "to_delete: getFeedReflectionWeeklyLocal: Is Complete: " + isComplete);

        if(isComplete){
            reflection.setCompletedOn(sharedPreferences.getLong(mContext.getString(R.string.pref_feed_reflection_weekly_a_completed_on),0));
        }

        return reflection;

    }

    public void setFeedReflection(int type, FeedReflections reflection, boolean isForceSave){
        boolean isComplete;

        editor = sharedPreferences.edit();

        switch (type){
            case REFLECTION_MORNING:
                editor.putString(mContext.getString(R.string.pref_feed_reflection_daily_morning),reflection.getSummary());

                isComplete = reflection.isComplete();
                editor.putBoolean(mContext.getString(R.string.pref_feed_reflection_daily_morning_completed),reflection.isComplete());

                if(isComplete){
                    editor.putString(mContext.getString(R.string.pref_feed_reflection_daily_morning_completed_value),reflection.getAnswer());
                    editor.putLong(mContext.getString(R.string.pref_feed_reflection_daily_morning_completed_on),reflection.getCompletedOn());
                }

                break;

            case REFLECTION_EVENING:
                Log.d(TAG, "to_delete: setFeedReflection: In Preference Loader");
                editor.putString(mContext.getString(R.string.pref_feed_reflection_daily_evening),reflection.getSummary());

                isComplete = reflection.isComplete();
                editor.putBoolean(mContext.getString(R.string.pref_feed_reflection_daily_evening_completed),reflection.isComplete());

                Log.d(TAG, "to_delete: setFeedReflection: Is Complete: " + isComplete);

                if(isComplete){
                    editor.putLong(mContext.getString(R.string.pref_feed_reflection_daily_evening_completed_on),reflection.getCompletedOn());
                }
                break;

            case REFLECTION_WEEKLY:
                editor.putString(mContext.getString(R.string.pref_feed_reflection_weekly_a),reflection.getSummary());

                isComplete = reflection.isComplete();
                editor.putBoolean(mContext.getString(R.string.pref_feed_reflection_weekly_a_completed),reflection.isComplete());

                if(isComplete){
                    editor.putString(mContext.getString(R.string.pref_feed_reflection_weekly_a_completed_value),reflection.getAnswer());
                    editor.putLong(mContext.getString(R.string.pref_feed_reflection_weekly_a_completed_on),reflection.getCompletedOn());
                }
                break;
        }

        Log.d(TAG, "to_delete: setFeedReflection: Editor Complete, Saving");

        if(isForceSave){
            editor.commit();
            Log.d(TAG, "to_delete: setFeedReflection: Saved Now!");
        }else{
            editor.apply();
        }

        FeedReflections reflectDelete = getFeedReflectionDailyEveningLocal();

        Log.d(TAG, "to_delete: setFeedReflection: reflection is complete: " + reflectDelete.isComplete());

    }


    public void setAppFirstRun(boolean appFirstRun, boolean isForceSave) {
        settings.setFirstRun(appFirstRun);

        editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.pref_app_first_run),appFirstRun);
        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }

    }

    public void setAppDemo(boolean appDemo, boolean isForceSave, boolean addToSettings) {
        if(addToSettings){
            settings.setAppDemo(appDemo);
        }

        editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.pref_app_demo_mode),appDemo);
        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }

    }

    public void setFirebaseUserVerified(boolean isUserVerified, boolean isForceSave){
        settings.setUserVerified(isUserVerified);

        editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.pref_firebase_user_verified),isUserVerified);
        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }
    }

    public void setFirebaseUserSignedIn(boolean isUserSignedIn, boolean isForceSave){
        settings.setUserLoggedIn(isUserSignedIn);

        editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.pref_firebase_user_logged_in),isUserSignedIn);
        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }
    }

    public void setFirebaseUserID(String userID, boolean isForceSave){
        settings.setFirebaseUserID(userID);

        editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.pref_firebase_user_id),userID);
        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }
    }

    public void setFirebaseUserEmail(String userEmail, boolean isForceSave){
        settings.setFirebaseUserEmail(userEmail);

        editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.pref_firebase_user_email),userEmail);
        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }
    }

    public void setFirebaseUserPassword(String userPassword, boolean isForceSave){
        settings.setFirebaseUserPassword(userPassword);

        editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.pref_firebase_user_password),userPassword);
        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }
    }

    public void setFirebaseUserNameFirst(String userFirstName, boolean isForceSave){
        settings.setFirebaseUserNameFirst(userFirstName);

        editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.pref_firebase_user_name_first),userFirstName);
        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }
    }

    public void setFirebaseUserNameLast(String userLastName, boolean isForceSave){
        settings.setFirebaseUserNameLast(userLastName);

        editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.pref_firebase_user_name_last),userLastName);
        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }
    }

    public void setRemoteConfigCacheExpireTime(Long cacheExpireTime, boolean isForceSave, boolean addToSettings){
        if(addToSettings){
            settings.setAppRemoteConfigCacheExpireTime(cacheExpireTime);
        }

        editor = sharedPreferences.edit();
        editor.putLong(mContext.getString(R.string.pref_app_remote_config_cache_expire_time),cacheExpireTime);
        if(isForceSave){
            editor.commit();
        }else {
            editor.apply();
        }
    }

    public void setRemoteConfigThemeCompanyPages(int pageNo, String topUrl, String bottomUrl, boolean isForceSave){

        editor = sharedPreferences.edit();

        switch (pageNo){
            case 0:
                editor.putString(mContext.getString(R.string.theme_staff_company_top_page_0),topUrl);
                editor.putString(mContext.getString(R.string.theme_staff_company_bottom_page_0),bottomUrl);

                break;

            case 1:
                editor.putString(mContext.getString(R.string.theme_staff_company_top_page_1),topUrl);
                editor.putString(mContext.getString(R.string.theme_staff_company_bottom_page_1),bottomUrl);
                break;

            case 2:
                editor.putString(mContext.getString(R.string.theme_staff_company_top_page_2),topUrl);
                editor.putString(mContext.getString(R.string.theme_staff_company_bottom_page_2),bottomUrl);
                break;

        }
        if(isForceSave){
            editor.commit();
        }else {
            editor.apply();
        }
    }

    public void setRemoteConfigFeedDefaultPublicRoom(String defaultPublicRoom, boolean isForceSave){
        editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.pref_feed_default_public_room),defaultPublicRoom);

        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }

    }

    public void setRemoteConfigFeedReflections(@Nullable String daily_morning,
                                               @Nullable String daily_morning_start, @Nullable String daily_morning_end,
                                               @Nullable String daily_evening,
                                               @Nullable String daily_evening_start, @Nullable String daily_evening_end,
                                               @Nullable String weekly_a,
                                               boolean isForceSave){
        editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.pref_feed_reflection_daily_morning),daily_morning);
        editor.putString(mContext.getString(R.string.pref_feed_reflection_daily_morning_start),daily_morning_start);
        editor.putString(mContext.getString(R.string.pref_feed_reflection_daily_morning_end),daily_morning_end);

        editor.putString(mContext.getString(R.string.pref_feed_reflection_daily_evening),daily_evening);
        editor.putString(mContext.getString(R.string.pref_feed_reflection_daily_evening_start),daily_evening_start);
        editor.putString(mContext.getString(R.string.pref_feed_reflection_daily_evening_end),daily_evening_end);

        editor.putString(mContext.getString(R.string.pref_feed_reflection_weekly_a),weekly_a);

        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }

    }


    public void setRemoteConfigFeedStyle(boolean isEventThemeLight, boolean isForceSave){
        editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.pref_feed_event_text_is_light),isEventThemeLight);

        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }

    }

    public void setRemoteConfigAnnouncements(boolean isPromo, @Nullable String backgroundUrl, @Nullable String promoAnnoucement, boolean isForceSave){

        editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.pref_feed_reward),isPromo);

        if(isPromo){
            editor.putString(mContext.getString(R.string.pref_feed_reward_url),backgroundUrl);
            editor.putString(mContext.getString(R.string.pref_feed_reward_topic),promoAnnoucement);
        }

        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }

    }

    public void setAnnouncementShowReward(boolean isShow, boolean isForceSave){
        editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.pref_feed_show_reward),isShow);

        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }

    }

    public boolean getAnnouncementShowReward(){
        return sharedPreferences.getBoolean(mContext.getString(R.string.pref_feed_show_reward),true);
    }

    public void setUserFirebaseToken(String token, boolean isForceSave){
        editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.pref_firebase_user_message_token),token);

        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }
    }

    public String getUserFirebaseToken(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_firebase_user_message_token),null);
    }

    public String getFeedCurrentActiveRoom(){
        return sharedPreferences.getString(mContext.getString(R.string.pref_feed_current_active_room),"");
    }

    public void setCurrentActiveRoom(String room, boolean isForceSave){
        editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.pref_feed_current_active_room),room);

        if(isForceSave){
            editor.commit();
        }else{
            editor.apply();
        }
    }


}
