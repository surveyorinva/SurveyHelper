package com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts.activity.CompanyContactNew;
import com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts.controller.CompanyContactsController;

public class CompanyContactsFragment extends Fragment implements CompanyContactsController.CompanyContactsControllerListener{

    private static final String TAG = "CompanyNewsFragment";

    @Override
    public void refreshContactsUI() {

    }

    /**
     * StaffCompanyController.StaffCompanyControllerListener
     */


    private static final String ARG_PARAM1 = "param1";
    private Context mContext;

    private String mParam1;
    private TextView tabText;

    private CompanyContactsController mController;
    private boolean isControllerSetup = false;

    private RecyclerView recyclerViewContacts;
    private FloatingActionButton fabAddNew;

    public CompanyContactsFragment() {
        // Required empty public constructor
    }

    public static CompanyContactsFragment newInstance(String param1) {
        CompanyContactsFragment fragment = new CompanyContactsFragment();
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

        View convertView = inflater.inflate(R.layout.staff_company_content_contacts, container, false);
        recyclerViewContacts = convertView.findViewById(R.id.recycler_in_company_contacts);
        fabAddNew = convertView.findViewById(R.id.add_new_contact);
        fabAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewContact();
            }
        });

        return convertView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!isControllerSetup){
            mController = new CompanyContactsController(mContext,this);
            mController.setRecyclerView(recyclerViewContacts);

        }

    }

    private void openNewContact(){
        Intent i = new Intent(getActivity(), CompanyContactNew.class);
        getActivity().startActivity(i);
        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }


}
