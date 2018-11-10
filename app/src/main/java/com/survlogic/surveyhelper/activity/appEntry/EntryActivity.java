package com.survlogic.surveyhelper.activity.appEntry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appEntry.controller.EntryController;
import com.survlogic.surveyhelper.activity.appEntry.controller.WelcomeController;
import com.survlogic.surveyhelper.activity.appEntry.inter.EntryControllerListener;
import com.survlogic.surveyhelper.activity.appEntry.inter.WelcomeControllerListener;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.RemoteConfigLoader;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class EntryActivity extends AppCompatActivity implements WelcomeControllerListener,
                                                                EntryControllerListener,
                                                                RemoteConfigLoader.RemoteConfigLoaderListener,
                                                                AlertDialog.OnClickListener {
    private static final String TAG = "EntryActivity";

    //WelcomeControllerListener
    @Override
    public void refreshUI(AppSettings settings) {
        this.settings = settings;
        checkStartupWorkflow();

    }

    //EntryControllerListener
    @Override
    public void startLoginActivityForResults() {

    }

    @Override
    public void startHomeActivity() {

    }

    //RemoteConfigListener
    @Override
    public void isLoaded(boolean isLoaded) {
        initController();
        initStaticRemoteConfigSettings();
    }


    //AlertDialog.OnClickListener
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                killActivity();
                break;
        }
    }

    private Context mContext;
    private WelcomeController mController;
    private EntryController mEntryController;
    private AppSettings settings = new AppSettings();
    private RemoteConfigLoader remoteConfigLoader;

    private static final int INTENT_REQUEST_FIREBASE_LOGIN = 1000;
    private boolean isControllerSetup = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity_main);
        Log.d(TAG, "Entry Activity Started---------------------------------------------------->");
        mContext = EntryActivity.this;
        remoteConfigLoader = new RemoteConfigLoader(mContext, this);

        mEntryController = new EntryController(mContext,this);

    }

    private void initController() {
        if (!isControllerSetup) {
            this.mController = new WelcomeController(mContext, this);
            isControllerSetup = true;
        }

    }

    private void initStaticRemoteConfigSettings(){
        remoteConfigLoader.fetchRemoteConfigTheme();
        remoteConfigLoader.fetchRemoteConfigAnnouncements();
    }


    private void checkStartupWorkflow(){
        boolean isAppAvailable = remoteConfigLoader.fetchRemoteConfigApplicationAvailable();

        if(isAppAvailable){
            if(settings.isAppFirstRun()){
                mEntryController.startStartupActivityWorkflow();

            }else{
                mEntryController.startHomeActivityWorkflow();
            }
        }else{
            String dialogTitle = getResources().getString(R.string.general_exit_app);
            String dialogSummary = remoteConfigLoader.fetchRemoteConfigApplicationAvailableReason();

            DialogUtils.showAlertDialog(mContext,dialogTitle,dialogSummary,this);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_REQUEST_FIREBASE_LOGIN && resultCode == RESULT_OK){
            boolean key_login_successful = data.getBooleanExtra(getResources().getString(R.string.KEY_LOGIN_SUCCESSFUL),false);
            if(key_login_successful){
                mEntryController.startHomeActivityWorkflow();

            }

        }


    }

    private void killActivity(){
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
