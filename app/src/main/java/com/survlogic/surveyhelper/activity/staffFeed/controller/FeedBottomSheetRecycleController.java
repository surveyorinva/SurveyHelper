package com.survlogic.surveyhelper.activity.staffFeed.controller;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedBottomSheetAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;

import java.util.ArrayList;

public class FeedBottomSheetRecycleController {

    private static final String TAG = "FeedBottomSheetRecycleC";

    private Context mContext;
    private Activity mActivity;

    private ArrayList<FeedActions> mFeedActionsCompiledList = new ArrayList<>();

    private boolean isAdapterSet = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager mGridLayoutManager;

    private StaffFeedBottomSheetAdapter adapter;

    public FeedBottomSheetRecycleController(Context context){
        this.mContext = context;
        this.mActivity = (Activity) context;
    }

    public void setFeedCompiledList(ArrayList<FeedActions> list) {
        this.mFeedActionsCompiledList = list;
    }

    public ArrayList<FeedActions> getFeedActionsCompiledList() {
        return mFeedActionsCompiledList;
    }

    public void setRecyclerView(RecyclerView recyclerView){
        this.mRecyclerView = recyclerView;

    }

    public void updateRecycler(){
        if(isAdapterSet){
            adapter.swapItems(mFeedActionsCompiledList);
        }else{
            initAdapter();
        }
    }

    private void initAdapter(){
        Log.d(TAG, "to_delete: Calling Init Adapter ");
        adapter = new StaffFeedBottomSheetAdapter(mContext, mFeedActionsCompiledList);

        int columns = 2;

        mGridLayoutManager = new GridLayoutManager(mContext,columns);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(adapter);

        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter.isFullCard(position)){
                    return mGridLayoutManager.getSpanCount();
                }else{
                    return 1;
                }
            }
        });

        isAdapterSet = true;

    }

}
