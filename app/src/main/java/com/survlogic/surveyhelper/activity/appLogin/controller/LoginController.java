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
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appLogin.inter.SignInControllerListener;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class LoginController implements AlertDialog.OnClickListener {
    private static final String TAG = "LoginController";

    private Context mContext;
    private Activity mActivity;
    private SignInControllerListener mSignInControllerListener;

    private FirebaseAuth mAuth;

    private PreferenceLoader preferenceLoader;
    private AppSettings settings;

    private  String mUserEmail, mUserPassword;

    private ProgressDialog mProgressDialog;

    public LoginController(Context mContext, FirebaseAuth mAuth, SignInControllerListener mSignInControllerListener) {
        this.mContext = mContext;
        mActivity = (Activity) mContext;

        this.mAuth = mAuth;
        this.mSignInControllerListener = mSignInControllerListener;

        initController();
    }

    private void initController(){
        loadPreferences();


    }

    private void loadPreferences(){
        preferenceLoader = new PreferenceLoader(mContext);
        settings = preferenceLoader.getSettings();

    }


    public void setUserEmail(String userName) {
        this.mUserEmail = userName;
    }

    public void setUserPassword(String userPassword) {
        this.mUserPassword = userPassword;
    }

    public void onStart(){


    }

    public void signIn(){
        Log.d(TAG, "Started Sign In Process");
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(mUserEmail, mUserPassword)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                            mSignInControllerListener.onFailureException(errorCode);

                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void updateUI(FirebaseUser user){
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


                //todo potentially problematic if not receiving user data
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mSignInControllerListener.closeActivity();
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

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
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


    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(mUserEmail)) {
            mSignInControllerListener.setUserEmailErrorNull(true);
            valid = false;
        } else {
            mSignInControllerListener.setUserEmailErrorNull(false);
        }

        if (TextUtils.isEmpty(mUserPassword)) {
            mSignInControllerListener.setPasswordErrorNull(true);
            valid = false;
        } else {
            mSignInControllerListener.setPasswordErrorNull(false);
        }

        return valid;
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
                break;
        }
    }
}
