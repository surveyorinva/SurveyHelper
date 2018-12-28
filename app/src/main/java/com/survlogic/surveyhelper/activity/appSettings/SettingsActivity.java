package com.survlogic.surveyhelper.activity.appSettings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appSettings.fragment.GeneralSettingsFragment;
import com.survlogic.surveyhelper.activity.appSettings.fragment.ProfileSettingsFragment;
import com.survlogic.surveyhelper.activity.appSettings.inter.SettingsListener;
import com.survlogic.surveyhelper.adapters.ViewPagerAdapter;
import com.survlogic.surveyhelper.utils.BaseActivity;

public class SettingsActivity extends BaseActivity implements SettingsListener {

    private static final String TAG = "SettingsActivity";

    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private ViewPagerAdapter viewPagerAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton ibAppBarBack;

    private String parentClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_main);

        mContext = SettingsActivity.this;
        mAuth = FirebaseAuth.getInstance();

        initView();
        setOnClickListeners();

        initViewPager();

        getIntentDelivery();

    }

    private void getIntentDelivery(){
        parentClass = getIntent().getStringExtra(getString(R.string.KEY_PARENT_ACTIVITY));

    }


    private Bundle getExtrasFromVariables(){
        Log.d(TAG, "getExtrasFromVariables: Started...");
        Bundle extras = new Bundle();
        extras.putString(getString(R.string.KEY_PARENT_ACTIVITY),parentClass);

        return extras;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCurrentUser = mAuth.getCurrentUser();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

    }

    private void initView(){
        TextView tvAppBarTitle = findViewById(R.id.appBar_top_title);
        tvAppBarTitle.setText(getResources().getString(R.string.general_menu_settings));
        tvAppBarTitle.setVisibility(View.VISIBLE);

        ibAppBarBack = findViewById(R.id.appBar_top_action_nav_back);
        ibAppBarBack.setVisibility(View.VISIBLE);

        tabLayout = findViewById(R.id.login_content_tab_layout);
        viewPager = findViewById(R.id.login_content_view_pager);

    }

    private void setOnClickListeners(){
        ibAppBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initViewPager(){

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        GeneralSettingsFragment generalSettingsFragment = new GeneralSettingsFragment();
        generalSettingsFragment.setArguments(getExtrasFromVariables());

        ProfileSettingsFragment profileSettingsFragment = new ProfileSettingsFragment();
        profileSettingsFragment.setArguments(getExtrasFromVariables());

        viewPagerAdapter.addFragments(generalSettingsFragment,getResources().getString(R.string.appSettings_tab_name_general));
        viewPagerAdapter.addFragments(profileSettingsFragment,getResources().getString(R.string.appSettings_tab_name_profile));

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                final float normalizedPosition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedPosition / 2 + 0.5f);
                page.setScaleY(normalizedPosition / 2 + 0.5f);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

    }
}
