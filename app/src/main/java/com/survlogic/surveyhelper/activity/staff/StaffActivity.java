package com.survlogic.surveyhelper.activity.staff;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staff.controller.StaffController;
import com.survlogic.surveyhelper.activity.staffFeed.StaffFeedFragment;
import com.survlogic.surveyhelper.activity.staff.inter.StaffActivityListener;
import com.survlogic.surveyhelper.inter.NavigationHost;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.BaseActivity;
import com.survlogic.surveyhelper.utils.BottomNavigationViewLoader;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

public class StaffActivity extends BaseActivity implements  NavigationHost,
                                                            StaffController.StaffControllerListener,
                                                            StaffActivityListener {

    private static final String TAG = "StaffActivity";

    /**
     * NavigationHost
     */

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }


    /**
     * StaffController
     */

    @Override
    public void refreshUI() {

    }

    @Override
    public void returnCurrentUser(FirestoreUser currentUser) {
        this.mFirestoreUser = currentUser;

        if(mStaffFeedFragment !=null){
            mStaffFeedFragment.setFirestoreUser(mFirestoreUser);
        }

    }

    @Override
    public void returnErrorNoUserLoggedIn() {

    }

    /**
     * StaffActivityListener
     */

    @Override
    public void callNavigationDrawer() {
        callNavigationDrawerWorker();
    }

    private Context mContext;

    private StaffController mController;
    private StaffFeedFragment mStaffFeedFragment;

    private DrawerLayout dlSlidingLeftDrawer;
    private BottomNavigationViewEx bnvNavigationBar;
    private FloatingActionButton fabActionAnnouncement;
    private ImageButton ibAppBarCommandDrawer;

    private FirestoreUser mFirestoreUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_feed_activity);

        mContext = StaffActivity.this;
        mController = new StaffController(mContext,this);

        initFragmentViews(savedInstanceState);

        initView();

    }

    private void initView(){
        dlSlidingLeftDrawer = findViewById(R.id.widget_staff_feed_drawer);

        bnvNavigationBar = findViewById(R.id.appBar_bottom_navigation_view);
        BottomNavigationViewLoader.setupBottomNavigationStaff(bnvNavigationBar);
        BottomNavigationViewLoader.enableNavgiationStaff(mContext,bnvNavigationBar);

    }


    private void initFragmentViews(@Nullable Bundle savedInstanceState){
        if (savedInstanceState == null) {
            mStaffFeedFragment = new StaffFeedFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();

            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

            fragmentTransaction.replace(R.id.container,
                    mStaffFeedFragment).addToBackStack(null)
                    .commit();
        }
    }


    private void callNavigationDrawerWorker(){
        if(dlSlidingLeftDrawer.isDrawerOpen(GravityCompat.START)){
                    dlSlidingLeftDrawer.closeDrawer(GravityCompat.START);
                }else{
                    dlSlidingLeftDrawer.openDrawer(GravityCompat.START);
                }
    }

    //----------------------------------------------------------------------------------------------



}
