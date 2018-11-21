package com.survlogic.surveyhelper.activity.staffFeed.controller;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.StaffFeedFragment;
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.dialog.FeedBottomSheet;
import com.survlogic.surveyhelper.activity.staffFeed.dialog.FeedDialogUtils;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedAnnouncement;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedItem;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedRooms;
import com.survlogic.surveyhelper.activity.staffFeed.workers.GeneratorAnnouncement;
import com.survlogic.surveyhelper.activity.staffFeed.workers.GeneratorBirthday;
import com.survlogic.surveyhelper.activity.staffFeed.workers.GeneratorEvent;
import com.survlogic.surveyhelper.activity.staffFeed.workers.FeedCompiler;
import com.survlogic.surveyhelper.activity.staffFeed.workers.GeneratorFeedItem;
import com.survlogic.surveyhelper.activity.staffFeed.workers.GeneratorRoomActions;
import com.survlogic.surveyhelper.database.Feed_Rooms.FirestoreDatabaseFeedRooms;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.model.AppStaticSettings;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StaffFeedController implements StaffFeedAdapter.AdapterListener,
                                            GeneratorAnnouncement.AnnouncementGeneratorListener,
                                            GeneratorBirthday.BirthdayGeneratorListener,
                                            GeneratorEvent.EventGeneratorListener,
                                            GeneratorFeedItem.FeedItemGeneratorListener,
                                            GeneratorRoomActions.RoomActionsListener,
                                            FirestoreDatabaseFeedRooms.FeedRoomListener,
                                            StaffFeedRecycleController.FeedRecycleListener,
                                            FeedDialogUtils.DialogListener,
                                            FeedBottomSheet.FeedItemBottomSheetListener{

    private static final String TAG = "StaffFeedController";

    public interface StaffFeedControllerListener {

        void refreshFragmentUI();
        void sendFeedCategoryNameToAppBar(String feedCategory);

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
    public void returnAnnouncementsError(boolean isErrorState) {
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
        //Todo Handle Error Message
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
    public void returnFeedItemNoList() {
        isWorkerFeedItemReturnOk = true;
    }

    @Override
    public void returnFeedItemError(boolean isErrorState) {
        //Todo Handle Error Message
    }


    /**
     * StaffFeedAdapter.AdapterListener
     */

    @Override
    public void refreshView() {

    }


    /**
     * FeedRoomListener
     */

    @Override
    public void fetchPublicHall(FeedRooms publicHall) {
        this.mRoomPublicOptions = publicHall;
        isWorkerRoomPublicReturnOk = true;

        generatorRoomActions.setRoomActionsRaw(mRoomPublicOptions.getFeed_actions_common());
        generatorRoomActions.build();

    }


    @Override
    public void fetchPrivateRoomsMember(ArrayList<FeedRooms> privateRoomList) {
        this.mListRoomPrivateRooms = privateRoomList;
        isWorkerRoomPrivateReturnOk = true;
    }

    @Override
    public void fetchFeedRoomGetError(boolean isError) {
        //Todo Handle Error Message
        Log.d(TAG, "to_delete: fetchFeedRoomGetError ");
    }

    @Override
    public void returnActions(ArrayList<FeedActions> feedActions) {
        Log.d(TAG, "to_delete: Return Actions: " + feedActions.size());
        this.mFeedActionsForBottomSheet = feedActions;
    }

    /**
     * RoomActionsListener
     */


    @Override
    public void returnActionsError(boolean isError) {
        //Todo Handle Error Message
        Log.d(TAG, "to_delete: returnActionsError ");
    }

    /**
     *FeedDialogUtils.DialogListener
     */

    @Override
    public void isPopupOpen(boolean isOpen) {
        isPopupWindowShown = isOpen;

        if(isOpen){
            fabActionAnnouncement.hide();
        }else{
            fabActionAnnouncement.show();
        }
    }

    /**
     *StaffFeedRecycleController.FeedRecycleListener
     */

    @Override
    public void showFab(boolean isShow) {
        if(isShow){
            fabActionAnnouncement.show();
        }else{
            fabActionAnnouncement.hide();
        }
    }

    @Override
    public boolean isFabShown() {
        return fabActionAnnouncement.isShown();
    }

    /**
     * FeedBottomSheet.FeedItemBottomSheetListener
     */

    @Override
    public void isOpen(boolean isOpen) {

    }

    @Override
    public ArrayList<FeedActions> getFeedActions() {
        Log.d(TAG, "to_delete: Sending Feed Actions" + mFeedActionsForBottomSheet.size());
        return mFeedActionsForBottomSheet;
    }



    private Context mContext;
    private Activity mActivity;

    private StaffFeedControllerListener mStaffFeedControllerListener;

    private PreferenceLoader preferenceLoader;
    private AppSettings settings;
    private AppStaticSettings mStaticSettings;

    private FloatingActionButton fabActionAnnouncement;
    private boolean isPopupWindowShown = false;

    private FirestoreUser mFirestoreUser;

    private List mUserPrivateRoomMembers;
    private boolean isUserHavePrivateRooms = false;
    private ArrayList<FeedRooms> mListRoomPrivateRooms;
    private FeedRooms mRoomPublicOptions;

    private ArrayList<FeedActions> mFeedActionsForBottomSheet;
    private String mRoomToShow, mRoomPublic;

    private GeneratorAnnouncement generatorAnnouncement;
    private boolean isWorkerFeedAnnouncementSet = false;
    private boolean isWorkerFeedAnnouncementReturnOk = false;
    private ArrayList<FeedAnnouncement> mListAnnouncements = new ArrayList<>();

    private GeneratorBirthday generatorBirthday;
    private boolean isWorkerFeedBirthdaySet = false;
    private boolean isWorkerFeedBirthdayReturnOk = false;
    private ArrayList<FeedBirthday> mListBirthdays = new ArrayList<>();
    private ArrayList<FeedBirthday> mListBirthdaysFuture = new ArrayList<>();

    private GeneratorEvent generatorEvent;
    private boolean isWorkerFeedEventSet = false;
    private boolean isWorkerFeedEventReturnOk = false;
    private ArrayList<FeedEvent> mListEvents = new ArrayList<>();

    private GeneratorFeedItem generatorFeedItem;
    private boolean isWorkerFeedItemSet = false;
    private boolean isWorkerFeedItemReturnOk = false;
    private ArrayList<FeedItem> mListFeedItems = new ArrayList<>();

    private GeneratorRoomActions generatorRoomActions;
    private boolean isWorkerRoomActionsSet = false;
    private boolean isWorkerRoomPublicReturnOk = false;
    private boolean isWorkerRoomPrivateReturnOk = false;
    private boolean isAllRoomsReadyToGo = false;
    private ArrayList<GeneratorRoomActions.RoomActions> roomActions = new ArrayList<>();

    private Date mFeedQueryDateToShow;

    private boolean isCompilerFeedSet = false;
    private ArrayList<Feed> mFeedCompiledList = new ArrayList<>();

    private boolean isFeedAdapterSet = false;
    private StaffFeedRecycleController feedRecycleController;

    private RecyclerView mRecyclerView;
    private StaffFeedAdapter feedAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager mGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isAllFeedsReadyToGo = false;


    private StaffFeedFragment mStaffFeedFragment;

    public StaffFeedController(Context context, StaffFeedControllerListener listener, StaffFeedFragment parentFragment) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mStaffFeedFragment = parentFragment;
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
            generatorAnnouncement = new GeneratorAnnouncement(mContext,this);
            isWorkerFeedAnnouncementSet = true;
        }

        if(!isWorkerFeedBirthdaySet){
            generatorBirthday = new GeneratorBirthday(mContext,this);
            isWorkerFeedBirthdaySet = true;
        }

        if(!isWorkerFeedEventSet){
            generatorEvent = new GeneratorEvent(mContext,this);
            isWorkerFeedEventSet = true;
        }

        if(!isWorkerFeedItemSet){
            generatorFeedItem = new GeneratorFeedItem(mContext,this);
            isWorkerFeedItemSet = true;
        }

        if(!isCompilerFeedSet){
            isCompilerFeedSet = true;
        }

        if(!isWorkerRoomActionsSet){
            generatorRoomActions = new GeneratorRoomActions(mContext,this);
            isWorkerRoomActionsSet = true;
        }


    }

    private void initViewWidgets(){
        fabActionAnnouncement = mActivity.findViewById(R.id.appBar_bottom_fab);
        fabActionAnnouncement.hide();

        fabActionAnnouncement.setImageDrawable(mActivity.getDrawable(R.drawable.ic_action_announcement_brand));
        fabActionAnnouncement.show();

        fabActionAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBottomSheetView();

            }
        });


        RelativeLayout rlSpecialActionButton = mActivity.findViewById(R.id.rl_special_launcher_forms);
        rlSpecialActionButton.setVisibility(View.GONE);

    }

    private void callBottomSheetView(){
        FeedBottomSheet dialog = FeedBottomSheet.newInstance(mContext,this);
        dialog.show(mStaffFeedFragment.getFragmentManager(),"bottom_Sheet");
    }

    //----------------------------------------------------------------------------------------------

    public void setSwipeRefreshLayout (SwipeRefreshLayout layout){
        this.swipeRefreshLayout = layout;

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reFetchNewPublicRoomFeeds(500);
            }
        });

    }

    public void setFeedQueryDateToShow(Date queryDate) {
        this.mFeedQueryDateToShow = queryDate;
    }


    public void setFeedRoomToShow(String queryRoom) {
        this.mRoomToShow = queryRoom;
    }

    public void setFeedRoomPublic(String queryRoom){
        this.mRoomPublic = queryRoom;
    }

    public void setFirestoreUser(FirestoreUser user) {
        this.mFirestoreUser = user;
    }

    //----------------------------------------------------------------------------------------------
    public void buildUserProfile(){
        buildPrivateFeedRooms();

    }

    private void buildPrivateFeedRooms(){
        final FirestoreDatabaseFeedRooms db = new FirestoreDatabaseFeedRooms(mContext,this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                LoadRoomsAsyncTask async = new LoadRoomsAsyncTask();
                async.execute();

                if(mFirestoreUser.getPrivate_room_member() != null){
                    mUserPrivateRoomMembers = mFirestoreUser.getPrivate_room_member();
                    isUserHavePrivateRooms = true;
                }

                if(isUserHavePrivateRooms){
                    db.getRoomListFromFirestore(mUserPrivateRoomMembers);
                }else{
                    db.getRoomListFromFirestore();
                }

            }
        }, 0);

    }

    //----------------------------------------------------------------------------------------------

    public void setRecyclerController(RecyclerView recyclerView){

        feedRecycleController = new StaffFeedRecycleController(mContext,this,this);
        feedRecycleController.setFeedCompiledList(mFeedCompiledList);

        feedRecycleController.setRecyclerView(recyclerView);

    }

    //----------------------------------------------------------------------------------------------
    public void fetchPublicRoomFeeds(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                callPublicFeedCompilerOnRefresh(0);

                generatorAnnouncement.onStart();
                generatorBirthday.onStart();
                generatorEvent.onStart();
                generatorFeedItem.onStart(mFeedQueryDateToShow, mRoomToShow);

            }
        }, 0);

    }

    public void reFetchNewPublicRoomFeeds(final long timeToWait){
        if(invalidateFeeds()){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    callPublicFeedCompilerOnRefresh(timeToWait);

                    generatorAnnouncement.onStart();
                    generatorBirthday.onStart();
                    generatorEvent.onStart();
                    generatorFeedItem.onStart(mFeedQueryDateToShow, mRoomToShow);

                }
            }, 0);
        }

    }

    public void fetchPrivateFeeds(final long timeToWait){
        if(invalidateFeeds()){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    callPrivateFeedCompilerOnRefresh(timeToWait);
                    generatorFeedItem.onStart(mRoomToShow);
                }
            },0);
        }
    }


    private void callPublicFeedCompilerOnRefresh(long timeToWait){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadPublicFeedsAsyncTask mLoadFeeds = new LoadPublicFeedsAsyncTask();
                mLoadFeeds.execute();

            }
        }, timeToWait);
    }

    private void callPrivateFeedCompilerOnRefresh(long timeToWait){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadPrivateFeedsAsyncTask mLoadFeeds = new LoadPrivateFeedsAsyncTask();
                mLoadFeeds.execute();
            }
        },timeToWait);
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

    //----------------------------------------------------------------------------------------------

    public void createPopUpFeedRoomNavigator(View anchorView){
        String title;

        PopupMenu popupMenu = new PopupMenu(mActivity,anchorView);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case 1:
                        String title1 = item.getTitle().toString();
                        mStaffFeedControllerListener.sendFeedCategoryNameToAppBar(title1);
                        setFeedRoomToShow(mRoomPublic);
                        reFetchNewPublicRoomFeeds(500);

                        generatorRoomActions.setRoomActionsRaw(mRoomPublicOptions.getFeed_actions_common());
                        generatorRoomActions.build();
                        return true;

                }

                return false;
            }
        });


        if(isUserHavePrivateRooms){
            for(int i = 0; i  < mListRoomPrivateRooms.size(); i++){
                final FeedRooms room = mListRoomPrivateRooms.get(i);

                Menu menu = popupMenu.getMenu();
                MenuItem item = menu.add(2,Menu.NONE,i+100,room.getRoom_name());
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title1 = item.getTitle().toString();
                        mStaffFeedControllerListener.sendFeedCategoryNameToAppBar(title1);

                        setFeedRoomToShow(room.getRoom_id());
                        fetchPrivateFeeds(500);

                        generatorRoomActions.setRoomActionsRaw(room.getFeed_actions_common());
                        generatorRoomActions.build();
                        return true;
                    }
                });

            }

        }

        popupMenu.getMenu().add(1,1,1,mRoomPublicOptions.getRoom_name());
        popupMenu.show();

    }

    public void getPopUpFeedAnnouncement(){
        mStaticSettings = new AppStaticSettings(preferenceLoader.getStaticSettings());

        if(mStaticSettings.isPromo()){
            if(preferenceLoader.getAnnouncementShowReward()){
                createPopUpFeedActionAnnouncement();
            }
        }
    }


    private void createPopUpFeedActionAnnouncement(){
        FeedDialogUtils dialogUtils = new FeedDialogUtils(mContext,this);

        PopupWindow popuWindow = dialogUtils.createPopUpFeedActionAnnouncementGame(mStaticSettings.getPromoUrl());

        LinearLayout llContainer = mActivity.findViewById(R.id.ll_feed_fragment_container);

        if(llContainer != null){
            popuWindow.showAtLocation(llContainer, Gravity.TOP,0,0);
            isPopupWindowShown = true;
        }

    }


    private class LoadPublicFeedsAsyncTask extends AsyncTask<Void, Void, Void> {
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
            if(isAllFeedsReadyToGo){

                FeedCompiler feedCompiler = new FeedCompiler(mContext);
                feedCompiler.setListAnnouncements(mListAnnouncements);
                feedCompiler.setListEvents(mListEvents);
                feedCompiler.setListBirthdays(mListBirthdays);
                feedCompiler.setListBirthdaysFuture(mListBirthdaysFuture);
                feedCompiler.setListFeedItems(mListFeedItems);

                mFeedCompiledList = feedCompiler.compileFeeds();

                if(mFeedCompiledList.size() !=0){

                    feedRecycleController.setFeedCompiledList(mFeedCompiledList);
                    feedRecycleController.updateFeedRecycler();

                }else{
                    //Todo Show Error Message Here
                    Log.d(TAG, "to_delete: No Feeds found ");
                }

            }else{
                //Todo Show Error Message Here
                Log.d(TAG, "to_delete: Timed Out! ");

            }

            swipeRefreshLayout.setRefreshing(false);

        }

    }

    private class LoadPrivateFeedsAsyncTask extends AsyncTask<Void, Void, Void> {
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

                    if(isWorkerFeedItemReturnOk){

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
            if(isWorkerFeedItemReturnOk){

                FeedCompiler feedCompiler = new FeedCompiler(mContext);
                feedCompiler.setListFeedItems(mListFeedItems);

                mFeedCompiledList = feedCompiler.compileFeeds();

                if(mFeedCompiledList.size() !=0){
                    feedRecycleController.setFeedCompiledList(mFeedCompiledList);
                    feedRecycleController.updateFeedRecycler();

                }else{
                    //Todo Show Error Message Here
                    Log.d(TAG, "to_delete: No Feeds found ");
                }

            }else{
                //Todo Show Error Message Here
                Log.d(TAG, "to_delete: Timed Out! ");

            }

            swipeRefreshLayout.setRefreshing(false);

        }

    }

    private class LoadRoomsAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... params) {
            Long t = Calendar.getInstance().getTimeInMillis();

            while(!isAllRoomsReadyToGo && Calendar.getInstance().getTimeInMillis()-t < 10000){
                try {
                    Thread.sleep(1000);

                    if(isWorkerRoomPublicReturnOk &&
                            isWorkerRoomPrivateReturnOk){

                        isAllRoomsReadyToGo = true;
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
            if(isAllRoomsReadyToGo){


            }else{
                //Todo Show Error Message Here
                Log.d(TAG, "to_delete: Rooms Timed Out! ");

            }

        }

    }


}
