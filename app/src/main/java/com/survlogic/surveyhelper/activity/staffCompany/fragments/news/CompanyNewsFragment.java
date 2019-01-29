package com.survlogic.surveyhelper.activity.staffCompany.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffCompany.fragments.news.controller.CompanyNewsController;

public class CompanyNewsFragment extends Fragment implements CompanyNewsController.CompanyNewsControllerListener{

    private static final String TAG = "CompanyNewsFragment";

    /**
     * CompanyNewsController.CompanyNewsControllerListener
     */

    @Override
    public void refreshView() {

    }

    private static final String ARG_PARAM1 = "param1";

        private Context mContext;

        private String mParam1;
        private TextView tabText;

        private CompanyNewsController mController;
        private boolean isControllerSetup = false;

        private RecyclerView recyclerViewNavigator;

        public CompanyNewsFragment() {
            // Required empty public constructor
        }

        public static CompanyNewsFragment newInstance(String param1) {
            CompanyNewsFragment fragment = new CompanyNewsFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);

            }

            mContext = getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View convertView = inflater.inflate(R.layout.staff_company_content_news, container, false);

            recyclerViewNavigator = convertView.findViewById(R.id.recycler_in_company_news_feed);


            return convertView;
        }

    @Override
    public void onResume() {
        super.onResume();

        if(!isControllerSetup){
            mController = new CompanyNewsController(mContext,this);
            mController.setRecyclerView(recyclerViewNavigator);

        }

    }
}


