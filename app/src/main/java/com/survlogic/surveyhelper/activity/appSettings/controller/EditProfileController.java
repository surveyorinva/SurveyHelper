package com.survlogic.surveyhelper.activity.appSettings.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.survlogic.surveyhelper.activity.appSettings.inter.EditProfileListener;
import com.survlogic.surveyhelper.database.Users.FirestoreDatabaseUser;
import com.survlogic.surveyhelper.database.Users.FirestoreDatabaseUserListener;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.Calendar;
import java.util.Date;

public class EditProfileController implements FirestoreDatabaseUserListener {

    private static final String TAG = "EditProfileController";
    private Context mContext;
    private Activity mActivity;

    private FirebaseUser mCurrentUser;
    private FirestoreUser mFirestoreUser;

    private EditProfileListener mViewListener;
    private PreferenceLoader preferenceLoader;

    private  String mUserDisplayName;
    private long mUserContactPhoneOffice, mUserContactPhoneMobile;
    private Date mUserBirthday;


    public EditProfileController(Context context, EditProfileListener profileListener) {
        Log.d(TAG, "------------------EditProfileController Started---------------------");
        this.mContext = context;
        this.mActivity = (Activity) context;
        this.mViewListener = profileListener;

        initController();

    }

    public void setUserDisplayName(String displayName) {
        this.mUserDisplayName = displayName;
        mFirestoreUser.setDisplay_name(displayName);
    }

    public void setUserContactPhoneOffice(long userContactPhoneOffice) {
        this.mUserContactPhoneOffice = userContactPhoneOffice;
        mFirestoreUser.setTelephone_office(userContactPhoneOffice);
    }

    public void setUserContactPhoneMobile(long userContactPhoneMobile) {
        this.mUserContactPhoneMobile = userContactPhoneMobile;
        mFirestoreUser.setTelephone_mobile(userContactPhoneMobile);
    }

    public void setUserBirthday(Date userBirthday) {
        this.mUserBirthday = userBirthday;
        mFirestoreUser.setProfile_birthday(userBirthday);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(userBirthday);
        long dayOfYear = cal1.get(Calendar.DAY_OF_YEAR);

        mFirestoreUser.setProfile_birthday_long(dayOfYear);

    }

    private void initController(){
        loadPreferences();

    }

    private void loadPreferences(){
        preferenceLoader = new PreferenceLoader(mContext);

    }

    public void onStart(FirestoreUser firestoreUser){
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestoreUser = firestoreUser;

    }


    public void onSaveFirestoreUser(){
        Log.d(TAG, "-------------Saving FirestoreUser Started");
        editUserProfile();


    }

    /**
     * Firestore
     */

    private void editUserProfile(){
        final FirestoreDatabaseUser dbUser = new FirestoreDatabaseUser(mContext,this);
        dbUser.updateUserProfileMetadataToFirestore(mFirestoreUser);
    }


    @Override
    public void returnFirestoreUser(FirestoreUser user) {
        //User has been updated, return to Profile screen
        Log.d(TAG, "returnFirestoreUser: Success");
        
    }

    @Override
    public void returnFirestoreUserGetError(boolean isError) {
        Log.d(TAG, "returnFirestoreUserGetError: Error");
    }

    @Override
    public void updateUserProfileSuccess(FirestoreUser user) {
        Log.d(TAG, "updateUserProfileSuccess: Success");
        mViewListener.finishActivity();
    }

    @Override
    public void updateUserProfileGetError(boolean isError) {
        Log.d(TAG, "updateUserProfileGetError: Error");

    }
}
