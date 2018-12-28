package com.survlogic.surveyhelper.activity.staffFeed.view.announcement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedAnnouncement;
import com.survlogic.surveyhelper.activity.staffFeed.activity.FeedWebActivity;

import java.util.ArrayList;

public class CardFeedAnnouncement extends RecyclerView.ViewHolder  {

    private static final String TAG = "CardFeedAnnouncement";

    private Context mContext;
    private StaffFeedAdapter.AdapterListener mListner;

    private ImageView ivBackground;
    private TextView tvHeader, tvDetails;
    private Button btDetails;
    private ProgressBar pbBackground;

    public CardFeedAnnouncement(@NonNull View itemView, Context context, StaffFeedAdapter.AdapterListener listener) {
        super(itemView);

        this.mContext = context;
        this.mListner = listener;

        initViewWidgets();

    }

    private void initViewWidgets(){
        ivBackground = itemView.findViewById(R.id.announcement_image_background);
        pbBackground = itemView.findViewById(R.id.announcement_image_background_progress);

        tvHeader = itemView.findViewById(R.id.event_text_header);
        tvDetails = itemView.findViewById(R.id.announcement_body);

        btDetails = itemView.findViewById(R.id.announcement_btn_learn_more);

    }

    public void configureViewHolder(ArrayList<Feed> mFeedList, int position){
        Feed feed = mFeedList.get(position);

        Activity activity = (Activity) mContext;

        final FeedAnnouncement announcement = feed.getAnnouncement();
        final String announcementTitle = activity.getResources().getString(R.string.staff_feed_event_web_view_announcement_all_about);

        tvHeader.setText(announcement.getNews_header());
        tvDetails.setText(announcement.getNews_body());

        btDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) mContext;
                Intent i = new Intent(activity, FeedWebActivity.class);
                i.putExtra(activity.getResources().getString(R.string.KEY_EVENT_WEB_URL),announcement.getDetails_url());
                i.putExtra(activity.getResources().getString(R.string.KEY_EVENT_HEADER_TITLE),announcementTitle);
                activity.startActivity(i);
                activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(announcement.getBackground_url(), ivBackground, new ImageLoadingListener() {
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
