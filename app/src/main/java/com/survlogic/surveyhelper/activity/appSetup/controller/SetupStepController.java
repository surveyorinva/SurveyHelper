package com.survlogic.surveyhelper.activity.appSetup.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.survlogic.surveyhelper.MainActivity;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appSettings.AboutAppActivity;
import com.survlogic.surveyhelper.activity.appSetup.inter.SetupListener;
import com.survlogic.surveyhelper.database.Users.FirestoreDatabaseUser;
import com.survlogic.surveyhelper.database.Users.FirestoreDatabaseUserListener;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class SetupStepController implements VerticalStepperForm, FirestoreDatabaseUserListener {

    private static final String TAG = "SetupStepController";
    private Context mContext;
    private Activity mActivity;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private SetupListener mSetupListener;

    private VerticalStepperFormLayout verticalStepperForm;
    private LabeledSwitch swtTerms;

    //Login Content
    private RelativeLayout rlPreLogin, rlPostLogin;
    private Button btnGoToLoginActivity, btnCurrentUserLoggedIn;
    private TextView tvUserDisplayName;

    private boolean mIsTermsApproved = false, mIsUserLoggedIn = false;

    public SetupStepController(Context mContext, VerticalStepperFormLayout verticalStepperForm, SetupListener setupListener) {
        this.mContext = mContext;
        mActivity = (Activity) mContext;

        this.verticalStepperForm = verticalStepperForm;

        this.mSetupListener = setupListener;

        init();
    }

    private void init(){
        String[] stepsTitles = mActivity.getResources().getStringArray(R.array.setup_steps_subtitles);
        int colorPrimary = ContextCompat.getColor(mContext.getApplicationContext(), R.color.colorPrimaryNormal);
        int colorPrimaryDark = ContextCompat.getColor(mContext.getApplicationContext(), R.color.colorPrimaryDark);

        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, stepsTitles, this, mActivity)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .showVerticalLineWhenStepsAreCollapsed(true)
                .displayBottomNavigation(false)
                .init();

    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case 0:
                view = createTermsOfUseStep();
                break;
            case 1:
                view = createLoginStep();
                break;
            case 2:
                view = createSetupStep();

        }
        return view;
    }

    private View createTermsOfUseStep() {
        LayoutInflater inflater = LayoutInflater.from(mActivity.getBaseContext());
        LinearLayout layoutContent = (LinearLayout) inflater.inflate(R.layout.setup_content_step_terms, null, false);
        //Widget here
        swtTerms = layoutContent.findViewById(R.id.switch_terms_agree);
        swtTerms.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                mIsTermsApproved = isOn;
                checkTerms();
            }
        });

        Button btnShowTermsOfUse = layoutContent.findViewById(R.id.btn_view_terms_of_use);
        btnShowTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialogTitle = mActivity.getResources().getString(R.string.appSetup_terms_dialog_header);
                String dialogSummary = mActivity.getResources().getString(R.string.app_terms_of_use);
                DialogUtils.showAlertDialog(mContext,dialogTitle,dialogSummary);
            }
        });


        return layoutContent;
    }

    private View createLoginStep() {
        LayoutInflater inflater = LayoutInflater.from(mActivity.getBaseContext());
        LinearLayout layoutContent = (LinearLayout) inflater.inflate(R.layout.setup_content_step_login, null, false);
        //Widget here
        rlPreLogin = layoutContent.findViewById(R.id.rl_view_go_to_login_action_pre_login);

        btnGoToLoginActivity = layoutContent.findViewById(R.id.btn_view_go_to_login_activity);
        btnGoToLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mSetupListener.startLoginActivityForResults();

            }
        });

        rlPostLogin = layoutContent.findViewById(R.id.rl_view_go_to_login_action_post_login);

        btnCurrentUserLoggedIn = layoutContent.findViewById(R.id.btn_view_user_logged_in);
        btnCurrentUserLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo

            }
        });

        tvUserDisplayName = layoutContent.findViewById(R.id.lbl_view_go_to_login_welcome_value);

        return layoutContent;
    }

    private View createSetupStep(){
        LayoutInflater inflater = LayoutInflater.from(mActivity.getBaseContext());
        LinearLayout layoutContent = (LinearLayout) inflater.inflate(R.layout.setup_content_step_settings, null, false);

        Button btnGoToSettingsActivity = layoutContent.findViewById(R.id.btn_view_go_to_settings_activity);
        btnGoToSettingsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetupListener.startSettingsActivity();
            }
        });

        return layoutContent;
    }



    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                checkTerms();
                break;
            case 1:
                checkLogin();
                break;
            case 2:
                checkSettings();
        }
    }


    private void checkTerms(){
        if(mIsTermsApproved){
            verticalStepperForm.setActiveStepAsCompleted();
        }

    }

    private void checkLogin(){
        if(mIsUserLoggedIn){
            verticalStepperForm.setActiveStepAsCompleted();
        }

    }

    private void checkSettings(){
        verticalStepperForm.setStepAsCompleted(2);
    }


    @Override
    public void sendData() {
        PreferenceLoader mPrefenceLoader = new PreferenceLoader(mContext);
        mPrefenceLoader.setAppFirstRun(false, true);

        startHomeActivity();

    }


    private void startHomeActivity(){
        Intent i = new Intent(mActivity, MainActivity.class);
        i.putExtra(mActivity.getResources().getString(R.string.KEY_PARENT_ACTIVITY),mActivity.getResources().getString(R.string.CLASS_SETUP));
        mActivity.startActivity(i);
        mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        mActivity.finish();

    }

    private void visibilityControlLoginButtons(){
        if(mIsUserLoggedIn){
            rlPreLogin.setVisibility(View.GONE);
            rlPostLogin.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Firestore Methods
     */

    public void setFirebaseCredentials(FirebaseAuth auth, FirebaseUser currentUser){
        this.mAuth = auth;
        this.mCurrentUser = currentUser;

        mIsUserLoggedIn = true;
        visibilityControlLoginButtons();

        FirestoreDatabaseUser dbUser = new FirestoreDatabaseUser(mContext,this);
        dbUser.getUserDataFromFirestore(currentUser);

    }


    @Override
    public void returnFirestoreUser(FirestoreUser user) {
        tvUserDisplayName.setText(user.getDisplay_name());

        checkLogin();
    }

    @Override
    public void returnFirestoreUserGetError(boolean isError) {
        Log.d(TAG, "returnFirestoreUserGetError: ");
        DialogUtils.showAlertDialog(mActivity,mActivity.getResources().getString(R.string.appSetup_sign_in_error_user_account_not_found_title), mActivity.getResources().getString(R.string.appSetup_sign_in_error_user_account_not_found_summary));

    }

    @Override
    public void updateUserProfileSuccess(FirestoreUser user) {
        //Not Used
    }

    @Override
    public void updateUserProfileGetError(boolean isError) {
        // Not Used
    }
}
