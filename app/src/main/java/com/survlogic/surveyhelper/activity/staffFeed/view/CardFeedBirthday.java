package com.survlogic.surveyhelper.activity.staffFeed.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;
import com.survlogic.surveyhelper.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;

public class CardFeedBirthday extends RecyclerView.ViewHolder  {

    private static final String TAG = "CardFeedBirthday";

    private Context mContext;
    private StaffFeedAdapter.AdapterListener mListner;
    private int mBirthdayType;
    public final static int TYPE_TODAY = 0, TYPE_FUTURE = 1;

    private ImageView ivBackground;
    private ProgressBar pbBackground;

    private TextView tvUserName, tvBirthdate;


    public CardFeedBirthday(@NonNull View itemView, Context context, StaffFeedAdapter.AdapterListener listener, int type) {
        super(itemView);

        this.mContext = context;
        this.mListner = listener;
        this.mBirthdayType = type;

        initViewWidgets();

    }

    private void initViewWidgets(){
        ivBackground = itemView.findViewById(R.id.birthday_user_profile_pic);
        pbBackground = itemView.findViewById(R.id.birthday_user_profile_progress);

        tvUserName = itemView.findViewById(R.id.birthday_user_name);


        if(mBirthdayType == TYPE_FUTURE){
            tvBirthdate = itemView.findViewById(R.id.birthday_date_by_day);
        }

    }

    public void configureViewHolder(ArrayList<Feed> mFeedList, int position, boolean isToday){
        Feed feed = mFeedList.get(position);
        FeedBirthday birthday = feed.getBirthday();

        tvUserName.setText(birthday.getProfile_name());

        if(!isToday){
            long dateLong = birthday.getBirthDate();
            Date date = new Date(dateLong);
            String day = DateUtils.getTimeStamp(date);

            tvBirthdate.setText(day);
        }


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(birthday.getUserProfilePicUrl(), ivBackground, new ImageLoadingListener() {
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
