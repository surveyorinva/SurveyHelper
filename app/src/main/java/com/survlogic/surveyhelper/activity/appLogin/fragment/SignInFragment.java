package com.survlogic.surveyhelper.activity.appLogin.fragment;

import android.content.Intent;
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
import com.survlogic.surveyhelper.activity.appLogin.ForgetPasswordActivity;
import com.survlogic.surveyhelper.activity.appLogin.controller.LoginController;
import com.survlogic.surveyhelper.activity.appLogin.helper.FirebaseErrorHelper;
import com.survlogic.surveyhelper.activity.appLogin.inter.LoginActivityListener;
import com.survlogic.surveyhelper.activity.appLogin.inter.SignInControllerListener;
import com.survlogic.surveyhelper.activity.appLogin.model.FirebaseHelperObject;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.KeyboardUtils;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class SignInFragment extends Fragment implements SignInControllerListener {

    private View v;

    private LoginController mLoginController;
    private LoginActivityListener mLoginActivityListener;

    private FirebaseAuth mAuth;

    private Button btSignIn, btForgotPassword;

    private TextFieldBoxes tfUserEmail, tfPassword;
    private ExtendedEditText etUserEmail, etPassword;

    private boolean isPasswordShown = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.login_fragment_portal_sign_in, container, false);

        initView();
        setOnClickListeners();
        setIMEEventListeners();

        mAuth = FirebaseAuth.getInstance();
        mLoginController = new LoginController(getActivity(), mAuth,this);
        mLoginActivityListener = (LoginActivityListener) getActivity();

        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
            mLoginController.onStart();
    }


    private void initView(){

        btSignIn = v.findViewById(R.id.btn_signin_action);
        btForgotPassword = v.findViewById(R.id.btn_signin_forgot_password);

        tfUserEmail = v.findViewById(R.id.signIn_email);
        tfPassword = v.findViewById(R.id.signIn_password);

        etUserEmail = v.findViewById(R.id.signIn_email_value);
        etPassword = v.findViewById(R.id.signIn_password_value);


    }

    private void setOnClickListeners(){
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupForSignIn();
            }
        });

        btForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForgetPasswordActivity.class);
                startActivity(intent);
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
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setupForSignIn();

                    KeyboardUtils.hideKeyboard(etPassword);
                    handled = true;
                }
                return handled;
            }
        });

    }


    private void setupForSignIn(){
        String mUserName = etUserEmail.getText().toString();
        String mPassword = etPassword.getText().toString();

        mLoginController.setUserEmail(mUserName);
        mLoginController.setUserPassword(mPassword);

        mLoginController.signIn();

    }

    @Override
    public void refreshUI() {

    }

    @Override
    public void closeActivity() {
        mLoginActivityListener.returnResults(true);
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
    public void onFailureException(String errorCode) {
        FirebaseErrorHelper firebaseErrorHelper = new FirebaseErrorHelper(getContext());
        FirebaseHelperObject firebaseException = firebaseErrorHelper.getExceptionMessage(errorCode);

        DialogUtils.showAlertDialog(getActivity(), firebaseException.getExceptionTitle(),firebaseException.getExceptionString());


    }

    @Override
    public void setViewsErrorAuthenticationFail(boolean isFail) {
        if(isFail){
            tfUserEmail.setError(getResources().getString(R.string.appLogin_sign_in_error_authentication_fail),false);
            tfPassword.setError(getResources().getString(R.string.appLogin_sign_in_error_authentication_fail),false);

        }
    }
}
