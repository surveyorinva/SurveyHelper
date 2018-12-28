package com.survlogic.surveyhelper.activity.staffFeed.view.event;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.activity.FeedWebActivity;
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.activity.staffFeed.view.event.activity.CardFeedEventRosterActivity;
import com.survlogic.surveyhelper.activity.staffFeed.view.event.controller.CardFeedEventController;
import com.survlogic.surveyhelper.utils.DateUtils;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.HapticFeedbackUtils;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CardFeedEvent extends RecyclerView.ViewHolder implements CardFeedEventController.CardFeedEventWorkerListener{

    private static final String TAG = "CardFeedEvent";

    public interface CardFeedEventListener{
        void replaceEvent(int position, FeedEvent event);
    }


    /**
     * CardFeedEventWorkerListener
     */

    @Override
    public void returnSuccessful() {

    }

    @Override
    public void returnInError(boolean isError) {

    }

    @Override
    public void callZoomablePhotoDialog(String photoURL) {
        mListner.openPhotoViewDialog(photoURL);
    }

    @Override
    public void updateFeedEventInAdapter(FeedEvent event) {
        mEventListener.replaceEvent(position,event);
    }


    private Context mContext;
    private Activity mActivity;
    private StaffFeedAdapter.AdapterListener mListner;
    private CardFeedEventListener mEventListener;

    private ImageView ivBackground;
    private ProgressBar pbBackground;
    private TextView tvHeader, tvDetails, tvStartDate, tvStartTime, tvLocation;
    private LinearLayout llRootPhase, llPhaseI_Intro, llPhaseII_Count_Down;
    private ConstraintLayout clPhaseIII_Check_In, clPhaseIV_In_Event, clPhaseV_After_Event;

    private boolean isEventWorkerSetup = false;
    private int currentPhaseShown = 0;
    private CardFeedEventController mEventWorker;

    private ArrayList<Feed> mFeedList;
    private FeedEvent mFeedEvent;
    private int position;

    private FABProgressCircle fabProgressCircle;

    public static final int EVENT_SYSTEM_ERROR = 0,
                            EVENT_PHASE_INTRO = 1,
                            EVENT_PHASE_COUNTDOWN_DAYS = 2,
                            EVENT_PHASE_DAY_OF_EVENT =  3,
                            EVENT_PHASE_IN_EVENT = 4,
                            EVENT_PHASE_REMINISCE = 5,
                            EVENT_PHASE_OVER = 6,
                            EVENT_PHASE_HIDE = 7;

    private Uri singleImageUri;
    private Bitmap singleImageBitmap;


    public CardFeedEvent(@NonNull View itemView, Context context, StaffFeedAdapter.AdapterListener listener, CardFeedEventListener eventListener) {
        super(itemView);

        this.mContext = context;
        this.mActivity = (Activity) context;
        this.mListner = listener;
        this.mEventListener = eventListener;

        initViewWidgets();
        initWorker();


    }

    private void initViewWidgets(){

        ivBackground = itemView.findViewById(R.id.event_image);
        pbBackground = itemView.findViewById(R.id.event_image_progress);

        tvHeader = itemView.findViewById(R.id.event_text_header);
        tvDetails = itemView.findViewById(R.id.event_body);
        tvStartDate = itemView.findViewById(R.id.event_date_start);
        tvStartTime = itemView.findViewById(R.id.event_date_time);

        tvLocation = itemView.findViewById(R.id.event_location);

        llRootPhase = itemView.findViewById(R.id.ll_event_card_root_phase);

            ((ViewGroup) itemView.findViewById(R.id.ll_event_card_root_phase)).getLayoutTransition()
                    .enableTransitionType(LayoutTransition.CHANGING);

        llPhaseI_Intro = itemView.findViewById(R.id.ll_event_actions_invitation);
        llPhaseII_Count_Down = itemView.findViewById(R.id.ll_event_actions_countdown);
        clPhaseIII_Check_In = itemView.findViewById(R.id.cl_event_actions_check_in);
        clPhaseIV_In_Event = itemView.findViewById(R.id.cl_event_actions_in_event);
        clPhaseV_After_Event = itemView.findViewById(R.id.cl_event_actions_after_event);

    }

    private void initWorker(){
        if(!isEventWorkerSetup){
            mEventWorker = new CardFeedEventController(mContext, this);
            isEventWorkerSetup = true;
        }
    }



    public void configureViewHolder(ArrayList<Feed> feedList, int position){
        this.position = position;
        this.mFeedList = feedList;

        Feed feed = feedList.get(position);
        mFeedEvent = feed.getEvent();
        mEventWorker.setEvent(mFeedEvent);

        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        Activity mActivity = (Activity) mContext;

        if(preferenceLoader.isFeedEventStyleLight()){
            tvHeader.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextPrimary, null));
            tvDetails.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextPrimary, null));
        }else{
            tvHeader.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextSecondary, null));
            tvDetails.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextPrimary, null));
        }

        tvHeader.setText(mFeedEvent.getEvent_header());
        tvDetails.setText(mFeedEvent.getEvent_body());

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE. MMM. dd, yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a");

        Date dateStart = new Date(mFeedEvent.getDate_event_start());
        Date dateEnd = new Date(mFeedEvent.getDate_event_end());

        String startDateFormatted = dateFormat.format(dateStart);
        String startTimeFormatted = hourFormat.format(dateStart);
        String endTimeFormatted = hourFormat.format(dateEnd);

        tvStartDate.setText(startDateFormatted);
        tvStartTime.setText(mActivity.getResources().getString(
                                                R.string.staff_feed_card_event_time_of_event_fmt,
                                                startTimeFormatted,
                                                endTimeFormatted));

        tvLocation.setText(mActivity.getResources().getString(R.string.staff_feed_card_event_location_of_event_fmt,
                                                mFeedEvent.getEvent_location(),
                                                mFeedEvent.getEvent_address()));

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mFeedEvent.getBackground_url(), ivBackground, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(pbBackground !=null){
                    pbBackground.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(pbBackground !=null){
                    pbBackground.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(pbBackground !=null){
                    pbBackground.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(pbBackground !=null){
                    pbBackground.setVisibility(View.GONE);
                }
            }
        });

        configureCardPhase();

    }

    private void configureCardPhase(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                controlCardPhaseView(mEventWorker.determineEventPhase());

            }
        }, 700);


    }

    private void controlCardPhaseView(int phase){
        if(currentPhaseShown != phase){
            llPhaseI_Intro.setVisibility(View.GONE);
            llPhaseII_Count_Down.setVisibility(View.GONE);
            clPhaseIII_Check_In.setVisibility(View.GONE);
            clPhaseIV_In_Event.setVisibility(View.GONE);
            clPhaseV_After_Event.setVisibility(View.GONE);

            switch (phase){
                case EVENT_SYSTEM_ERROR:
                    break;

                case EVENT_PHASE_INTRO:
                    llPhaseI_Intro.setVisibility(View.VISIBLE);
                    currentPhaseShown = EVENT_PHASE_INTRO;

                    initViewIntro();

                    break;

                case EVENT_PHASE_COUNTDOWN_DAYS:
                    llPhaseII_Count_Down.setVisibility(View.VISIBLE);
                    currentPhaseShown = EVENT_PHASE_COUNTDOWN_DAYS;

                    initViewCountDownToEvent();

                    break;

                case EVENT_PHASE_DAY_OF_EVENT:
                    clPhaseIII_Check_In.setVisibility(View.VISIBLE);
                    currentPhaseShown = EVENT_PHASE_DAY_OF_EVENT;

                    initViewBeforeEvent();

                    break;

                case EVENT_PHASE_IN_EVENT:
                    clPhaseIV_In_Event.setVisibility(View.VISIBLE);
                    currentPhaseShown = EVENT_PHASE_IN_EVENT;

                    initViewInEvent();

                    break;

                case EVENT_PHASE_REMINISCE:
                    clPhaseV_After_Event.setVisibility(View.VISIBLE);
                    currentPhaseShown = EVENT_PHASE_REMINISCE;

                    initViewReminisce();
                    break;

                case EVENT_PHASE_OVER:
                    currentPhaseShown = EVENT_PHASE_OVER;
                    break;

                case EVENT_PHASE_HIDE:
                    currentPhaseShown = EVENT_PHASE_HIDE;
                    initViewHide();

                    break;

            }
        }else if(currentPhaseShown == EVENT_PHASE_DAY_OF_EVENT && phase == EVENT_PHASE_DAY_OF_EVENT){
            initViewBeforeEvent();
        }

    }

    private void initViewIntro(){
        Button btMoreInfo = mActivity.findViewById(R.id.event_btn_learn_more);
        btMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, FeedWebActivity.class);
                i.putExtra(mActivity.getResources().getString(R.string.KEY_EVENT_WEB_URL),mFeedEvent.getDetails_url());
                i.putExtra(mActivity.getResources().getString(R.string.KEY_EVENT_HEADER_TITLE),mActivity.getResources().getString(R.string.staff_feed_event_web_view_event_all_about_event));
                mActivity.startActivity(i);
                mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });


        Button btGoing = mActivity.findViewById(R.id.event_btn_event_going);
        btGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventWorker.addUserToList(CardFeedEventController.LIST_GOING);
            }
        });
        Button btNotGoing = mActivity.findViewById(R.id.event_btn_not_going);
        btNotGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventWorker.addUserToList(CardFeedEventController.LIST_NOT_GOING);
            }
        });

        final ImageButton ibOptions = mActivity.findViewById(R.id.btn_menu_options);
        ibOptions.setVisibility(View.GONE);

    }


    private void initViewCountDownToEvent(){
        Button btMoreInfo = mActivity.findViewById(R.id.event_btn_learn_more_countdown);
        btMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, FeedWebActivity.class);
                i.putExtra(mActivity.getResources().getString(R.string.KEY_EVENT_WEB_URL),mFeedEvent.getDetails_url());
                mActivity.startActivity(i);
                mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        Button btNotGoing = mActivity.findViewById(R.id.event_btn_not_going_countdown);
        btNotGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventWorker.addUserToList(CardFeedEventController.LIST_NOT_GOING);
            }
        });

        TextView tvCountDown = mActivity.findViewById(R.id.event_tv_count_down);
        long timeStamp = mFeedEvent.getDate_event_start();
        Date date = new Date(timeStamp);
        String day = DateUtils.getTimeStamp(date);
        tvCountDown.setText(day);

        final ImageButton ibOptions = mActivity.findViewById(R.id.btn_menu_options);
        ibOptions.setVisibility(View.GONE);

    }

    private void initViewBeforeEvent(){
        Button btCheckIn = mActivity.findViewById(R.id.btn_event_check_in);

        if(mEventWorker.isUserInList(CardFeedEventController.LIST_CHECKED_IN)){
            btCheckIn.setText(mActivity.getResources().getString(R.string.staff_feed_card_event_checked_in));
            btCheckIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showAlertDialog(mContext,
                            mActivity.getResources().getString(R.string.staff_feed_event_check_in_complete_waiting_title),
                            mActivity.getResources().getString(R.string.staff_feed_event_check_in_complete_waiting_summary));
                }
            });
        }else{
            btCheckIn.setText(mActivity.getResources().getString(R.string.staff_feed_card_event_check_in));
            btCheckIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEventWorker.addUserToList(CardFeedEventController.LIST_CHECKED_IN);
                }
            });
        }


        final ImageButton ibOptions = mActivity.findViewById(R.id.btn_menu_options);
        ibOptions.setVisibility(View.GONE);
    }


    private void initViewInEvent(){
        FABProgressCircle fabProgressCircleLocal = mActivity.findViewById(R.id.fabProgressCircle_in_event);
        mEventWorker.setupFabProgressCircle(fabProgressCircleLocal);

        ImageButton ibNavigateToOpenCamera = mActivity.findViewById(R.id.fab_in_event_take_photo);
        ibNavigateToOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HapticFeedbackUtils.init(mActivity);
                HapticFeedbackUtils.once(50);

                mListner.openImageGetterDialog(StaffFeedAdapter.FEED_EVENT, position);

            }
        });

        GridView gvPhotos = mActivity.findViewById(R.id.gridView_in_event_photo_gallery);
        TextView tvPhotosWarning = mActivity.findViewById(R.id.recycler_in_event_photo_gallery_empty_message);

        mEventWorker.setGridView(gvPhotos);
        mEventWorker.setWarningMessageView(tvPhotosWarning);
        mEventWorker.showPhotoGridView();

        ImageButton ibExpandCard = mActivity.findViewById(R.id.btn_menu_expand);
        ibExpandCard.setVisibility(View.VISIBLE);
        //Todo

        Button btNavigateToRoster = mActivity.findViewById(R.id.btn_in_event_check_roster);
        btNavigateToRoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, CardFeedEventRosterActivity.class);
                i.putExtra(mActivity.getResources().getString(R.string.KEY_EVENT_HEADER_TITLE),mActivity.getResources().getString(R.string.staff_feed_event_roster_title_active));
                mActivity.startActivity(i);
                mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });


    }

    private void initViewReminisce(){
        FABProgressCircle fabProgressCircleLocal = mActivity.findViewById(R.id.fabProgressCircle_after_event);
        mEventWorker.setupFabProgressCircle(fabProgressCircleLocal);

        ImageButton ibNavigateToOpenCamera = mActivity.findViewById(R.id.fab_after_event_add_from_gallery);
        ibNavigateToOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HapticFeedbackUtils.init(mActivity);
                HapticFeedbackUtils.once(50);

                mListner.openImageGetterDialog(StaffFeedAdapter.FEED_EVENT, position);

            }
        });

        GridView gvPhotos = mActivity.findViewById(R.id.gridView_after_event_photo_gallery);
        TextView tvPhotosWarning = mActivity.findViewById(R.id.recycler_after_event_photo_gallery_empty_message);

        mEventWorker.setGridView(gvPhotos);
        mEventWorker.setWarningMessageView(tvPhotosWarning);
        mEventWorker.showPhotoGridView();

        final ImageButton ibOptions = mActivity.findViewById(R.id.btn_menu_options);
        ibOptions.setVisibility(View.VISIBLE);

        ibOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext,ibOptions);
                popupMenu.getMenuInflater().inflate(R.menu.menu_staff_feed_item_card_event_reminisce,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        DialogUtils.showToast(mContext,"Future");
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        Button btNavigateToRoster = mActivity.findViewById(R.id.btn_after_event_check_roster);
        btNavigateToRoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, CardFeedEventRosterActivity.class);
                i.putExtra(mActivity.getResources().getString(R.string.KEY_EVENT_HEADER_TITLE),mActivity.getResources().getString(R.string.staff_feed_event_roster_title_closed));
                mActivity.startActivity(i);
                mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    private void initViewHide(){
        final ImageButton ibOptions = mActivity.findViewById(R.id.btn_menu_options);
        ibOptions.setVisibility(View.VISIBLE);

        ibOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext,ibOptions);
                popupMenu.getMenuInflater().inflate(R.menu.menu_staff_feed_item_card_event_hide,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_hide_item1_id:
                                mEventWorker.addUserToList(CardFeedEventController.LIST_GOING);
                                break;

                            case R.id.menu_hide_item2_id:
                                DialogUtils.showToast(mContext,"Future");
                                break;
                        }

                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    //----------------------------------------------------------------------------------------------//

    public void setSingleImageUri(int position, Uri singleImageUri) {
        Feed feed = mFeedList.get(position);
        FeedEvent event = feed.getEvent();

        this.singleImageUri = singleImageUri;

        mEventWorker.setUserUploadedEventPictureUri(singleImageUri);
        mEventWorker.setUserUploadedEventPictureBitmap(null);

        mEventWorker.startEventPictureUploadToCloud();

    }

    public void setSingleImageBitmap(int position, Bitmap singleImageBitmap) {
        this.singleImageBitmap = singleImageBitmap;

        mEventWorker.setUserUploadedEventPictureUri(null);
        mEventWorker.setUserUploadedEventPictureBitmap(singleImageBitmap);

        mEventWorker.startEventPictureUploadToCloud();


    }

    //----------------------------------------------------------------------------------------------

}
