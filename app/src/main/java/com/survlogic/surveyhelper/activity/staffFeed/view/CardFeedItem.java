package com.survlogic.surveyhelper.activity.staffFeed.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedItem;
import com.survlogic.surveyhelper.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;

public class CardFeedItem extends RecyclerView.ViewHolder  {

    private static final String TAG = "CardFeedItem";

    private Context mContext;
    private Activity mActivity;
    private StaffFeedAdapter.AdapterListener mListner;

    private LinearLayout llPhotoGallery;
    private RecyclerView rvPhotoGallery;

    private ImageView ivProfileImage;
    private ProgressBar pbProfileImage;

    private ImageView ivFeedTypeImageBackground, ivFeedTypeImage;

    private TextView tvUserName, tvPostDate, tvPostDetails;

    private final static int ITEM_SYSTEM_DEFAULT = 0,
            ITEM_GEO_TAG_PRJ = 1001,
            ITEM_CHECK_IN_PRJ = 1002;

    public CardFeedItem(@NonNull View itemView, Context context, StaffFeedAdapter.AdapterListener listener) {
        super(itemView);

        this.mContext = context;
        mActivity = (Activity) context;

        this.mListner = listener;

        initViewWidgets();

    }

    private void initViewWidgets(){
        ivProfileImage = itemView.findViewById(R.id.feed_user_profile_pic);
        pbProfileImage = itemView.findViewById(R.id.feed_user_profile_progress);

        ivFeedTypeImage = itemView.findViewById(R.id.feed_item_background_type);
        ivFeedTypeImageBackground = itemView.findViewById(R.id.feed_item_background);

        tvUserName = itemView.findViewById(R.id.feed_user_name);
        tvPostDate = itemView.findViewById(R.id.feed_post_date);
        tvPostDetails = itemView.findViewById(R.id.feed_post_body);

        llPhotoGallery = itemView.findViewById(R.id.feed_ll_post_pictures);
        rvPhotoGallery = itemView.findViewById(R.id.feed_post_pictures);

    }

    public void configureViewHolder(ArrayList<Feed> mFeedList, int position){
        Feed feed = mFeedList.get(position);
        FeedItem item = feed.getItem();

        tvUserName.setText(item.getDisplay_name());
        tvPostDetails.setText(item.getExtra_entry());

        Timestamp timeStamp = item.getPostedOn();
        Date date = timeStamp.toDate();

        String day = DateUtils.getTimeStamp(date);
        tvPostDate.setText(day);

        switch (item.getFeed_post_type()){
            case ITEM_SYSTEM_DEFAULT:
                break;

            case ITEM_GEO_TAG_PRJ:
                break;

            case ITEM_CHECK_IN_PRJ:
                ivFeedTypeImage.setImageDrawable(ContextCompat.getDrawable(mActivity,R.drawable.bm_project_feed_item_check_in));
                ivFeedTypeImageBackground.setImageDrawable(ContextCompat.getDrawable(mActivity,R.drawable.background_triangle_filled_dark));

                break;

            default:
                break;

        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(item.getUser_profile_pic_url(), ivProfileImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(pbProfileImage !=null){
                    pbProfileImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(pbProfileImage !=null){
                    pbProfileImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(pbProfileImage !=null){
                    pbProfileImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(pbProfileImage !=null){
                    pbProfileImage.setVisibility(View.GONE);
                }
            }
        });

    }

}
