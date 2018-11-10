package com.survlogic.surveyhelper.activity.appLogin.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appLogin.inter.SignUpControllerListener;
import com.survlogic.surveyhelper.model.FirestoreAppAccessKeys;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.ArrayList;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class SignUpController implements AlertDialog.OnClickListener {

    private static final String TAG = "SignUpController";

    private Context mContext;
    private Activity mActivity;

    private SignUpControllerListener mSignUpControllerListener;

    private FirebaseAuth mAuth;
    private DocumentSnapshot mLastQueriedAccessKey;
    private ArrayList<FirestoreAppAccessKeys> mListKeys = new ArrayList<>();

    private PreferenceLoader preferenceLoader;
    private AppSettings settings;

    private  String mUserEmail, mUserPassword, mUserNameFirst, mUserNameLast, mAccessKey;
    private FirestoreAppAccessKeys mAppKeyLookup;
    private boolean mUserProfileUpdated = false;
    private boolean mGotUserKeysFromFireStore = false;

    private ProgressDialog mProgressDialog;

    public SignUpController(Context mContext, FirebaseAuth mAuth, SignUpControllerListener signUpControllerListener) {
        this.mContext = mContext;

        mActivity = (Activity) mContext;

        this.mAuth = mAuth;
        this.mSignUpControllerListener = signUpControllerListener;

        initController();

    }

    private void initController(){
        loadPreferences();


    }

    private void loadPreferences(){
        preferenceLoader = new PreferenceLoader(mContext);
        settings = preferenceLoader.getSettings();


    }

    public void onStart(){
        getAccessKeysFromFirestore();
    }


    public void setUserEmail(String userEmail) {
        this.mUserEmail = userEmail;
    }

    public void setUserPassword(String userPassword) {
        this.mUserPassword = userPassword;
    }

    public void setUserNameFirst(String userNameFirst) {
        this.mUserNameFirst = userNameFirst;
    }

    public void setUserNameLast(String userNameLast) {
        this.mUserNameLast = userNameLast;
    }

    public void setAccessKey(String accessKey) {
        this.mAccessKey = accessKey;
    }

    public void signUp(){
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(mUserEmail, mUserPassword)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUserInformation(user);

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.getException());

                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            mSignUpControllerListener.onFailureException(errorCode);

                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUserInformation(final FirebaseUser user){
        String mUserNameFull = mUserNameFirst + " " + mUserNameLast;

        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mUserNameFull)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mUserProfileUpdated = true;
                                createNewUserFirestore(user);
                            }
                        }
                    });
        }
    }

    private void createNewUserFirestore(FirebaseUser user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newUserRef = db
                .collection("users")
                .document(user.getUid());

        FirestoreUser fsUser = new FirestoreUser();
        fsUser.setDisplay_name(user.getDisplayName());
        fsUser.setUser_id(user.getUid());
        fsUser.setEmail(user.getEmail());
        fsUser.setAccess_level(mAccessKey);
        fsUser.setAccess_key_secure(mAppKeyLookup.getAccess_key_secure());
        fsUser.setFeed_posts(0);
        fsUser.setRewards_current(0);
        fsUser.setRewards_total(0);


        newUserRef.set(fsUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Logging User Information - Success");

                }else{
                    Log.d(TAG, "Logging User Information - Failed");

                }
            }
        });

    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DialogUtils.showToast(mActivity,mActivity.getResources().getString(R.string.firebase_email_verification_confirmation) + user.getEmail());

                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                DialogUtils.showToast(mActivity,mActivity.getResources().getString(R.string.firebase_email_error_verification));
                            }
                        }
                    });

        }

    }


    private void updateUI(FirebaseUser user) {
        String mEmailPasswordStatus, mFirebaseStatus;
        hideProgressDialog();

        if (user != null) {
            mEmailPasswordStatus = mActivity.getResources().getString(R.string.firebase_email_password_status_fmt,
                    user.getEmail(), user.isEmailVerified());
            mFirebaseStatus = mActivity.getResources().getString(R.string.firebase_status_fmt, user.getUid());

            Log.d(TAG, "Firebase: " + mEmailPasswordStatus + mFirebaseStatus);

            if(!user.isEmailVerified()){
                DialogUtils.showAlertDialog(mContext,mActivity.getResources().getString(R.string.firebase_email_verification_title), mActivity.getResources().getString(R.string.firebase_email_verification_summary),this);
                preferenceLoader.setFirebaseUserVerified(false,true);

            }else{
                DialogUtils.showCustomDialog_Login_Signed_in(mContext, 1500);

                preferenceLoader.setFirebaseUserVerified(true,true);
                preferenceLoader.setFirebaseUserSignedIn(true,true);

                preferenceLoader.setFirebaseUserID(user.getUid(),true);
                preferenceLoader.setFirebaseUserEmail(user.getEmail(),true);
                preferenceLoader.setFirebaseUserPassword(mUserPassword,true);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mSignUpControllerListener.closeActivity();
                    }
                },1600);

            }

        } else {
            mEmailPasswordStatus = mActivity.getResources().getString(R.string.general_signedOut);
            Log.d(TAG, "Firebase: " + mEmailPasswordStatus);

            preferenceLoader.setFirebaseUserVerified(false,true);
            preferenceLoader.setFirebaseUserSignedIn(false,true);

            preferenceLoader.setFirebaseUserID(null,true);
            preferenceLoader.setFirebaseUserEmail(null,true);
            preferenceLoader.setFirebaseUserPassword(null,true);

        }

    }


    private boolean validateForm(){
        boolean valid = true;

        if (TextUtils.isEmpty(mUserEmail)) {
            mSignUpControllerListener.setUserEmailErrorNull(true);
            valid = false;
        } else {
            mSignUpControllerListener.setUserEmailErrorNull(false);
        }

        if (TextUtils.isEmpty(mUserPassword)) {
            mSignUpControllerListener.setPasswordErrorNull(true);
            valid = false;
        } else {
            mSignUpControllerListener.setPasswordErrorNull(false);
        }

        if (TextUtils.isEmpty(mUserNameFirst)) {
            mSignUpControllerListener.setUserNameFirstErrorNull(true);
            valid = false;
        } else {
            mSignUpControllerListener.setUserNameFirstErrorNull(false);
        }

        if (TextUtils.isEmpty(mUserNameLast)) {
            mSignUpControllerListener.setUserNameLastErrorNull(true);
            valid = false;
        } else {
            mSignUpControllerListener.setUserNameLastErrorNull(false);
        }

        if (TextUtils.isEmpty(mAccessKey)) {
            mSignUpControllerListener.setAccessKeyErrorNull(true);
            valid = false;
        } else {
            mSignUpControllerListener.setAccessKeyErrorNull(false);
        }

        if(!validateKeyCode()){
            valid = false;
        }

        return valid;

    }

    private boolean validateKeyCode(){
        //check access validation here!
        ArrayList<String> listKeysFromObject = new ArrayList<>();

        for (FirestoreAppAccessKeys appKey : mListKeys) {
            listKeysFromObject.add(appKey.getAccess_key_id());
        }

        boolean ans = listKeysFromObject.contains(mAccessKey);

        if(!ans){
            mSignUpControllerListener.setAccessKeyErrorWrongKey(true);
        }else{
            mSignUpControllerListener.setAccessKeyErrorWrongKey(false);

            int index = listKeysFromObject.indexOf(mAccessKey);
            mAppKeyLookup = new FirestoreAppAccessKeys(mListKeys.get(index));

        }

        return ans;
    }

    private void getAccessKeysFromFirestore(){
        Log.d(TAG, "getAccessKeys: Started");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference accessKeysRef = db.collection("app_access_keys");

        Query accessKeysQuery = null;
        if(mLastQueriedAccessKey !=null){
            accessKeysQuery = accessKeysRef
                    .startAfter(mLastQueriedAccessKey);
        }else{
            accessKeysQuery = accessKeysRef;

        }

        accessKeysQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        FirestoreAppAccessKeys appAccessKeys = document.toObject(FirestoreAppAccessKeys.class);
                        mListKeys.add(appAccessKeys);
                    }

                    if(task.getResult().size() !=0){
                        mLastQueriedAccessKey = task.getResult().getDocuments()
                                .get(task.getResult().size() -1);
                    }
                    Log.d(TAG, "getAccessKeys-Success");
                    mGotUserKeysFromFireStore = true;

                }else{
                    Log.d(TAG, "getAccessKeys-Failure");
                    String errorTitle  = mActivity.getResources().getString(R.string.appLogin_sign_in_error_access_key_no_cloud_title);
                    String errorMessage = mActivity.getResources().getString(R.string.appLogin_sign_in_error_access_key_no_cloud_value);
                    DialogUtils.showAlertDialog(mActivity,errorTitle,errorMessage);
                }
            }
        });

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mActivity.getResources().getString(R.string.general_loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                sendEmailVerification();
                mSignUpControllerListener.refreshUI();
                break;
        }
    }

}
