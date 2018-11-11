package com.survlogic.surveyhelper.activity.staffFeed.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedAnnouncement;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;
import com.survlogic.surveyhelper.activity.staffFeed.workers.AnnouncementGenerator;
import com.survlogic.surveyhelper.activity.staffFeed.workers.BirthdayGenerator;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.model.AppStaticSettings;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.ArrayList;

public class StaffFeedController implements AnnouncementGenerator.AnnouncementGeneratorListener,
                                            BirthdayGenerator.BirthdayGeneratorListener {

    private static final String TAG = "StaffFeedController";

    public interface StaffFeedControllerListener {

        void refreshFragmentUI();
        void sendFeedCategory(String feedCategory);

    }

    /**
     *AnnouncementGeneratorListener
     */
    @Override
    public void returnAnnouncementList(ArrayList<FeedAnnouncement> announcementList) {
        isWorkerFeedAnnouncementReturnOk = true;
        this.mListAnnouncements = announcementList;

    }

    @Override
    public void returnNoAnnouncements(boolean isErrorState) {
        //Todo Handle Error Message
        DialogUtils.showToast(mContext,"Whoops.  Error.  Need to fix this!");
    }

    /**
     *BirthdayGeneratorListener
     */

    @Override
    public void returnBirthdayList(ArrayList<FeedBirthday> birthdayList) {
        isWorkerFeedBirthdayReturnOk = true;
        this.mListBirthdays = birthdayList;
    }

    @Override
    public void returnNoBirthdays(boolean isErrorState) {
        //Todo Handle Error Message
        DialogUtils.showToast(mContext,"Whoops.  Error.  Need to fix this!");
    }

    private Context mContext;
    private Activity mActivity;

    private StaffFeedControllerListener mStaffFeedControllerListener;

    private PreferenceLoader preferenceLoader;
    private AppSettings settings;
    private AppStaticSettings mStaticSettings;

    private FloatingActionButton fabActionAnnouncement;
    private boolean isPopupWindowShown = false;

    private AnnouncementGenerator announcementGenerator;
    private boolean isWorkerFeedAnnouncementSet = false;
    private boolean isWorkerFeedAnnouncementReturnOk = false;
    private ArrayList<FeedAnnouncement> mListAnnouncements = new ArrayList<>();

    private BirthdayGenerator birthdayGenerator;
    private boolean isWorkerFeedBirthdaySet = false;
    private boolean isWorkerFeedBirthdayReturnOk = false;
    private ArrayList<FeedBirthday> mListBirthdays = new ArrayList<>();


    public StaffFeedController(Context context, StaffFeedControllerListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mStaffFeedControllerListener = listener;

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
        if(!isWorkerFeedAnnouncementSet){
            announcementGenerator = new AnnouncementGenerator(mContext,this);
            isWorkerFeedAnnouncementSet = true;
        }

        if(!isWorkerFeedBirthdaySet){
            birthdayGenerator = new BirthdayGenerator(mContext,this);
            isWorkerFeedBirthdaySet = true;
        }

    }

    private void initViewWidgets(){
        fabActionAnnouncement = mActivity.findViewById(R.id.appBar_bottom_fab);
        fabActionAnnouncement.hide();

        fabActionAnnouncement.setImageDrawable(mActivity.getDrawable(R.drawable.ic_action_announcement_brand));

        fabActionAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopUpFeedActionItems();
            }
        });

        fabActionAnnouncement.show();

        RelativeLayout rlSpecialActionButton = mActivity.findViewById(R.id.rl_special_launcher_forms);
        rlSpecialActionButton.setVisibility(View.GONE);

    }

    public void fetchAllFeeds(){
        Log.d(TAG, "to_delete: Fetching Feed from Controller");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                announcementGenerator.onStart();
                birthdayGenerator.onStart();
            }
        }, 3000);

    }



    public void createPopUpFeedNavigator(View anchorView){
        String title;

        PopupMenu popupMenu = new PopupMenu(mActivity,anchorView);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case 1:
                        String title1 = item.getTitle().toString();
                        mStaffFeedControllerListener.sendFeedCategory(title1);

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

    public void getPopUpFeedAnnouncement(){
        mStaticSettings = new AppStaticSettings(preferenceLoader.getStaticSettings());

        if(mStaticSettings.isPromo()){
            if(preferenceLoader.getAnnouncementShowReward()){
                createPopUpFeedActionAnnoucementGame();
            }

        }

    }


    private void createPopUpFeedActionItems(){

        if(isPopupWindowShown){
            return;
        }

        fabActionAnnouncement.hide();

        View popupView = mActivity.getLayoutInflater().inflate(R.layout.staff_feed_popup_feed_add, null);

        float density = mActivity.getResources().getDisplayMetrics().density;

        int width = (int) density * 240;
        int height = (int) density * 285;

        int marginBottom = (int) density * 50;
        int marginEnd = (int) density * 50;

        final PopupWindow popupWindowFeedItems = new PopupWindow(popupView, width, height,true);

        popupWindowFeedItems.setAnimationStyle(R.style.popup_window_animation_explode);

        popupWindowFeedItems.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindowFeedItems.setTouchable(true);
        popupWindowFeedItems.setFocusable(false);
        popupWindowFeedItems.setOutsideTouchable(true);

        popupWindowFeedItems.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindowFeedItems.dismiss();
                    isPopupWindowShown = false;
                    fabActionAnnouncement.show();
                    return true;
                }

                return false;
            }

        });

        popupWindowFeedItems.setElevation(10);

        popupWindowFeedItems.showAsDropDown(fabActionAnnouncement, marginEnd,marginBottom,Gravity.END);
        isPopupWindowShown = true;
    }

    private void createPopUpFeedActionAnnoucementGame(){

        if(isPopupWindowShown){
            return;
        }

        fabActionAnnouncement.hide();

        View popupView = mActivity.getLayoutInflater().inflate(R.layout.staff_feed_popup_feed_announcement, null);
        ImageView ivImage = popupView.findViewById(R.id.announcement_image_background);
        final ProgressBar progress = popupView.findViewById(R.id.announcement_image_background_progress);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mStaticSettings.getPromoUrl(), ivImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(progress !=null){
                    progress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(progress !=null){
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(progress !=null){
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(progress !=null){
                    progress.setVisibility(View.GONE);
                }
            }
        });

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindowFeedItems = new PopupWindow(popupView, width, height,true);

        popupWindowFeedItems.setAnimationStyle(R.style.popup_window_animation_slide_down);

        popupWindowFeedItems.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindowFeedItems.setTouchable(true);
        popupWindowFeedItems.setFocusable(false);
        popupWindowFeedItems.setOutsideTouchable(true);

        Button btnDismissForSession = popupView.findViewById(R.id.announcement_button_action_dont_show);
        btnDismissForSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceLoader.setAnnouncementShowReward(false,true);
                popupWindowFeedItems.dismiss();
                isPopupWindowShown = false;
                fabActionAnnouncement.show();
            }
        });


        popupWindowFeedItems.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindowFeedItems.dismiss();
                    isPopupWindowShown = false;
                    fabActionAnnouncement.show();
                    return true;
                }

                return false;
            }

        });

        popupWindowFeedItems.setElevation(10);


        LinearLayout llContainer = mActivity.findViewById(R.id.ll_feed_fragment_container);

        popupWindowFeedItems.showAtLocation(llContainer, Gravity.TOP,0,0);
        isPopupWindowShown = true;
    }

}
