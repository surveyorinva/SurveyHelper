package com.survlogic.surveyhelper.activity.appLogin.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appLogin.controller.SignUpController;
import com.survlogic.surveyhelper.activity.appLogin.helper.FirebaseErrorHelper;
import com.survlogic.surveyhelper.activity.appLogin.inter.LoginActivityListener;
import com.survlogic.surveyhelper.activity.appLogin.inter.SignUpControllerListener;
import com.survlogic.surveyhelper.activity.appLogin.model.FirebaseHelperObject;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.KeyboardUtils;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class SignUpFragment extends Fragment implements SignUpControllerListener {

    private static final String TAG = "SignUpFragment";

    private View v;
    SignUpController mSignUpController;
    private LoginActivityListener mLoginActivityListener;

    private FirebaseAuth mAuth;

    private Button btSignUp;

    private TextFieldBoxes tfUserEmail, tfPassword, tfUserNameFirst, tfUserNameLast, tfAccessKey;
    private ExtendedEditText etUserEmail, etPassword, etUserNameFirst, etUserNameLast, etAccessKey;

    private boolean isPasswordShown = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.login_fragment_portal_sign_up, container, false);

        initView();
        setOnClickListeners();
        setIMEEventListeners();

        mAuth = FirebaseAuth.getInstance();
        mSignUpController = new SignUpController(getActivity(), mAuth,this);
        mLoginActivityListener = (LoginActivityListener) getActivity();

        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
        mSignUpController.onStart();
    }


    private void initView(){

        btSignUp = v.findViewById(R.id.btn_signUp_action);

        tfUserEmail = v.findViewById(R.id.signIn_email);
        tfPassword = v.findViewById(R.id.signIn_password);
        tfUserNameFirst = v.findViewById(R.id.signIn_user_name_first);
        tfUserNameLast = v.findViewById(R.id.signIn_user_name_last);
        tfAccessKey = v.findViewById(R.id.signIn_access_key);

        etUserEmail = v.findViewById(R.id.signIn_email_value);
        etPassword = v.findViewById(R.id.signIn_password_value);
        etUserNameFirst = v.findViewById(R.id.signIn_user_name_first_value);
        etUserNameLast = v.findViewById(R.id.signIn_user_name_last_value);
        etAccessKey = v.findViewById(R.id.signIn_access_key_value);


    }

    private void setOnClickListeners(){
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupForSignUp();
            }
        });

        tfPassword.getEndIconImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordShown){
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    tfPassword.setEndIcon(getResources().getDrawable(R.drawable.ic_visibility_dark_24dp));

                    isPasswordShown = false;
                }else{
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    tfPassword.setEndIcon(getResources().getDrawable(R.drawable.ic_visibility_off_dark_24dp));

                    isPasswordShown = true;
                }
            }
        });


    }

    private void setIMEEventListeners(){
        etAccessKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setupForSignUp();

                    KeyboardUtils.hideKeyboard(etAccessKey);
                    handled = true;
                }
                return handled;
            }
        });

    }


    private void setupForSignUp(){
        String mUserName = etUserEmail.getText().toString();
        String mPassword = etPassword.getText().toString();
        String mUserNameFirst = etUserNameFirst.getText().toString();
        String mUserNameLast = etUserNameLast.getText().toString();
        String mAccessKey = etAccessKey.getText().toString();

        mSignUpController.setUserEmail(mUserName);
        mSignUpController.setUserPassword(mPassword);
        mSignUpController.setUserNameFirst(mUserNameFirst);
        mSignUpController.setUserNameLast(mUserNameLast);
        mSignUpController.setAccessKey(mAccessKey);

        mSignUpController.signUp();
    }


    @Override
    public void refreshUI() {
        mLoginActivityListener.recreateActivity();
    }

    @Override
    public void closeActivity() {

    }

    @Override
    public void setUserNameFirstErrorNull(boolean isNull) {
        if(isNull){
            tfUserNameFirst.setError(getResources().getString(R.string.appLogin_sign_in_error_user_first_blank),false);
        }
    }

    @Override
    public void setUserNameLastErrorNull(boolean isNull) {
        if(isNull){
            tfUserNameLast.setError(getResources().getString(R.string.appLogin_sign_in_error_user_last_blank),false);
        }
    }

    @Override
    public void setUserEmailErrorNull(boolean isNull) {
        if(isNull){
            tfUserEmail.setError(getResources().getString(R.string.appLogin_sign_in_error_user_email_blank),false);
        }
    }

    @Override
    public void setPasswordErrorNull(boolean isNull) {
        if(isNull){
            tfPassword.setError(getResources().getString(R.string.appLogin_sign_in_error_password_blank),false);
        }
    }

    @Override
    public void setAccessKeyErrorNull(boolean isNull) {
        if(isNull){
            tfAccessKey.setError(getResources().getString(R.string.appLogin_sign_in_error_access_key_blank),false);
        }
    }

    @Override
    public void setAccessKeyErrorWrongKey(boolean isNull) {
        if(isNull){
            tfAccessKey.setError(getResources().getString(R.string.appLogin_sign_in_error_access_key_wrong),false);
        }
    }

    @Override
    public void onFailureException(String errorCode) {
        FirebaseErrorHelper firebaseErrorHelper = new FirebaseErrorHelper(getContext());
        FirebaseHelperObject firebaseException = firebaseErrorHelper.getExceptionMessage(errorCode);

        DialogUtils.showAlertDialog(getActivity(), firebaseException.getExceptionTitle(),firebaseException.getExceptionString());
    }

    @Override
    public void setViewsErrorAuthenticationFail(boolean isFail) {

    }
}