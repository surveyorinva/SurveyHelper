package com.survlogic.surveyhelper.activity.staffFeed.view.actions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;
import com.survlogic.surveyhelper.activity.staffFeed.view.message.activity.FeedMessageNewActivity;
import com.survlogic.surveyhelper.activity.staffFeed.workers.BottomSheetCompiler;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.ArrayList;

public class CardFeedActions extends RecyclerView.ViewHolder  {

    private static final String TAG = "CardFeedActions";
    private Context mContext;
    private Activity mActivity;
    private Button btAction;
    BottomSheetCompiler.RoomActions roomAction;


    public CardFeedActions(@NonNull View itemView, Context context) {
        super(itemView);

        this.mContext = context;
        this.mActivity = (Activity) context;

        initViewWidgets();
    }

    private void initViewWidgets(){
        btAction = itemView.findViewById(R.id.button_action);
    }

    public void configureViewHolder(ArrayList<FeedActions> mActionList, int position){
        FeedActions feedAction = mActionList.get(position);
        roomAction = feedAction.getRoomActions();

        btAction.setText(roomAction.getRoomActionName());
        Drawable iconDrawable = changeDrawableColor(roomAction.getRoomActionDrawable(),roomAction.getRoomActionColor());

        btAction.setCompoundDrawablesRelativeWithIntrinsicBounds(iconDrawable,null,null,null);

        btAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewHolderItem();
            }
        });
    }


    private static Drawable changeDrawableColor(Drawable drawable, int newColor) {
        drawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        return drawable;
    }

    private void onClickViewHolderItem(){
        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        String roomToPost = preferenceLoader.getFeedCurrentActiveRoom();

        Intent i = new Intent(mActivity, FeedMessageNewActivity.class);
        i.putExtra(mActivity.getResources().getString(R.string.KEY_MESSAGE_CLASS),2);
        i.putExtra(mActivity.getResources().getString(R.string.KEY_MESSAGE_ROOM),roomToPost);

        mActivity.startActivityForResult(i,101);
        mActivity.overridePendingTransition(R.anim.anim_feed_message_slide_in_from_bottom, R.anim.anim_activity_on_screen);
    }

}
