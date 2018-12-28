package com.survlogic.surveyhelper.activity.staffFeed.view.event.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.utils.BaseActivity;

public class CardFeedEventRosterActivity extends BaseActivity {

    private static final String TAG = "CardFeedEventWebActivit";

    private Context mContext;

    private String mActivityTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_feed_item_event_activity_roster);

        mContext = CardFeedEventRosterActivity.this;
        getIntentDelivery();

        initView();

    }

    private void getIntentDelivery(){
        mActivityTitle = getIntent().getStringExtra(getString(R.string.KEY_EVENT_HEADER_TITLE));

    }

    private void initView(){
        ImageButton ibAppBarBack = findViewById(R.id.appBar_top_action_nav_back);
        ibAppBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ibAppBarBack.setVisibility(View.VISIBLE);

        TextView tvAppBarTitle = findViewById(R.id.appBar_top_title);
        tvAppBarTitle.setText(mActivityTitle);
    }

}
