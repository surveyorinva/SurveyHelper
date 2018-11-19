package com.survlogic.surveyhelper.activity.staffFeed.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.Distribution;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.ArrayList;

public class CardFeedEvent extends RecyclerView.ViewHolder  {

    private static final String TAG = "CardFeedAnnouncement";

    private Context mContext;
    private StaffFeedAdapter.AdapterListener mListner;

    private ImageView ivBackground;
    private ProgressBar pbBackground;
    private TextView tvHeader, tvDetails, tvStartDate, tvStartTime, tvLocation;
    private LinearLayout llInvetation, llPending, llAfter;
    private Button btLearnMore;

    public CardFeedEvent(@NonNull View itemView, Context context, StaffFeedAdapter.AdapterListener listener) {
        super(itemView);

        this.mContext = context;
        this.mListner = listener;

        initViewWidgets();

    }

    private void initViewWidgets(){

        ivBackground = itemView.findViewById(R.id.event_image);
        pbBackground = itemView.findViewById(R.id.event_image_progress);

        tvHeader = itemView.findViewById(R.id.event_text_header);
        tvDetails = itemView.findViewById(R.id.event_body);
        tvStartDate = itemView.findViewById(R.id.event_date_start);
        tvStartTime = itemView.findViewById(R.id.event_date_time);

        tvLocation = itemView.findViewById(R.id.event_location);

        llInvetation = itemView.findViewById(R.id.ll_event_actions_invitation);
        llPending = itemView.findViewById(R.id.ll_event_actions_pending);
        llAfter = itemView.findViewById(R.id.ll_event_actions_after);

        btLearnMore = itemView.findViewById(R.id.event_btn_learn_more);
    }

    public void configureViewHolder(ArrayList<Feed> mFeedList, int position){
        Feed feed = mFeedList.get(position);
        FeedEvent event = feed.getEvent();

        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        
        if(preferenceLoader.isFeedEventStyleLight()){
            tvHeader.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextPrimary, null));
            tvDetails.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextPrimary, null));
        }else{
            tvHeader.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextSecondary, null));
            tvDetails.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorTextPrimary, null));
        }

        tvHeader.setText(event.getEvent_header());
        tvDetails.setText(event.getEvent_body());

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

    }
}
