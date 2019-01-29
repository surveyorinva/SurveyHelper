package com.survlogic.surveyhelper.activity.staffFeed.view.reflection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedReflections;
import com.survlogic.surveyhelper.activity.staffFeed.view.message.activity.FeedMessageNewActivity;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.ArrayList;

public class CardFeedReflections extends RecyclerView.ViewHolder  {

    private static final String TAG = "CardFeedReflections";
    private Context mContext;
    private Activity mActivity;

    private FeedReflections mReflection;

    private TextView tvSummary;

    public CardFeedReflections(@NonNull View itemView, Context context) {
        super(itemView);

        this.mContext = context;
        this.mActivity = (Activity) context;

        initViewWidgets();
    }

    private void initViewWidgets(){
        tvSummary = itemView.findViewById(R.id.reflection_name);
    }

    public void configureViewHolder(ArrayList<FeedActions> mActionList, int position){
        FeedActions feedAction = mActionList.get(position);
        mReflection = feedAction.getFeedReflections();

        tvSummary.setText(mReflection.getSummary());
        tvSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewHolderItem();
            }
        });
    }

    private void onClickViewHolderItem(){
        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        String roomToPostReflection = preferenceLoader.getFeedPublicRoom();

        Intent i = new Intent(mActivity, FeedMessageNewActivity.class);
        i.putExtra(mActivity.getResources().getString(R.string.KEY_MESSAGE_CLASS),1);
        i.putExtra(mActivity.getResources().getString(R.string.KEY_EVENT_HEADER_TITLE),mReflection.getSummary());
        i.putExtra(mActivity.getResources().getString(R.string.KEY_FEED_PARCEL), mReflection);
        i.putExtra(mActivity.getResources().getString(R.string.KEY_MESSAGE_TYPE_ID),mReflection.getType());
        i.putExtra(mActivity.getResources().getString(R.string.KEY_MESSAGE_ROOM),roomToPostReflection);

        mActivity.startActivityForResult(i,100);
        mActivity.overridePendingTransition(R.anim.anim_feed_message_slide_in_from_bottom, R.anim.anim_activity_on_screen);
    }


}
