package com.survlogic.surveyhelper.activity.staffFeed.view;

import android.content.Context;
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
        FeedAnnouncement announcement = feed.getAnnouncement();

        tvHeader.setText(announcement.getNews_header());
        tvDetails.setText(announcement.getNews_body());

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
