package com.survlogic.surveyhelper.activity.staffProject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staff.inter.StaffActivityListener;
import com.survlogic.surveyhelper.activity.staffProject.controller.StaffProjectController;
import com.survlogic.surveyhelper.inter.NavigationIconClickListener;

public class StaffProjectFragment extends Fragment implements StaffProjectController.StaffProjectControllerListener {
    private static final String TAG = "StaffProjectFragment";

    /**
     * StaffProjectControllerListener
     */

    @Override
    public void refreshProjectFragmentUI() {

    }


    private Context mContext;

    private View v;


    private Button btShowList, btShowMap;
    private static final int SHOW_LIST = 0, SHOW_MAP = 1;
    private int activeView = 0;
    private ImageButton ibBackdrop, ibNavigator, ibFeedNavigator;
    private TextView tvFragmentName;

    private StaffActivityListener mActivityListener;
    private StaffProjectController mProjectController;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();

        mActivityListener = (StaffActivityListener) getActivity();
        mProjectController = new StaffProjectController(mContext,this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.staff_project_fragment, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

    }

    private void initView(){
        tvFragmentName = v.findViewById(R.id.appBackdrop_top_title);
        tvFragmentName.setText(getActivity().getString(R.string.staff_project_fragment_header_show_all_projects));
        tvFragmentName.setVisibility(View.VISIBLE);

        ibBackdrop = v.findViewById(R.id.appBar_top_action_nav_backdrop);
        ibBackdrop.setOnClickListener(new NavigationIconClickListener(
                getContext(),
                v.findViewById(R.id.project_front_view),
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
                mProjectController.createPopUpFeedNavigator(ibFeedNavigator);
            }
        });


        btShowList = v.findViewById(R.id.appBar_top_action_nav_display_list);
        btShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchShownView();
            }
        });
        btShowMap = v.findViewById(R.id.appBar_top_action_nav_display_map);
        btShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchShownView();
            }
        });

    }

    private void switchShownView(){
        Log.d(TAG, "to_delete: Started");
        if (activeView == SHOW_LIST){
            Log.d(TAG, "to_delete: " + SHOW_LIST);
            activeView = SHOW_MAP;

            btShowList.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorTransparent));
            btShowMap.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.background_rectangle_hollow_light));

        }else {
            Log.d(TAG, "to_delete: " + SHOW_MAP);
            activeView = SHOW_LIST;

            btShowMap.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorTransparent));
            btShowList.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.background_rectangle_hollow_light));
        }
    }


}
