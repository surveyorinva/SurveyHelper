package com.survlogic.surveyhelper.activity.staffCompany.controller;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.RelativeLayout;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

public class StaffCompanyController {

    private static final String TAG = "StaffCompanyController";

    public interface StaffCompanyControllerListener {

        void refreshCompanyFragmentUI();
    }

    private Context mContext;
    private Activity mActivity;

    private StaffCompanyControllerListener mStaffCompanyControllerListener;

    private PreferenceLoader preferenceLoader;
    private AppSettings settings;

    private FloatingActionButton fabActionAnnouncement;

    private boolean isPopupWindowShown = false;


    public StaffCompanyController(Context context, StaffCompanyControllerListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mStaffCompanyControllerListener = listener;

        initController();
        initViewWidgets();

    }

    private void initController(){
        loadPreferences();
        initWorkers();
    }

    private void loadPreferences(){
        preferenceLoader = new PreferenceLoader(mContext);
        settings = preferenceLoader.getSettings();

    }

    private void initWorkers(){


    }

    private void initViewWidgets(){
        fabActionAnnouncement = mActivity.findViewById(R.id.appBar_bottom_fab);
        fabActionAnnouncement.hide();

        RelativeLayout rlSpecialActionButton = mActivity.findViewById(R.id.rl_special_launcher_forms);
        rlSpecialActionButton.setVisibility(View.GONE);

    }


}
