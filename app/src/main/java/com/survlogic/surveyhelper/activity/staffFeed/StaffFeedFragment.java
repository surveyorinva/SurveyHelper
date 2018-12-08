package com.survlogic.surveyhelper.activity.staffFeed;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.controller.StaffFeedController;
import com.survlogic.surveyhelper.activity.staff.inter.StaffActivityListener;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.Calendar;
import java.util.Date;

public class StaffFeedFragment extends Fragment implements StaffFeedController.StaffFeedControllerListener {
    private static final String TAG = "StaffFeedFragment";

    /**
     * StaffFeedControllerListener
     */

    @Override
    public void refreshFragmentUI() {

    }

    @Override
    public void sendFeedCategoryNameToAppBar(String feedCategory) {
        tvFragmentName.setText(feedCategory);
    }

    private Context mContext;

    private View v;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageButton ibBackdrop, ibNavigator, ibFeedNavigator;
    private TextView tvFragmentName;

    private StaffActivityListener mActivityListener;
    private StaffFeedController mFeedController;

    private StaffFeedFragment mThisFragment;
    private FirestoreUser mFirestoreUser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();

        mActivityListener = (StaffActivityListener) getActivity();
        mFeedController = new StaffFeedController(mContext,this,this);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.staff_feed_fragment, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

    }

    @Override
    public void onStart() {
        super.onStart();

        Date today = Calendar.getInstance().getTime();

        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        String feed_room = preferenceLoader.getFeedPublicRoom();

        mFeedController.setFeedQueryDateToShow(today);
        mFeedController.setFeedRoomToShow(feed_room);
        mFeedController.setFeedRoomPublic(feed_room);
        mFeedController.fetchPublicRoomFeeds();

        showAnnouncementAtStart();

    }

    private void initView(){
        TextView tvActivityName = v.findViewById(R.id.appBar_top_title);
        tvActivityName.setText(getActivity().getString(R.string.app_set_for_staff));
        tvActivityName.setVisibility(View.VISIBLE);

        tvFragmentName = v.findViewById(R.id.appBackdrop_top_title);
        tvFragmentName.setText(getActivity().getString(R.string.staff_feed_navigation_feed_corporate));
        tvFragmentName.setVisibility(View.VISIBLE);

        ibNavigator = v.findViewById(R.id.appBar_top_action_nav_menu);
        ibNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityListener.callNavigationDrawer();
            }
        });

        mFeedController.setFragmentView(v);
    }

    private void showAnnouncementAtStart(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mFeedController.getBannerFeedAnnouncement();
            }
        }, 2000);
    }

    public void setFirestoreUser(FirestoreUser user) {
        this.mFirestoreUser = user;
        mFeedController.setFirestoreUser(user);
        mFeedController.buildUserProfile();

    }


}
