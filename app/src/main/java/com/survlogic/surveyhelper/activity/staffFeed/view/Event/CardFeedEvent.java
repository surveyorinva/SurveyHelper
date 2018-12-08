package com.survlogic.surveyhelper.activity.staffFeed.view.Event;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CardFeedEvent extends RecyclerView.ViewHolder implements CardFeedEventWorker.CardFeedEventWorkerListener {

    private static final String TAG = "CardFeedAnnouncement";

    @Override
    public void returnSuccessful() {

    }

    @Override
    public void returnInError(boolean isError) {

    }

    private Context mContext;
    private StaffFeedAdapter.AdapterListener mListner;

    private ImageView ivBackground;
    private ProgressBar pbBackground;
    private TextView tvHeader, tvDetails, tvStartDate, tvStartTime, tvLocation;
    private LinearLayout llRootPhase, llPhaseI_Intro, llPhaseII_Count_Down;
    private ConstraintLayout clPhaseIII_Check_In, clPhaseIV_In_Event, clPhaseV_After_Event;

    private boolean isEventWorkerSetup = false;
    private int currentPhaseShown = 0;
    private CardFeedEventWorker mEventWorker;

    public static final int EVENT_SYSTEM_ERROR = 0,
                            EVENT_PHASE_INTRO = 1,
                            EVENT_PHASE_COUNTDOWN_DAYS = 2,
                            EVENT_PHASE_DAY_OF_EVENT =  3,
                            EVENT_PHASE_IN_EVENT = 4,
                            EVENT_PHASE_REMINISCE = 5,
                            EVENT_PHASE_OVER = 6,
                            EVENT_PHASE_HIDE = 7;

    public CardFeedEvent(@NonNull View itemView, Context context, StaffFeedAdapter.AdapterListener listener) {
        super(itemView);

        this.mContext = context;
        this.mListner = listener;

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
            mEventWorker = new CardFeedEventWorker(mContext, this);
            isEventWorkerSetup = true;
        }
    }

    public void configureViewHolder(ArrayList<Feed> mFeedList, int position){
        Feed feed = mFeedList.get(position);
        FeedEvent event = feed.getEvent();
        mEventWorker.setEvent(event);

        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        Activity mActivity = (Activity) mContext;

        if(preferenceLoader.isFeedEventStyleLight()){
            tvHeader.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextPrimary, null));
            tvDetails.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextPrimary, null));
        }else{
            tvHeader.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextSecondary, null));
            tvDetails.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextPrimary, null));
        }

        tvHeader.setText(event.getEvent_header());
        tvDetails.setText(event.getEvent_body());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a");

        Date dateStart = new Date(event.getDate_event_start());
        Date dateEnd = new Date(event.getDate_event_end());

        String startDateFormatted = dateFormat.format(dateStart);
        String startTimeFormatted = hourFormat.format(dateStart);
        String endTimeFormatted = hourFormat.format(dateEnd);

        tvStartDate.setText(startDateFormatted);
        tvStartTime.setText(mActivity.getResources().getString(
                                                R.string.staff_feed_card_event_time_of_event_fmt,
                                                startTimeFormatted,
                                                endTimeFormatted));

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(event.getBackground_url(), ivBackground, new ImageLoadingListener() {
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
                    break;

                case EVENT_PHASE_COUNTDOWN_DAYS:
                    llPhaseII_Count_Down.setVisibility(View.VISIBLE);
                    currentPhaseShown = EVENT_PHASE_COUNTDOWN_DAYS;
                    break;

                case EVENT_PHASE_DAY_OF_EVENT:
                    clPhaseIII_Check_In.setVisibility(View.VISIBLE);
                    currentPhaseShown = EVENT_PHASE_DAY_OF_EVENT;
                    break;

                case EVENT_PHASE_IN_EVENT:
                    clPhaseIV_In_Event.setVisibility(View.VISIBLE);
                    currentPhaseShown = EVENT_PHASE_IN_EVENT;
                    break;

                case EVENT_PHASE_REMINISCE:
                    clPhaseV_After_Event.setVisibility(View.VISIBLE);
                    currentPhaseShown = EVENT_PHASE_REMINISCE;
                    break;

                case EVENT_PHASE_OVER:
                    break;

                case EVENT_PHASE_HIDE:
                    break;

            }
        }

    }

}
