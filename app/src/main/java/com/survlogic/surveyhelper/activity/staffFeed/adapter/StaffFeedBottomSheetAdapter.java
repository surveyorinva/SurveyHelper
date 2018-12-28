package com.survlogic.surveyhelper.activity.staffFeed.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;
import com.survlogic.surveyhelper.activity.staffFeed.view.actions.CardFeedActions;
import com.survlogic.surveyhelper.activity.staffFeed.view.CardFeedEmpty;
import com.survlogic.surveyhelper.activity.staffFeed.view.reflection.CardFeedReflections;
import com.survlogic.surveyhelper.activity.staffFeed.view.reflection.CardFeedReflectionsHeader;

import java.util.ArrayList;

public class StaffFeedBottomSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private static final String TAG = "StaffFeedBottomSheetAda";

    private Context mContext;
    private ArrayList<FeedActions> mActionList;

    private final static int    FEED_SYSTEM_EMPTY = 1,
                                FEED_ACTIONS = 1001,
                                FEED_REFLECTIONS_HEADER = 2001,
                                FEED_REFLECTIONS = 2002;

    public StaffFeedBottomSheetAdapter(Context context, ArrayList<FeedActions> actionList) {
        this.mContext = context;
        this.mActionList = actionList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case FEED_ACTIONS:
                View vAction = mInflater.inflate(R.layout.staff_feed_bottom_sheet_content_card_actions,parent,false);
                viewHolder = new CardFeedActions(vAction, mContext);
                break;

            case FEED_REFLECTIONS_HEADER:
                View vReflectionHeader = mInflater.inflate(R.layout.staff_feed_bottom_sheet_content_card_header_reflections,parent,false);
                viewHolder = new CardFeedReflectionsHeader(vReflectionHeader);
                break;

            case FEED_REFLECTIONS:
                View vReflection = mInflater.inflate(R.layout.staff_feed_bottom_sheet_content_card_reflections,parent,false);
                viewHolder = new CardFeedReflections(vReflection, mContext);
                break;

            case FEED_SYSTEM_EMPTY:
                View vEmpty = mInflater.inflate(R.layout.staff_feed_bottom_sheet_content_card_empty,parent,false);
                viewHolder = new CardFeedEmpty(vEmpty);
                break;

            default:
                View v = mInflater.inflate(R.layout.staff_feed_bottom_sheet_content_card_actions,parent,false);
                viewHolder = new CardFeedActions(v, mContext);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        FeedActions feedActions = mActionList.get(position);
        return feedActions.getFeed_action_type();
    }

    public boolean isFullCard(int position){
        FeedActions feedActions = mActionList.get(position);
        return feedActions.getFeed_action_type() != FEED_ACTIONS;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch(viewHolder.getItemViewType()){
            case FEED_ACTIONS:
                CardFeedActions vhActions = (CardFeedActions) viewHolder;
                vhActions.configureViewHolder(mActionList,position);
                break;

            case FEED_REFLECTIONS:
                CardFeedReflections vhReflections = (CardFeedReflections) viewHolder;
                vhReflections.configureViewHolder(mActionList,position);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return mActionList == null ? 0 : mActionList.size();
    }

    public void swapItems(ArrayList<FeedActions> items){
        this.mActionList = items;
        notifyDataSetChanged();
    }


}
