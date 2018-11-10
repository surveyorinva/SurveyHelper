package com.survlogic.surveyhelper.activity.appLogin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appLogin.helper.FirebaseErrorHelper;
import com.survlogic.surveyhelper.activity.appLogin.model.FirebaseHelperObject;
import com.survlogic.surveyhelper.utils.DialogUtils;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class ForgetPasswordActivity extends AppCompatActivity implements AlertDialog.OnClickListener{

    private FirebaseAuth mAuth;
    private Context mContext;

    private ImageButton ibAppBarBack;
    private Button btForgotPassword;

    private ProgressBar pbStatus;
    private TextFieldBoxes tfUserEmail;
    private ExtendedEditText etUserEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_reset_email);


        mContext = ForgetPasswordActivity.this;
        mAuth = FirebaseAuth.getInstance();


        initView();
        setOnClickListeners();

    }

    private void initView(){

        ibAppBarBack = findViewById(R.id.appBar_top_action_nav_back);
        ibAppBarBack.setVisibility(View.VISIBLE);

        btForgotPassword  = findViewById(R.id.btn_forgot_password_action);

        tfUserEmail = findViewById(R.id.signIn_email);
        etUserEmail = findViewById(R.id.signIn_email_value);

        pbStatus = findViewById(R.id.app_bar_progress_bar);

    }

    private void setOnClickListeners(){

        ibAppBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForgotPassword();
            }
        });

    }

    private void sendForgotPassword(){
        String email = etUserEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            tfUserEmail.setError(getResources().getString(R.string.appLogin_sign_in_error_user_email_blank),false);
            return;
        }

        pbStatus.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DialogUtils.showAlertDialog(mContext,getResources().getString(R.string.appLogin_forgot_password_status_success_title), getResources().getString(R.string.appLogin_forgot_password_status_success_summary),ForgetPasswordActivity.this);


                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                            FirebaseErrorHelper firebaseErrorHelper = new FirebaseErrorHelper(mContext);
                            FirebaseHelperObject firebaseException = firebaseErrorHelper.getExceptionMessage(errorCode);

                            DialogUtils.showAlertDialog(ForgetPasswordActivity.this, firebaseException.getExceptionTitle(),firebaseException.getExceptionString());
                        }

                        pbStatus.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                finish();
                break;
        }
    }
}
