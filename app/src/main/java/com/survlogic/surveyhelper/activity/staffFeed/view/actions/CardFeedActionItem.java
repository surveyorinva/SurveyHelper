package com.survlogic.surveyhelper.activity.staffFeed.view.actions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.view.message.activity.FeedMessageNewActivity;
import com.survlogic.surveyhelper.model.AppUserClient;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

public class CardFeedActionItem  extends RecyclerView.ViewHolder  {

    private Context mContext;
    private Activity mActivity;
    private StaffFeedAdapter.AdapterListener mListner;

    public CardFeedActionItem(@NonNull View itemView, Context context, StaffFeedAdapter.AdapterListener listener) {
        super(itemView);

        this.mContext = context;
        mActivity = (Activity) context;

        this.mListner = listener;

        initViewWidgets();
    }


    public void configureViewHolder(int position){
        //Nothing here
    }

    private void initViewWidgets(){
        FirestoreUser user = ((AppUserClient) mActivity.getApplicationContext()).getUser();

        ImageView ivProfilePic = itemView.findViewById(R.id.profile_current_user_image);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(user.getProfile_pic_url(), ivProfilePic);


        TextView tvDefaultMessage = itemView.findViewById(R.id.default_message_in_action_card);

        Button btAction = itemView.findViewById(R.id.item_action_button);
        btAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewHolderItem();
            }
        });

    }
    private void onClickViewHolderItem(){
        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        String roomToPost = preferenceLoader.getFeedCurrentActiveRoom();

        Intent i = new Intent(mActivity, FeedMessageNewActivity.class);
        i.putExtra(mActivity.getResources().getString(R.string.KEY_MESSAGE_CLASS),2);
        i.putExtra(mActivity.getResources().getString(R.string.KEY_MESSAGE_ROOM),roomToPost);

        mActivity.startActivityForResult(i,101);
        mActivity.overridePendingTransition(R.anim.anim_popup_explode_on_enter, R.anim.anim_activity_on_screen);
    }

}
