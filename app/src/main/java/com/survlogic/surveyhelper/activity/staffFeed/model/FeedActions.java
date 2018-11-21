package com.survlogic.surveyhelper.activity.staffFeed.model;

import com.survlogic.surveyhelper.activity.staffFeed.workers.GeneratorRoomActions;

import java.util.ArrayList;

public class FeedActions {

    private int feed_action_type;
    private GeneratorRoomActions.RoomActions roomActions;

    public FeedActions() {}

    public int getFeed_action_type() {
        return feed_action_type;
    }

    public void setFeed_action_type(int feed_action_type) {
        this.feed_action_type = feed_action_type;
    }

    public GeneratorRoomActions.RoomActions getRoomActions() {
        return roomActions;
    }

    public void setRoomActions(GeneratorRoomActions.RoomActions roomActions) {
        this.roomActions = roomActions;
    }
}
