package com.survlogic.surveyhelper.activity.appLogin.helper;

import android.app.Activity;
import android.content.Context;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appLogin.model.FirebaseHelperObject;

public class FirebaseErrorHelper {

    private static final String TAG = "FirebaseErrorHelper";

    private Context mContext;
    private Activity mActivity;


    public FirebaseErrorHelper(Context context) {
        this.mContext = context;
        this.mActivity = (Activity) context;
    }



    public FirebaseHelperObject getExceptionMessage(String errorCode){
        String errorMessage, errorTitle;
        FirebaseHelperObject results= new FirebaseHelperObject();

        switch (errorCode){
            case "ERROR_INVALID_CUSTOM_TOKEN":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_INVALID_CUSTOM_TOKEN);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_token);
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_CUSTOM_TOKEN_MISMATCH);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_token);
                break;

            case "ERROR_INVALID_CREDENTIAL":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_INVALID_CREDENTIAL);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_credential);
                break;

            case "ERROR_INVALID_EMAIL":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_INVALID_EMAIL);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_email);
                break;

            case "ERROR_WRONG_PASSWORD":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_WRONG_PASSWORD);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_password);
                break;

            case "ERROR_USER_MISMATCH":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_USER_MISMATCH);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_sign_in);
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_REQUIRES_RECENT_LOGIN);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_sign_in);
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_sign_in);
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_EMAIL_ALREADY_IN_USE);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_sign_up);
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_CREDENTIAL_ALREADY_IN_USE);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_sign_up);
                break;

            case "ERROR_USER_DISABLED":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_USER_DISABLED);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_sign_in);
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_USER_TOKEN_EXPIRED);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_session_time_out);
                break;

            case "ERROR_USER_NOT_FOUND":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_USER_NOT_FOUND);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_sign_in);
                break;

            case "ERROR_INVALID_USER_TOKEN":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_INVALID_USER_TOKEN);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_session_time_out);
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_OPERATION_NOT_ALLOWED);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_default);
                break;

            case "ERROR_WEAK_PASSWORD":
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_ERROR_WEAK_PASSWORD);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_sign_in);
                break;

            default:
                errorMessage = mActivity.getResources().getString(R.string.firebase_exception_subject_default);
                errorTitle = mActivity.getResources().getString(R.string.firebase_exception_title_default);
                break;
        }

        results.setExceptionTitle(errorTitle);
        results.setExceptionString(errorMessage);

        return results;

    }



}
