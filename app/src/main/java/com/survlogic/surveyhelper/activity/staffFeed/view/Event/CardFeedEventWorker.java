package com.survlogic.surveyhelper.activity.staffFeed.view.Event;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.utils.DialogUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CardFeedEventWorker {

    private static final String TAG = "CardFeedEventWorker";

    public interface CardFeedEventWorkerListener{
        void returnSuccessful();
        void returnInError(boolean isError);
    }


    private Context mContext;
    private FeedEvent mEvent;
    private String mUserID;

    private CardFeedEventWorkerListener workerListener;

    private static final int LIST_GOING =  0,  LIST_NOT_GOING = 1, LIST_CHECKED_IN = 2;
    private static final int TIME_MINUTES = 1,  TIME_HOURS = 2, TIME_DAYS = 3,  TIME_WEEKS = 4;

    public CardFeedEventWorker(Context mContext, CardFeedEventWorkerListener workerListener) {
        this.mContext = mContext;
        this.workerListener = workerListener;
    }

    public FeedEvent getEvent() {
        return mEvent;
    }

    public void setEvent(FeedEvent event) {
        this.mEvent = event;
        this.mUserID = setUserID();

        int result = determineEventPhase();
        Log.d(TAG, "to_delete: Result: " + result);
    }

    private String setUserID(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

    //----------------------------------------------------------------------------------------------//

    public int determineEventPhase(){
        int result;

        Date todayDate = Calendar.getInstance().getTime();
        long todayMilliseconds = todayDate.getTime();

        //determine if event has expired
        boolean isEventExpired = isEventExpired();
        Log.d(TAG, "to_delete: Event Expired: " + isEventExpired);

        if(isEventExpired){
            DialogUtils.showToast(mContext, "EVENT_PHASE_OVER");
            result =  CardFeedEvent.EVENT_PHASE_OVER;
            return result;
        }

        // determine if user is in the event to go list  or the no-go list
        boolean isUserGoing = isUserInList(LIST_GOING);
        boolean isUserNotGoing = isUserInList(LIST_NOT_GOING);
        Log.d(TAG, "to_delete: Going/Not Going: " + isUserGoing + ", "  + isUserNotGoing);

        if(isUserGoing){
            long howLongUntilEventDays = convertMillisecondsToReadableTime(TIME_DAYS, numberOfMillisecondsBetweenNowAndEvent());

            if(howLongUntilEventDays > 1){
                DialogUtils.showToast(mContext, "EVENT_PHASE_COUNTDOWN_DAYS");
                result = CardFeedEvent.EVENT_PHASE_COUNTDOWN_DAYS;
                return result;
            }

            long howLongUntilEventStartHours = convertMillisecondsToReadableTime(TIME_HOURS, numberOfMillisecondsBetweenNowAndEvent());
            long howLongUntilEventStartsMinutes = convertMillisecondsToReadableTime(TIME_MINUTES, numberOfMillisecondsBetweenNowAndEvent());

            Log.d(TAG, "to_delete: Hours: " + howLongUntilEventStartHours);
            Log.d(TAG, "to_delete: Minutes: " + howLongUntilEventStartsMinutes);

            if(howLongUntilEventStartHours > 1 && howLongUntilEventStartHours <= 24){
                DialogUtils.showToast(mContext, "EVENT_PHASE_DAY_OF_EVENT");
                result = CardFeedEvent.EVENT_PHASE_COUNTDOWN_DAYS;
                return result;
            }


            if(howLongUntilEventStartsMinutes >= 0 && howLongUntilEventStartsMinutes <= 60){
                DialogUtils.showToast(mContext, "EVENT_PHASE_DAY_OF_EVENT");
                result = CardFeedEvent.EVENT_PHASE_DAY_OF_EVENT;
                return result;
            }

            long howLongUntilEventEndsMinutes = convertMillisecondsToReadableTime(TIME_MINUTES, numberOfMillisecondsBetweenNowAndEventEnd());

            if(howLongUntilEventStartsMinutes < 0 && howLongUntilEventEndsMinutes > 0){

                if(isUserInList(LIST_CHECKED_IN)){
                    DialogUtils.showToast(mContext, "EVENT_PHASE_IN_EVENT_CHECKED_IN");
                    result = CardFeedEvent.EVENT_PHASE_IN_EVENT;
                    return result;
                }else{
                    DialogUtils.showToast(mContext, "EVENT_PHASE_IN_EVENT_NOT_CHECK_IN");
                    result = CardFeedEvent.EVENT_PHASE_DAY_OF_EVENT;
                    return result;
                }

            }

            if(howLongUntilEventEndsMinutes <0){
                DialogUtils.showToast(mContext, "EVENT_PHASE_REMINISCE");
                result = CardFeedEvent.EVENT_PHASE_REMINISCE;
                return result;
            }


        }else if(isUserNotGoing){
            DialogUtils.showToast(mContext, "EVENT_PHASE_HIDE");
            result = CardFeedEvent.EVENT_PHASE_HIDE;
            return result;
        }else{
            DialogUtils.showToast(mContext, "EVENT_PHASE_INTRO");
            result = CardFeedEvent.EVENT_PHASE_INTRO;
            return result;
        }

       return 0;

    }


    //----------------------------------------------------------------------------------------------//

    private boolean isEventExpired(){
        Date today = Calendar.getInstance().getTime();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);

        long todaysDayOfYear = cal1.get(Calendar.DAY_OF_YEAR);
        long eventExpireDay = mEvent.getDate_expire_day_of_year();

        long diff = eventExpireDay - todaysDayOfYear;

        return diff <= 0;

    }

    private long numberOfMillisecondsBetweenNowAndEvent(){
        Date todayDate = Calendar.getInstance().getTime();
        long todayMilliseconds = todayDate.getTime();

        long difference = mEvent.getDate_event_start() - todayMilliseconds;

        return difference;

    }

    private long numberOfMillisecondsBetweenNowAndEventEnd(){
        Date todayDate = Calendar.getInstance().getTime();
        long todayMilliseconds = todayDate.getTime();

        long difference = mEvent.getDate_event_end() - todayMilliseconds;

        return difference;
    }

    private long convertMillisecondsToReadableTime(int timeUnit, long milliseconds){
        long SECOND_IN_MILLIS = 1000;
        long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
        long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
        long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
        long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;

        long results = -1;

        switch (timeUnit){
            case TIME_MINUTES:
                results = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
                break;

            case TIME_HOURS:
                results = TimeUnit.MILLISECONDS.toHours(milliseconds);
                break;

            case TIME_DAYS:
                results = TimeUnit.MILLISECONDS.toDays(milliseconds);
                break;

            case TIME_WEEKS:
                results = milliseconds/WEEK_IN_MILLIS;
                break;

        }

        return results;
    }

    private boolean isUserInList(int listToSearch){
        switch (listToSearch){

            case LIST_GOING:
                return mEvent.getWhos_going().contains(mUserID);

            case LIST_NOT_GOING:
                return mEvent.getWhos_not_going().contains(mUserID);

            case LIST_CHECKED_IN:
                return mEvent.getWhos_checked_in().contains(mUserID);

            default:
                return false;

        }


    }


}
