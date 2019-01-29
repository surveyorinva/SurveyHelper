package com.survlogic.surveyhelper.activity.staffFeed.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.utils.BaseActivity;

public class FeedRoomActivity extends BaseActivity {

    private static final String TAG = "FeedRoomActivity";
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_feed_room_activity);

        mContext = FeedRoomActivity.this;

        initView();
    }

    private void initView(){

    }
}
