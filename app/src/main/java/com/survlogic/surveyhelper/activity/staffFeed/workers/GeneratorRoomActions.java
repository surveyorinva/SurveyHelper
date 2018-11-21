package com.survlogic.surveyhelper.activity.staffFeed.workers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;

import java.util.ArrayList;
import java.util.HashMap;

public class GeneratorRoomActions {

    private static final String TAG = "GeneratorRoomActions";

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

    public GeneratorRoomActions(Context context, RoomActionsListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;
        this.mWorkerListener = listener;
    }

    public void setRoomActionsRaw(HashMap<String, Boolean> actionsRaw) {
        this.mRoomActionsRaw = actionsRaw;
    }

    public void build() {
        ArrayList<FeedActions> actionsToReturn = new ArrayList<>();

        ArrayList<RoomActions> roomActions = generateActionsByRoom();

        if(roomActions.size() > 0){
            for(int i =0;i<roomActions.size();i++){
                FeedActions feedAction = new FeedActions();
                feedAction.setFeed_action_type(1001);
                feedAction.setRoomActions(roomActions.get(i));
                actionsToReturn.add(feedAction);
            }

        }

        if(actionsToReturn.size() >0){
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
}
