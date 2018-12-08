package com.survlogic.surveyhelper.activity.staffFeed.workers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.database.Feed.FirestoreDatabaseFeedEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GeneratorEvent implements FirestoreDatabaseFeedEvent.FeedEventListener {

    private static final String TAG = "GeneratorEvent";

    public interface EventGeneratorListener{
        void returnEventList(ArrayList<FeedEvent> eventList);
        void returnEventFilterOff();
        void returnEventsError(boolean isErrorState);
    }

    /**
     *FeedEventListener
     */

    @Override
    public void fetchEventsAll(ArrayList<FeedEvent> eventList) {
        this.mListEvents = eventList;
        callFilterList();
    }

    @Override
    public void fetchFeedEventsGetError(boolean isError) {
        mWorkerListener.returnEventsError(isError);
    }

    private Context mContext;
    private Activity mActivity;
    private EventGeneratorListener mWorkerListener;

    private ArrayList<FeedEvent> mListEvents = new ArrayList<>();
    private boolean isFilteredShow = true;

    public GeneratorEvent(Context context, EventGeneratorListener listener){
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mWorkerListener = listener;
    }

    public void onStart(){
        if(isFilteredShow){
            Date today = Calendar.getInstance().getTime();

            FirestoreDatabaseFeedEvent db = new FirestoreDatabaseFeedEvent(mContext,this);
            db.getFeedEventListFromFirestore(today);
        }else{
            mWorkerListener.returnEventFilterOff();
        }
    }

    public void setIsFiltered(boolean isShown){
        isFilteredShow = isShown;
    }


    private void callFilterList(){
        mWorkerListener.returnEventList(mListEvents);
    }


}
