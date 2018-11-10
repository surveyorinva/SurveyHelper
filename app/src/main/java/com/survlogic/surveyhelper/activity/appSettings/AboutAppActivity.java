package com.survlogic.surveyhelper.activity.appSettings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appIntro.IntroActivity;
import com.survlogic.surveyhelper.utils.BaseActivity;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

public class AboutAppActivity extends BaseActivity {

    private static final String TAG = "AboutAppActivity";
    private Context mContext;
    private PreferenceLoader mPreferenceLoader;

    private LabeledSwitch swItem1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_about);
        Log.d(TAG, "to_delete:About Activity-------------------------------------------------------------> ");

        mContext = AboutAppActivity.this;
        mPreferenceLoader = new PreferenceLoader(mContext);

        initView();

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
        tvAppBarTitle.setText(getResources().getString(R.string.general_menu_about));
        tvAppBarTitle.setVisibility(View.VISIBLE);

        swItem1 = findViewById(R.id.about_item_1_switch);

        boolean isAppFirstRun = mPreferenceLoader.getSettings().isAppFirstRun();
        if(isAppFirstRun){
            swItem1.setOn(false);
        }else{
            swItem1.setOn(true);
        }

        swItem1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                if(isOn){
                    mPreferenceLoader.setAppFirstRun(false,true);
                }else{
                    mPreferenceLoader.setAppFirstRun(true, true);
                    startFirstStartActivity();
                }

            }
        });
    }

    private void startFirstStartActivity(){
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }

}
