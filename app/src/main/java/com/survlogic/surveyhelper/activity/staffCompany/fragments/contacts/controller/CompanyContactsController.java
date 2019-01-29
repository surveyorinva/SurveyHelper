package com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts.controller;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts.adapter.CompanyContactsRecyclerAdapter;
import com.survlogic.surveyhelper.activity.staffCompany.model.Contact;
import com.survlogic.surveyhelper.database.Contacts.FirestoreDatabaseContacts;

import java.util.ArrayList;

public class CompanyContactsController implements FirestoreDatabaseContacts.fetchList {

    private static final String TAG = "CompanyContactsControll";

    public interface CompanyContactsControllerListener{
        void refreshContactsUI();
    }

    /**
     * FirestoreDatabaseContacts.fetchList
     */

    @Override
    public void fetchContactListSuccess(ArrayList<Contact> contacts) {
        this.mContactsList = contacts;
        initRecyclerView();
    }

    @Override
    public void fetchContactListFail(String error) {
        //Todo
    }

    private Context mContext;
    private Activity mActivity;

    private CompanyContactsControllerListener listenerToActivity;

    private RecyclerView recyclerView;
    private ArrayList<Contact> mContactsList;

    public CompanyContactsController(Context context, CompanyContactsControllerListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.listenerToActivity = listener;

    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        initContacts();
    }

    private void initContacts(){

        FirestoreDatabaseContacts db = new FirestoreDatabaseContacts(mContext);
        db.setInterfaceFetchList(this);

        db.fetchContactListAll();

    }

    private void initRecyclerView(){
        CompanyContactsRecyclerAdapter staggeredRecyclerViewAdapter =
                new CompanyContactsRecyclerAdapter(mContext, mContactsList);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
    }


}
