package com.survlogic.surveyhelper.activity.staffProject.controller;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

public class StaffProjectController {

    private static final String TAG = "StaffProjectController";

    public interface StaffProjectControllerListener {

        void refreshProjectFragmentUI();

    }

    private Context mContext;
    private Activity mActivity;

    private StaffProjectControllerListener mStaffProjectControllerListener;

    private PreferenceLoader preferenceLoader;
    private AppSettings settings;

    private FloatingActionButton fabActionAnnouncement;

    private boolean isPopupWindowShown = false;


    public StaffProjectController(Context context, StaffProjectControllerListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mStaffProjectControllerListener = listener;

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

    private void initViewWidgets() {
        fabActionAnnouncement = mActivity.findViewById(R.id.appBar_bottom_fab);
        fabActionAnnouncement.hide();

        fabActionAnnouncement.setImageDrawable(mActivity.getDrawable(R.drawable.ic_search_dark_24dp));

        fabActionAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fabActionAnnouncement.show();

        RelativeLayout rlSpecialActionButton = mActivity.findViewById(R.id.rl_special_launcher_forms);
        rlSpecialActionButton.setVisibility(View.GONE);




    }
    public void createPopUpFeedNavigator(View anchorView){
        PopupMenu popupMenu = new PopupMenu(mActivity,anchorView);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case 1:
                        String title1 = item.getTitle().toString();

                        //Todo Action to Refresh feedView

                        return true;

                }


                return false;
            }
        });
        //Todo Feed Array List available here!
        popupMenu.getMenu().add(1, 1,1, mActivity.getString(R.string.staff_feed_navigation_feed_corporate));

        popupMenu.show();

    }




}
