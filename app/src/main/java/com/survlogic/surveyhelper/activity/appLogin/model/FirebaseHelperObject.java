package com.survlogic.surveyhelper.activity.appLogin.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseHelperObject {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private String mExceptionTitle, mExceptionString;

    private String displayName, emailAddress;


    public FirebaseHelperObject() {}


    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public void setAuth(FirebaseAuth auth) {
        this.mAuth = auth;
    }


    public FirebaseUser getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.mCurrentUser = currentUser;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setExceptionTitle(String exceptionTitle) {
            this.mExceptionTitle = exceptionTitle;
        }

    public void setExceptionString(String exceptionString) {
        this.mExceptionString = exceptionString;
    }

    public String getExceptionTitle() {
        return mExceptionTitle;
    }

    public String getExceptionString() {
        return mExceptionString;
    }
}


