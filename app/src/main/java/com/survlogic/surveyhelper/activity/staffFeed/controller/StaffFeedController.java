package com.survlogic.surveyhelper.activity.staffFeed.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedAnnouncement;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedItem;
import com.survlogic.surveyhelper.activity.staffFeed.workers.AnnouncementGenerator;
import com.survlogic.surveyhelper.activity.staffFeed.workers.BirthdayGenerator;
import com.survlogic.surveyhelper.activity.staffFeed.workers.EventGenerator;
import com.survlogic.surveyhelper.activity.staffFeed.workers.FeedCompiler;
import com.survlogic.surveyhelper.activity.staffFeed.workers.FeedItemGenerator;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.model.AppStaticSettings;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StaffFeedController implements StaffFeedAdapter.AdapterListener,
                                            AnnouncementGenerator.AnnouncementGeneratorListener,
                                            BirthdayGenerator.BirthdayGeneratorListener,
                                            EventGenerator.EventGeneratorListener,
                                            FeedItemGenerator.FeedItemGeneratorListener{

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
        this.mListAnnouncements = announcementList;
        isWorkerFeedAnnouncementReturnOk = true;
    }

    @Override
    public void returnNAnnouncementsError(boolean isErrorState) {
        //Todo Handle Error Message

    }

    /**
     *BirthdayGeneratorListener
     */

    @Override
    public void returnBirthdayList(ArrayList<FeedBirthday> birthdayList,ArrayList<FeedBirthday> birthdayListAhead ) {
        this.mListBirthdays = birthdayList;
        this.mListBirthdaysFuture = birthdayListAhead;
        isWorkerFeedBirthdayReturnOk = true;
    }

    @Override
    public void returnNoBirthdays(boolean isErrorState) {
        //Todo Handle Error Message
    }

    /**
     *EventGeneratorListener
     */

    @Override
    public void returnEventList(ArrayList<FeedEvent> eventList) {
        this.mListEvents = eventList;
        isWorkerFeedEventReturnOk = true;
    }

    @Override
    public void returnEventsError(boolean isErrorState) {
    }

    /**
     * FeedItemGeneratorListener
     */

    @Override
    public void returnFeedItemList(ArrayList<FeedItem> itemList) {
        this.mListFeedItems = itemList;
        isWorkerFeedItemReturnOk = true;
    }

    @Override
    public void returnFeedItemError(boolean isErrorState) {
    }


    /**
     * AdapterListener
     */

    @Override
    public void refreshView() {

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
    private ArrayList<FeedBirthday> mListBirthdaysFuture = new ArrayList<>();

    private EventGenerator eventGenerator;
    private boolean isWorkerFeedEventSet = false;
    private boolean isWorkerFeedEventReturnOk = false;
    private ArrayList<FeedEvent> mListEvents = new ArrayList<>();

    private FeedItemGenerator feedItemGenerator;
    private boolean isWorkerFeedItemSet = false;
    private boolean isWorkerFeedItemReturnOk = false;
    private ArrayList<FeedItem> mListFeedItems = new ArrayList<>();

    private Date mFeedQueryDateToShow;
    private String mFeedRoomToShow;

    private boolean isCompilerFeedSet = false;
    private ArrayList<Feed> mFeedCompiledList = new ArrayList<>();

    private boolean isFeedAdapterSet = false;
    private RecyclerView mRecyclerView;
    private StaffFeedAdapter feedAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager mGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isAllFeedsReadyToGo = false;

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

        if(!isWorkerFeedEventSet){
            eventGenerator = new EventGenerator(mContext,this);
            isWorkerFeedEventSet = true;
        }

        if(!isWorkerFeedItemSet){
            feedItemGenerator = new FeedItemGenerator(mContext,this);
            isWorkerFeedItemSet = true;
        }

        if(!isCompilerFeedSet){


            isCompilerFeedSet = true;
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

    public void setSwipeRefreshLayout (SwipeRefreshLayout layout){
        this.swipeRefreshLayout = layout;

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reFetchNewFeeds(500);
            }
        });

    }

    public void setFeedQueryDateToShow(Date queryDate) {
        this.mFeedQueryDateToShow = queryDate;
    }


    public void setFeedRoomToShow(String queryRoom) {
        this.mFeedRoomToShow = queryRoom;
    }

    public void setRecyclerView(RecyclerView recyclerView){
        this.mRecyclerView = recyclerView;

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && fabActionAnnouncement.isShown())
                    fabActionAnnouncement.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fabActionAnnouncement.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });



    }

    public void fetchAllFeeds(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                callFeedCompilerOnRefresh(0);

                announcementGenerator.onStart();
                birthdayGenerator.onStart();
                eventGenerator.onStart();
                feedItemGenerator.onStart(mFeedQueryDateToShow, mFeedRoomToShow);

            }
        }, 0);

    }

    public void reFetchNewFeeds(final long timeToWait){

        if(invalidateFeeds()){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    callFeedCompilerOnRefresh(timeToWait);

                    announcementGenerator.onStart();
                    birthdayGenerator.onStart();
                    eventGenerator.onStart();
                    feedItemGenerator.onStart(mFeedQueryDateToShow, mFeedRoomToShow);

                }
            }, 0);
        }

    }


    private void callFeedCompilerOnRefresh(long timeToWait){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadFeedsAsyncTask mLoadFeeds = new LoadFeedsAsyncTask();
                mLoadFeeds.execute();

            }
        }, timeToWait);
    }

    private boolean invalidateFeeds(){
        mListAnnouncements.clear();
        mListBirthdays.clear();
        mListBirthdaysFuture.clear();
        mListEvents.clear();
        mListFeedItems.clear();

        mFeedCompiledList.clear();

        isAllFeedsReadyToGo = false;
        isWorkerFeedItemReturnOk = false;
        isWorkerFeedEventReturnOk = false;
        isWorkerFeedEventReturnOk = false;
        isWorkerFeedBirthdayReturnOk = false;

        return true;

    }


    private void updateFeedRecycler(){
        if(isFeedAdapterSet){
            feedAdapter.swapItems(mFeedCompiledList);

        }else{
            initFeedAdapter();

        }
    }

    private void initFeedAdapter(){
        feedAdapter = new StaffFeedAdapter(mContext,this,mFeedCompiledList);

        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density  = mActivity.getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth/100);

        mGridLayoutManager = new GridLayoutManager(mContext,columns);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(feedAdapter);

        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(feedAdapter.isFullCard(position)){
                    return mGridLayoutManager.getSpanCount();
                }else{
                    return 1;
                }
            }
        });

        isFeedAdapterSet = true;
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

        if(llContainer != null){
            popupWindowFeedItems.showAtLocation(llContainer, Gravity.TOP,0,0);
            isPopupWindowShown = true;
        }

    }

    private class LoadFeedsAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(true);
            }
        }


        @Override
        protected Void doInBackground(Void... params) {
            Long t = Calendar.getInstance().getTimeInMillis();

            while(!isAllFeedsReadyToGo && Calendar.getInstance().getTimeInMillis()-t < 10000){
                try {
                    Thread.sleep(1000);

                    if(isWorkerFeedAnnouncementReturnOk &&
                            isWorkerFeedBirthdayReturnOk &&
                            isWorkerFeedEventReturnOk &&
                            isWorkerFeedItemReturnOk){

                        isAllFeedsReadyToGo = true;
                    }

                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(Void result) {
            if(isWorkerFeedAnnouncementReturnOk &&
                    isWorkerFeedBirthdayReturnOk &&
                    isWorkerFeedEventReturnOk &&
                    isWorkerFeedItemReturnOk){

                FeedCompiler feedCompiler = new FeedCompiler(mContext);
                feedCompiler.setListAnnouncements(mListAnnouncements);
                feedCompiler.setListEvents(mListEvents);
                feedCompiler.setListBirthdays(mListBirthdays);
                feedCompiler.setListBirthdaysFuture(mListBirthdaysFuture);
                feedCompiler.setListFeedItems(mListFeedItems);

                mFeedCompiledList = feedCompiler.compileFeeds();

                if(mFeedCompiledList.size() !=0){
                    updateFeedRecycler();
                }else{
                    Log.d(TAG, "to_delete: No Feeds found ");
                }

            }else{
                //Todo Show Error Message Here
                Log.d(TAG, "to_delete: Timed Out! ");

            }

            swipeRefreshLayout.setRefreshing(false);

        }

    }




}
