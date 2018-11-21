package com.survlogic.surveyhelper.activity.staffFeed.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;
import com.survlogic.surveyhelper.activity.staffFeed.workers.GeneratorRoomActions;

import java.util.ArrayList;

public class CardFeedActions extends RecyclerView.ViewHolder  {

    private static final String TAG = "CardFeedActions";
    private Context mContext;

    private Button btAction;

    public CardFeedActions(@NonNull View itemView, Context context) {
        super(itemView);

        this.mContext = context;
        initViewWidgets();
    }

    private void initViewWidgets(){
        btAction = itemView.findViewById(R.id.button_action);
    }

    public void configureViewHolder(ArrayList<FeedActions> mActionList, int position){
        FeedActions feedAction = mActionList.get(position);
        GeneratorRoomActions.RoomActions roomAction = feedAction.getRoomActions();

        btAction.setText(roomAction.getRoomActionName());
        Drawable iconDrawable = changeDrawableColor(roomAction.getRoomActionDrawable(),roomAction.getRoomActionColor());

        btAction.setCompoundDrawablesRelativeWithIntrinsicBounds(iconDrawable,null,null,null);
    }


    private static Drawable changeDrawableColor(Drawable drawable, int newColor) {
        drawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        return drawable;
    }

}
