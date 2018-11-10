package com.survlogic.surveyhelper.activity.appIntro;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appIntro.fragment.IntroFragment;
import com.survlogic.surveyhelper.activity.appSetup.SetupActivity;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(IntroFragment.newInstance(R.layout.intro_fragment_page_1));
        addSlide(IntroFragment.newInstance(R.layout.intro_fragment_page_2));
        addSlide(IntroFragment.newInstance(R.layout.intro_fragment_page_3));
        addSlide(IntroFragment.newInstance(R.layout.intro_fragment_page_4));
        addSlide(IntroFragment.newInstance(R.layout.intro_fragment_page_finish));

        setPermissions();

        showSkipButton(false);
        setProgressButtonEnabled(true);

        setZoomAnimation();

        setVibrate(true);
        setVibrateIntensity(30);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent i = new Intent(this, SetupActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    //----------------------------------------------------------------------------------------------//

    private void setPermissions(){
        // PERMISSIONS

        askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

        askForPermissions(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS},3);

        askForPermissions(new String[]{Manifest.permission.CAMERA}, 4);
    }


}
