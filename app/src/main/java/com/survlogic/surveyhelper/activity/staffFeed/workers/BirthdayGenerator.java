package com.survlogic.surveyhelper.activity.staffFeed.workers;

import android.app.Activity;
import android.content.Context;

import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;
import com.survlogic.surveyhelper.database.Feed.FirestoreDatabaseFeedAnnouncement;
import com.survlogic.surveyhelper.database.Feed.FirestoreDatabaseFeedBirthday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BirthdayGenerator implements FirestoreDatabaseFeedBirthday.FeedBirthdayListener{

    private static final String TAG = "BirthdayGenerator";

    public interface BirthdayGeneratorListener{
        void returnBirthdayList(ArrayList<FeedBirthday> birthdayList);
        void returnNoBirthdays(boolean isErrorState);
    }

    /**
     *FeedBirthdayListener
     */
    @Override
    public void fetchBirthdayAll(ArrayList<FeedBirthday> birthdayList) {
        this.mListBirthdays = birthdayList;
        callFilterList();
    }

    @Override
    public void fetchFeedBirthdayGetError(boolean isError) {
        mWorkerListener.returnNoBirthdays(isError);
    }

    private Context mContext;
    private Activity mActivity;
    private BirthdayGeneratorListener mWorkerListener;

    private ArrayList<FeedBirthday> mListBirthdays = new ArrayList<>();

    public BirthdayGenerator(Context context, BirthdayGeneratorListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mWorkerListener = listener;
    }

    public void onStart(){
        Date today = Calendar.getInstance().getTime();

        FirestoreDatabaseFeedBirthday db = new FirestoreDatabaseFeedBirthday(mContext,this);
        db.getFeedBirthdayFromFirestore(today);
    }

    /**
     * Potentially filter arraylist after returning from Firestore in the future
     */
    private void callFilterList(){
        if(mListBirthdays.size() >0){
            mWorkerListener.returnBirthdayList(mListBirthdays);
        }


    }


}
