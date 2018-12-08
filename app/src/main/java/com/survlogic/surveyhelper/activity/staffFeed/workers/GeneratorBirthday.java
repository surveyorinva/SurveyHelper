package com.survlogic.surveyhelper.activity.staffFeed.workers;

import android.app.Activity;
import android.content.Context;

import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;
import com.survlogic.surveyhelper.database.Feed.FirestoreDatabaseFeedBirthday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GeneratorBirthday implements FirestoreDatabaseFeedBirthday.FeedBirthdayListener{

    private static final String TAG = "GeneratorBirthday";

    public interface BirthdayGeneratorListener{
        void returnBirthdayList(ArrayList<FeedBirthday> birthdayListToday, ArrayList<FeedBirthday> birthdayListAhead );
        void returnBirthdayFilterOff();
        void returnNoBirthdays(boolean isErrorState);
    }

    /**
     *FeedBirthdayListener
     */

    @Override
    public void fetchBirthdayAll(ArrayList<FeedBirthday> birthdayListToday, ArrayList<FeedBirthday> birthdayListAhead) {
        this.mListBirthdaysToday = birthdayListToday;
        this.mListBirthdaysFuture = birthdayListAhead;
        callFilterList();
    }


    @Override
    public void fetchFeedBirthdayGetError(boolean isError) {
        mWorkerListener.returnNoBirthdays(isError);
    }

    private Context mContext;
    private Activity mActivity;
    private BirthdayGeneratorListener mWorkerListener;

    private ArrayList<FeedBirthday> mListBirthdaysToday = new ArrayList<>();
    private ArrayList<FeedBirthday> mListBirthdaysFuture = new ArrayList<>();
    private boolean isFilteredShow = true;

    public GeneratorBirthday(Context context, BirthdayGeneratorListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mWorkerListener = listener;
    }

    public void onStart(){

        if(isFilteredShow){
            Date today = Calendar.getInstance().getTime();

            FirestoreDatabaseFeedBirthday db = new FirestoreDatabaseFeedBirthday(mContext,this);
            db.getFeedBirthdayFromFirestore(today);
        }else{
            mWorkerListener.returnBirthdayFilterOff();
        }

    }

    public void setIsFiltered(boolean isShown){
        isFilteredShow = isShown;
    }


    /**
     * Potentially filter arraylist after returning from Firestore in the future
     */
    private void callFilterList(){
        mWorkerListener.returnBirthdayList(mListBirthdaysToday, mListBirthdaysFuture);


    }


}
