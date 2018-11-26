package com.survlogic.surveyhelper.activity.staffFeed.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedReflections;
import com.survlogic.surveyhelper.activity.staffFeed.workers.BottomSheetCompiler;

import java.util.ArrayList;

public class CardFeedReflections extends RecyclerView.ViewHolder  {

    private static final String TAG = "CardFeedReflections";
    private Context mContext;

    private TextView tvSummary;

    public CardFeedReflections(@NonNull View itemView, Context context) {
        super(itemView);

        this.mContext = context;
        initViewWidgets();
    }

    private void initViewWidgets(){
        tvSummary = itemView.findViewById(R.id.reflection_name);
    }

    public void configureViewHolder(ArrayList<FeedActions> mActionList, int position){
        FeedActions feedAction = mActionList.get(position);
        FeedReflections reflection = feedAction.getFeedReflections();

        tvSummary.setText(reflection.getSummary());

    }


}
