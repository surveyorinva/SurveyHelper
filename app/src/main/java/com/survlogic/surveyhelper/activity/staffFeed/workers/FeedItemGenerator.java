package com.survlogic.surveyhelper.activity.staffFeed.workers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.survlogic.surveyhelper.activity.staffFeed.model.FeedItem;
import com.survlogic.surveyhelper.database.Feed.FirestoreDatabaseFeedItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FeedItemGenerator implements FirestoreDatabaseFeedItem.FeedItemListener {
    private static final String TAG = "FeedItemGenerator";

    public interface FeedItemGeneratorListener{
        void returnFeedItemList(ArrayList<FeedItem> itemList);
        void returnFeedItemError(boolean isErrorState);
    }

    /**
     *FeedItemListener
     */

    @Override
    public void fetchFeedItemsAll(ArrayList<FeedItem> itemList) {
        this.mListItems = itemList;
        callFilterList();
    }

    @Override
    public void fetchFeedItemsGetError(boolean isError) {
        mWorkerListener.returnFeedItemError(isError);
    }

    private Context mContext;
    private Activity mActivity;
    private FeedItemGeneratorListener mWorkerListener;

    private ArrayList<FeedItem> mListItems = new ArrayList<>();

    public FeedItemGenerator(Context context, FeedItemGeneratorListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mWorkerListener = listener;
    }

    public void onStart(Date queryDate, String room_id){
        FirestoreDatabaseFeedItem db = new FirestoreDatabaseFeedItem(mContext,this);
        db.getFeedItemListFromFirestore(queryDate, room_id);

    }

    private void callFilterList(){
        mWorkerListener.returnFeedItemList(mListItems);

    }

}
