package com.survlogic.surveyhelper.activity.staffFeed;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.controller.StaffFeedController;
import com.survlogic.surveyhelper.activity.staff.inter.StaffActivityListener;
import com.survlogic.surveyhelper.inter.NavigationIconClickListener;

public class StaffFeedFragment extends Fragment implements StaffFeedController.StaffFeedControllerListener {
    private static final String TAG = "StaffFeedFragment";

    /**
     * StaffFeedControllerListener
     */

    @Override
    public void refreshFragmentUI() {

    }

    @Override
    public void sendFeedCategory(String feedCategory) {
        tvFragmentName.setText(feedCategory);
    }

    private Context mContext;

    private View v;

    private ImageButton ibBackdrop, ibNavigator, ibFeedNavigator;
    private TextView tvFragmentName;

    private StaffActivityListener mActivityListener;
    private StaffFeedController mFeedController;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();

        mActivityListener = (StaffActivityListener) getActivity();
        mFeedController = new StaffFeedController(mContext,this);

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

        showAnnouncementAtStart();

        mFeedController.fetchAllFeeds();

    }

    private void initView(){
        TextView tvActivityName = v.findViewById(R.id.appBar_top_title);
        tvActivityName.setText(getActivity().getString(R.string.app_set_for_staff));
        tvActivityName.setVisibility(View.VISIBLE);

        tvFragmentName = v.findViewById(R.id.appBackdrop_top_title);
        tvFragmentName.setText(getActivity().getString(R.string.staff_feed_navigation_feed_corporate));
        tvFragmentName.setVisibility(View.VISIBLE);

        ibBackdrop = v.findViewById(R.id.appBar_top_action_nav_backdrop);
        ibBackdrop.setOnClickListener(new NavigationIconClickListener(
                getContext(),
                v.findViewById(R.id.feed_front_view),
                new AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_filter_light_24dp), // Menu open icon
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_close_light_24dp) // Menu close icon

        ));

        ibBackdrop.setVisibility(View.VISIBLE);

        ibNavigator = v.findViewById(R.id.appBar_top_action_nav_menu);
        ibNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityListener.callNavigationDrawer();
            }
        });

        ibFeedNavigator = v.findViewById(R.id.btnAppFeedNavigator);
        ibFeedNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedController.createPopUpFeedNavigator(ibFeedNavigator);
            }
        });

    }


    private void showAnnouncementAtStart(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mFeedController.getPopUpFeedAnnouncement();
            }
        }, 2000);
    }

}
