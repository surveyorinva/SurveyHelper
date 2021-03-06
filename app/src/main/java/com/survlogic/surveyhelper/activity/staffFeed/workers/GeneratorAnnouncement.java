package com.survlogic.surveyhelper.activity.staffFeed.workers;

import android.app.Activity;
import android.content.Context;

import com.survlogic.surveyhelper.activity.staffFeed.model.FeedAnnouncement;
import com.survlogic.surveyhelper.database.Feed.FirestoreDatabaseFeedAnnouncement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GeneratorAnnouncement implements FirestoreDatabaseFeedAnnouncement.FeedAnnouncmentListener {

    private static final String TAG = "GeneratorAnnouncement";

    public interface AnnouncementGeneratorListener{
        void returnAnnouncementList(ArrayList<FeedAnnouncement> announcementList);
        void returnAnnouncementFilterOff();
        void returnAnnouncementsError(boolean isErrorState);
    }


    @Override
    public void fetchAnnouncementAll(ArrayList<FeedAnnouncement> announcementList) {
        this.mListAnnouncements = announcementList;
        callFilterList();
    }

    @Override
    public void fetchFeedAnnouncementGetError(boolean isError) {
        mWorkerListener.returnAnnouncementsError(isError);
    }

    private Context mContext;
    private Activity mActivity;
    private AnnouncementGeneratorListener mWorkerListener;

    private ArrayList<FeedAnnouncement> mListAnnouncements = new ArrayList<>();
    private boolean isFilteredShow = true;

    public GeneratorAnnouncement(Context context, AnnouncementGeneratorListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mWorkerListener = listener;
    }


    public void onStart(){
        if(isFilteredShow){
            Date today = Calendar.getInstance().getTime();

            FirestoreDatabaseFeedAnnouncement db = new FirestoreDatabaseFeedAnnouncement(mContext,this);
            db.getFeedAnnouncementListFromFirestore(today);
        }else{
            mWorkerListener.returnAnnouncementFilterOff();
        }


    }

    public void setIsFiltered(boolean isShown){
        isFilteredShow = isShown;
    }

    /**
     * Potentially filter arraylist after returning from Firestore in the future
     */
    private void callFilterList(){
        mWorkerListener.returnAnnouncementList(mListAnnouncements);


    }



}
