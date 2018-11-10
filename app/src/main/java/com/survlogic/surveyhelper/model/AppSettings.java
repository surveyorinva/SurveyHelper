package com.survlogic.surveyhelper.model;

public class AppSettings {


    private AppSettings settings;
    private boolean isAppFirstRun = true;
    private boolean isAppDemo = false;

    private long appRemoteConfigCacheExpireTime;

    //firebase
    private boolean isUserVerified = false;
    private boolean isUserLoggedIn = false;
    private String firebaseUserID, firebaseUserEmail, firebaseUserPassword;
    private String firebaseUserNameFirst, firebaseUserNameLast;
    private String firebaseAccessKey, firebaseAccessLevel, firebaseAccessKeySecure;

    public AppSettings() {}


    public AppSettings(AppSettings settings) {
        this.settings = settings;

        setFirstRun(settings.isAppFirstRun());
        setAppDemo(settings.isAppDemo());

        setAppRemoteConfigCacheExpireTime(settings.appRemoteConfigCacheExpireTime);

        setUserLoggedIn(settings.isUserLoggedIn);
        setUserVerified(settings.isUserVerified);
        setFirebaseUserNameFirst(settings.firebaseUserNameFirst);
        setFirebaseUserNameLast(settings.firebaseUserNameLast);
        setFirebaseUserID(settings.firebaseUserID);
        setFirebaseUserEmail(settings.firebaseUserEmail);
        setFirebaseUserPassword(settings.firebaseUserPassword);
        setFirebaseAccessKey(settings.firebaseAccessKey);
        setFirebaseAccessLevel(settings.firebaseAccessLevel);
        setFirebaseAccessKeySecure(settings.firebaseAccessKeySecure);

    }

    public boolean isAppFirstRun() {
        return isAppFirstRun;
    }

    public void setFirstRun(boolean firstRun) {
        isAppFirstRun = firstRun;
    }

    public boolean isAppDemo() {
        return isAppDemo;
    }

    public void setAppDemo(boolean appDemo) {
        isAppDemo = appDemo;
    }

    public boolean isUserVerified() {
        return isUserVerified;
    }

    public void setUserVerified(boolean userVerified) {
        isUserVerified = userVerified;
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        isUserLoggedIn = userLoggedIn;
    }

    public String getFirebaseUserID() {
        return firebaseUserID;
    }

    public void setFirebaseUserID(String firebaseUserID) {
        this.firebaseUserID = firebaseUserID;
    }

    public String getFirebaseUserEmail() {
        return firebaseUserEmail;
    }

    public void setFirebaseUserEmail(String firebaseUserEmail) {
        this.firebaseUserEmail = firebaseUserEmail;
    }

    public String getFirebaseUserPassword() {
        return firebaseUserPassword;
    }

    public void setFirebaseUserPassword(String firebaseUserPassword) {
        this.firebaseUserPassword = firebaseUserPassword;
    }

    public String getFirebaseUserNameFirst() {
        return firebaseUserNameFirst;
    }

    public void setFirebaseUserNameFirst(String firebaseUserNameFirst) {
        this.firebaseUserNameFirst = firebaseUserNameFirst;
    }

    public String getFirebaseUserNameLast() {
        return firebaseUserNameLast;
    }

    public void setFirebaseUserNameLast(String firebaseUserNameLast) {
        this.firebaseUserNameLast = firebaseUserNameLast;
    }

    public String getFirebaseAccessKey() {
        return firebaseAccessKey;
    }

    public void setFirebaseAccessKey(String firebaseAccessKey) {
        this.firebaseAccessKey = firebaseAccessKey;
    }

    public String getFirebaseAccessLevel() {
        return firebaseAccessLevel;
    }

    public void setFirebaseAccessLevel(String firebaseAccessLevel) {
        this.firebaseAccessLevel = firebaseAccessLevel;
    }

    public String getFirebaseAccessKeySecure() {
        return firebaseAccessKeySecure;
    }

    public void setFirebaseAccessKeySecure(String firebaseAccessKeySecure) {
        this.firebaseAccessKeySecure = firebaseAccessKeySecure;
    }

    public long getAppRemoteConfigCacheExpireTime() {
        return appRemoteConfigCacheExpireTime;
    }

    public void setAppRemoteConfigCacheExpireTime(long appRemoteConfigTalkWithServer) {
        this.appRemoteConfigCacheExpireTime = appRemoteConfigTalkWithServer;
    }
}
