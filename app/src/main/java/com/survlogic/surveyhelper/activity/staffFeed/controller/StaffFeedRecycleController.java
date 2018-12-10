package com.survlogic.surveyhelper.activity.staffFeed.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;

import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;

import java.util.ArrayList;

public class StaffFeedRecycleController {

    public interface FeedRecycleListener{
        void showFab(boolean isShow);

        boolean isFabShown();

    }

    private static final String TAG = "StaffFeedRecycleControl";

    private Context mContext;
    private Activity mActivity;

    private FeedRecycleListener listener;
    private ArrayList<Feed> mFeedCompiledList = new ArrayList<>();
    private StaffFeedAdapter.AdapterListener adapterActionListener;

    private boolean isAdapterSet = false;
    private RecyclerView mRecyclerView;
    private StaffFeedAdapter feedAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager mGridLayoutManager;

    public StaffFeedRecycleController(Context context, StaffFeedAdapter.AdapterListener controllerListener, FeedRecycleListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.adapterActionListener = controllerListener;

        this.listener = listener;
    }

    public void setFeedCompiledList(ArrayList<Feed> list) {
        this.mFeedCompiledList = list;
    }

    public void setRecyclerView(RecyclerView recyclerView){
        this.mRecyclerView = recyclerView;

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && listener.isFabShown())
                    listener.showFab(false);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    listener.showFab(true);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    public void updateFeedRecycler(){
        if(isAdapterSet){
            feedAdapter.swapItems(mFeedCompiledList);

        }else{
            initFeedAdapter();

        }
    }

    public void updateFeedItemWithImageUri(int returnDataTo, int returnToPosition, Uri uri){
        feedAdapter.setImageUriForItem(returnDataTo,returnToPosition, uri);
    }

    public void updateFeedItemWithImagBitmap(int returnDataTo, int returnToPosition, Bitmap bitmap){
        feedAdapter.setImageBitmapForItem(returnDataTo, returnToPosition, bitmap);
    }

    private void initFeedAdapter(){
        feedAdapter = new StaffFeedAdapter(mContext,adapterActionListener,mFeedCompiledList);

        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density  = mActivity.getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth/100);

        mGridLayoutManager = new GridLayoutManager(mContext,columns);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(feedAdapter);

        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(feedAdapter.isFullCard(position)){
                    return mGridLayoutManager.getSpanCount();
                }else{
                    return 1;
                }
            }
        });

        isAdapterSet = true;
    }


}
