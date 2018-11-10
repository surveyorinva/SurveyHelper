package com.survlogic.surveyhelper.activity.appLogin;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appLogin.fragment.SignInFragment;
import com.survlogic.surveyhelper.activity.appLogin.fragment.SignUpFragment;
import com.survlogic.surveyhelper.activity.appLogin.fragment.SignUpLockedFragment;
import com.survlogic.surveyhelper.activity.appLogin.inter.LoginActivityListener;
import com.survlogic.surveyhelper.adapters.ViewPagerAdapter;
import com.survlogic.surveyhelper.utils.RemoteConfigLoader;
import com.survlogic.surveyhelper.views.NonSwipeableViewPager;

public class LoginActivity extends AppCompatActivity implements LoginActivityListener, RemoteConfigLoader.RemoteConfigLoaderListener {

    private static final String TAG = "LoginActivity";

    @Override
    public void isLoaded(boolean isLoaded) {
        initView();
        initAnimations();
        initOnClickListeners();

        initViewPager();
    }

    private Context mContext;
    private RemoteConfigLoader remoteConfigLoader;

    private RelativeLayout rlRootWelcome;
    private RelativeLayout rlRootSignIn;

    private ImageView ivLoginHeader;

    private Button btnLoginIn, btnSignUp;
    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    private Animation animShowSignIn, animHideWelcome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_main);

        mContext = LoginActivity.this;
        remoteConfigLoader = new RemoteConfigLoader(mContext, this);

    }

    private void initView(){

        rlRootWelcome = findViewById(R.id.rlroot_login_main);
        rlRootWelcome.setVisibility(View.VISIBLE);

        rlRootSignIn = findViewById(R.id.rlroot_login_signup);
        rlRootSignIn.setVisibility(View.INVISIBLE);

        ((ViewGroup) findViewById(R.id.rlroot_login_signup)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        ivLoginHeader = findViewById(R.id.login_content_header_image);

        btnLoginIn = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_signup);

        tabLayout = findViewById(R.id.login_content_tab_layout);
        viewPager = findViewById(R.id.login_content_view_pager);



    }

    private void initAnimations(){
        animShowSignIn = AnimationUtils.loadAnimation(this,R.anim.anim_login_show_signin);
        animShowSignIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        animHideWelcome = AnimationUtils.loadAnimation(this,R.anim.anim_login_hide_welcome);
        animHideWelcome.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlRootWelcome.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initOnClickListeners(){
        btnLoginIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginScreen();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterScreen();
            }
        });

    }

    private void initViewPager(){

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        final SignInFragment signInFragment = new SignInFragment();
        SignUpFragment signUpFragment = new SignUpFragment();
        SignUpLockedFragment signUpLockedFragment = new SignUpLockedFragment();

        viewPagerAdapter.addFragments(signInFragment,getResources().getString(R.string.general_login));

        boolean isSignUpAvailable = remoteConfigLoader.fetchRemoteConfigSignUpEnabled();

        if(isSignUpAvailable){
            viewPagerAdapter.addFragments(signUpFragment,getResources().getString(R.string.general_signUp));
        }else {
            viewPagerAdapter.addFragments(signUpLockedFragment,getResources().getString(R.string.general_locked));
        }


        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ivLoginHeader.setVisibility(View.VISIBLE);

                } else {
                    ivLoginHeader.setVisibility(View.GONE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

    }

    private void showLoginScreen(){
        animCrossFadeLoginView();

        viewPager.setCurrentItem(0);

    }

    private void showRegisterScreen(){
        animCrossFadeLoginView();

        viewPager.setCurrentItem(1);
    }

    private void animCrossFadeLoginView(){
        rlRootSignIn.setVisibility(View.VISIBLE);

        rlRootSignIn.startAnimation(animShowSignIn);
        rlRootWelcome.startAnimation(animHideWelcome);


    }

    @Override
    public void returnResults(boolean isLoginSuccessful) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.KEY_LOGIN_SUCCESSFUL),isLoginSuccessful);

        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void recreateActivity() {
        recreate();
    }
}
