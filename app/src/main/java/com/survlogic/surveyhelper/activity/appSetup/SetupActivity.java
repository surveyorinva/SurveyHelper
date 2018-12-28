package com.survlogic.surveyhelper.activity.appSetup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appLogin.LoginActivity;
import com.survlogic.surveyhelper.activity.appSettings.SettingsActivity;
import com.survlogic.surveyhelper.activity.appSetup.controller.SetupStepController;
import com.survlogic.surveyhelper.activity.appSetup.inter.SetupListener;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;

public class SetupActivity extends AppCompatActivity implements SetupListener {
    private static final String TAG = "SetupActivity";
    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private SetupStepController setupStepController;
    private VerticalStepperFormLayout verticalStepperForm;

    private TextView tvAppBarTitle;

    private static final int INTENT_REQUEST_FIREBASE_LOGIN = 1000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity_main);

        mContext = SetupActivity.this;
        mAuth = FirebaseAuth.getInstance();

        initStepper();

    }


    @Override
    protected void onStart() {
        super.onStart();

        mCurrentUser = mAuth.getCurrentUser();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_REQUEST_FIREBASE_LOGIN && resultCode == RESULT_OK){
            boolean key_login_successful = data.getBooleanExtra(getResources().getString(R.string.KEY_LOGIN_SUCCESSFUL),false);
            if(key_login_successful){
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                setupStepController.setFirebaseCredentials(mAuth, mCurrentUser);

            }

        }

    }

    private void initStepper(){
        tvAppBarTitle = findViewById(R.id.appBar_top_title);
        tvAppBarTitle.setText(getResources().getString(R.string.app_startup_title));
        tvAppBarTitle.setVisibility(View.VISIBLE);


        verticalStepperForm = findViewById(R.id.vertical_stepper_form);
        setupStepController = new SetupStepController(mContext,verticalStepperForm,this);


    }


    @Override
    public void startLoginActivityForResults() {
        Intent i = new Intent(mContext, LoginActivity.class);
        startActivityForResult(i,INTENT_REQUEST_FIREBASE_LOGIN);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void startSettingsActivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra(getString(R.string.KEY_PARENT_ACTIVITY),getString(R.string.CLASS_SETUP));
        startActivity(i);

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
