package com.survlogic.surveyhelper.activity.staffFeed.workers;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedReflections;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class BottomSheetCompiler {

    private static final String TAG = "BottomSheetCompiler";

    public interface RoomActionsListener{
        void returnActions(ArrayList<FeedActions> feedActions);
        void returnActionsError(boolean isError);
    }

    public class RoomActions{
        private String roomActionName;
        private Drawable roomActionDrawable;
        private int action_id;
        private int roomActionColor;

        public RoomActions() {}

        public RoomActions(RoomActions actions){
            this.roomActionName = actions.getRoomActionName();
            this.action_id = actions.getAction_id();
            this.roomActionDrawable = actions.getRoomActionDrawable();
            this.roomActionColor = actions.getRoomActionColor();
        }

        public String getRoomActionName() {
            return roomActionName;
        }

        public void setRoomActionName(String roomActionName) {
            this.roomActionName = roomActionName;
        }

        public int getAction_id() {
            return action_id;
        }

        public void setAction_id(int action_id) {
            this.action_id = action_id;
        }

        public Drawable getRoomActionDrawable() {
            return roomActionDrawable;
        }

        public void setRoomActionDrawable(Drawable roomActionDrawable) {
            this.roomActionDrawable = roomActionDrawable;
        }

        public int getRoomActionColor() {
            return roomActionColor;
        }

        public void setRoomActionColor(int roomActionColor) {
            this.roomActionColor = roomActionColor;
        }
    }

    private Context mContext;
    private Activity mActivity;
    private RoomActionsListener mWorkerListener;

    private HashMap<String,Boolean> mRoomActionsRaw;

    private PreferenceLoader preferenceLoader;


    /**
     * Compile Order
     * 1001.) Room Actions
     * 2001.) Reflections (Header)
     * 2002.) Reflections
     * 3001.) Surveys (Header) //Todo
     * 3002.) Surveys (Future) //Todo
     * 100.) Empty Space

     */



    public BottomSheetCompiler(Context context, RoomActionsListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;
        this.mWorkerListener = listener;

        this.preferenceLoader = new PreferenceLoader(mContext);
    }

    public void setRoomActionsRaw(HashMap<String, Boolean> actionsRaw) {
        this.mRoomActionsRaw = actionsRaw;
    }

    public void build() {
        ArrayList<FeedActions> actionsToReturn = new ArrayList<>();

        ArrayList<RoomActions> roomActions = generateActionsByRoom();


        if(roomActions.size() > 0){
            for(int i = 0; i < roomActions.size(); i++){
                FeedActions feedAction = new FeedActions();
                feedAction.setFeed_action_type(1001);
                feedAction.setRoomActions(roomActions.get(i));
                actionsToReturn.add(feedAction);
            }
        }

        ArrayList<FeedReflections> roomReflections = generateReflectionsCurrent();

        if(roomReflections.size()>0){
            FeedActions feedReflectionHeader = new FeedActions();
            feedReflectionHeader.setFeed_action_type(2001);
            actionsToReturn.add(feedReflectionHeader);

            for(int i = 0; i < roomReflections.size(); i++){
                FeedActions feedReflection = new FeedActions();
                feedReflection.setFeed_action_type(2002);
                feedReflection.setFeedReflections(roomReflections.get(i));
                actionsToReturn.add(feedReflection);
            }
        }


        if(actionsToReturn.size() >0){
            FeedActions feedEmpty = new FeedActions();
            feedEmpty.setFeed_action_type(1);
            actionsToReturn.add(feedEmpty);

            mWorkerListener.returnActions(actionsToReturn);
        }else{
            mWorkerListener.returnActionsError(true);
        }

    }

    private ArrayList<RoomActions> generateActionsByRoom(){
        ArrayList<RoomActions> roomActionsReturn = new ArrayList<>();

        for (String key : mRoomActionsRaw.keySet()){
            boolean isAvailable = mRoomActionsRaw.get(key);
            if(isAvailable){
                RoomActions action = new RoomActions();

                switch (key){
                    case "post_announcement":
                        action.setRoomActionName(mActivity.getResources().getString(R.string.staff_feed_bottom_sheet_action_type_announcement));
                        action.setAction_id(mActivity.getResources().getInteger(R.integer.FEED_ACTIONS_ANNOUNCEMENT));
                        action.setRoomActionDrawable(ContextCompat.getDrawable(mActivity,R.drawable.ic_search_dark_24dp));
                        action.setRoomActionColor(ContextCompat.getColor(mActivity,R.color.feed_actions_item_color_announcement));
                        roomActionsReturn.add(action);
                        break;

                    case "post_event":
                        action.setRoomActionName(mActivity.getResources().getString(R.string.staff_feed_bottom_sheet_action_type_event));
                        action.setAction_id(mActivity.getResources().getInteger(R.integer.FEED_ACTIONS_EVENT));
                        action.setRoomActionDrawable(ContextCompat.getDrawable(mActivity,R.drawable.ic_search_dark_24dp));
                        action.setRoomActionColor(ContextCompat.getColor(mActivity,R.color.feed_actions_item_color_event));
                        roomActionsReturn.add(action);
                        break;

                    case "post_message":
                        action.setRoomActionName(mActivity.getResources().getString(R.string.staff_feed_bottom_sheet_action_type_message));
                        action.setAction_id(mActivity.getResources().getInteger(R.integer.FEED_ACTIONS_ITEM_MESSAGE));
                        action.setRoomActionDrawable(ContextCompat.getDrawable(mActivity,R.drawable.ic_search_dark_24dp));
                        action.setRoomActionColor(ContextCompat.getColor(mActivity,R.color.feed_actions_item_color_item_message));
                        roomActionsReturn.add(action);

                        break;

                    case "post_update":
                        action.setRoomActionName(mActivity.getResources().getString(R.string.staff_feed_bottom_sheet_action_type_update));
                        action.setAction_id(mActivity.getResources().getInteger(R.integer.FEED_ACTIONS_ITEM_UPDATE));
                        action.setRoomActionDrawable(ContextCompat.getDrawable(mActivity,R.drawable.ic_search_dark_24dp));
                        action.setRoomActionColor(ContextCompat.getColor(mActivity,R.color.feed_actions_item_color_item_update));
                        roomActionsReturn.add(action);

                        break;
                }
            }
        }

        return roomActionsReturn;

    }

    private ArrayList<FeedReflections> generateReflectionsCurrent(){
        ArrayList<FeedReflections> reflectionsToReturn = new ArrayList<>();

        FeedReflections reflectionMorning = preferenceLoader.getFeedReflectionDailyMorningLocal();
        boolean isReflectionMorningComplete = isLocalReflectionCompletedToday(PreferenceLoader.REFLECTION_MORNING,reflectionMorning);

        if(!isReflectionMorningComplete){
            // Determine if in range of local time to show on switch board
            boolean isReflectionMorningNow = isTimeWithinRange(PreferenceLoader.REFLECTION_MORNING);

            if(isReflectionMorningNow){
                //Add reflection to array list
                reflectionsToReturn.add(reflectionMorning);
            }
        }


        FeedReflections reflectionEvening = preferenceLoader.getFeedReflectionDailyEveningLocal();
        boolean isReflectionEveningComplete = isLocalReflectionCompletedToday(PreferenceLoader.REFLECTION_EVENING,reflectionEvening);

        if(!isReflectionEveningComplete){
            // Determine if in range of local time to show on switch board
            boolean isReflectionEveningNow = isTimeWithinRange(PreferenceLoader.REFLECTION_EVENING);

            if(isReflectionEveningNow){
                //Add reflection to array list
                reflectionsToReturn.add(reflectionEvening);
            }
        }

        FeedReflections reflectionsWeekly = preferenceLoader.getFeedReflectionWeeklyLocal();
        boolean isReflectionWeeklyComplete = isLocalReflectionCompletedThisWeek(PreferenceLoader.REFLECTION_WEEKLY,reflectionsWeekly);

        if(!isReflectionWeeklyComplete){
            reflectionsToReturn.add(reflectionsWeekly);
        }

        return reflectionsToReturn;
    }


    private boolean isLocalReflectionCompletedToday(int category, FeedReflections reflection){
        Date dateNow  = new Date();

        if(reflection.isComplete()){
            Date dateUserCompleted = new Date(reflection.getCompletedOn());
            if(dateUserCompleted.compareTo(dateNow)!=0){
                reflection.setComplete(false);
                reflection.setAnswer(null);
                reflection.setCompletedOn(0);

                preferenceLoader.setFeedReflection(category,reflection,true);
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }

    private boolean isLocalReflectionCompletedThisWeek(int category, FeedReflections reflection){
        if(reflection.isComplete()){
            Date dateUserCompleted = new Date(reflection.getCompletedOn());

            if(!isDateInCurrentWeek(dateUserCompleted)){
                reflection.setComplete(false);
                reflection.setAnswer(null);
                reflection.setCompletedOn(0);

                preferenceLoader.setFeedReflection(category,reflection,true);
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }


    private boolean isDateInCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);

        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);

        return week == targetWeek && year == targetYear;
    }

    private boolean isTimeWithinRange(int category) {
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY); // Get hour in 24 hour format
        int minute = now.get(Calendar.MINUTE);

        Date date = parseDate(hour + ":" + minute);
        String startTime, endTime;

        switch (category) {
            case PreferenceLoader.REFLECTION_MORNING:
                startTime = preferenceLoader.getFeedReflectionDailyMorningStartTime();
                endTime = preferenceLoader.getFeedReflectionDailyMorningEndTime();
                break;

            case PreferenceLoader.REFLECTION_EVENING:
                startTime = preferenceLoader.getFeedReflectionDailyEveningStartTime();
                endTime = preferenceLoader.getFeedReflectionDailyEveningEndTime();
                break;

            default:
                startTime = "05:00";
                endTime = "23:59";
                break;

        }

        Date dateCompareOne = parseDate(startTime);
        Date dateCompareTwo = parseDate(endTime);

        return dateCompareOne.before(date) && dateCompareTwo.after(date);
    }

    private Date parseDate(String date) {
        final String inputFormat = "HH:mm";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }
}
