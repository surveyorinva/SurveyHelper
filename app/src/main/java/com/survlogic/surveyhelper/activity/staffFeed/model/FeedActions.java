package com.survlogic.surveyhelper.activity.staffFeed.model;

import com.survlogic.surveyhelper.activity.staffFeed.workers.BottomSheetCompiler;

public class FeedActions {

    private int feed_action_type;
    private BottomSheetCompiler.RoomActions roomActions;
    private FeedReflections feedReflections;

    public FeedActions() {}

    public int getFeed_action_type() {
        return feed_action_type;
    }

    public void setFeed_action_type(int feed_action_type) {
        this.feed_action_type = feed_action_type;
    }

    public BottomSheetCompiler.RoomActions getRoomActions() {
        return roomActions;
    }

    public void setRoomActions(BottomSheetCompiler.RoomActions roomActions) {
        this.roomActions = roomActions;
    }

    public FeedReflections getFeedReflections() {
        return feedReflections;
    }

    public void setFeedReflections(FeedReflections feedReflections) {
        this.feedReflections = feedReflections;
    }
}
