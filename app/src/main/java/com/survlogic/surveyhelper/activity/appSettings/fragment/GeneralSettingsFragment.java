package com.survlogic.surveyhelper.activity.appSettings.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appSettings.AboutAppActivity;
import com.survlogic.surveyhelper.activity.appSettings.LegalTermsActivity;

public class GeneralSettingsFragment extends Fragment {

    private static final String TAG = "GeneralSettingsFragment";
    private View v;

    private RelativeLayout rl_legal_terms, rl_legal_privacy, rl_design_about;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.settings_fragment_general, container, false);

        initView();
        setOnClickListeners();

        return v;

    }

    private void initView(){
        rl_legal_terms = v.findViewById(R.id.rl_about_Item_1);
        rl_legal_privacy = v.findViewById(R.id.rl_about_Item_2);

        rl_design_about = v.findViewById(R.id.rl_about_Item_4);
    }



    private void setOnClickListeners(){
        rl_legal_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LegalTermsActivity.class);
                i.putExtra(getResources().getString(R.string.KEY_PARENT_ACTIVITY),getString(R.string.CLASS_SETTINGS_LEGAL_TERMS));
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        rl_legal_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LegalTermsActivity.class);
                i.putExtra(getResources().getString(R.string.KEY_PARENT_ACTIVITY),getString(R.string.CLASS_SETTINGS_LEGAL_PRIVACY));
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        rl_design_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AboutAppActivity.class);
                i.putExtra(getResources().getString(R.string.KEY_PARENT_ACTIVITY),getString(R.string.CLASS_SETTINGS_ABOUT));
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

    }



}
