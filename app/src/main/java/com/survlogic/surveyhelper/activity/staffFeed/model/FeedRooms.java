package com.survlogic.surveyhelper.activity.staffFeed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;

@IgnoreExtraProperties
public class FeedRooms implements Parcelable {

    private @ServerTimestamp Timestamp created_on;
    private boolean room_available;
    private String room_id;
    private String room_key;
    private String room_name;
    private boolean room_private;
    private boolean room_public_hall;
    private HashMap<String,Boolean> feed_actions_common;

    public FeedRooms() {
    }

    public void setFeedRooms(FeedRooms room){
        this.room_id = room.getRoom_id();
        this.room_available = room.room_available;
        this.room_key = room.room_key;
        this.room_name = room.room_name;
        this.room_private = room.room_private;
        this.room_public_hall = room.room_public_hall;
        this.feed_actions_common = room.feed_actions_common;
        this.created_on = room.created_on;
    }

    public Timestamp getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Timestamp created_on) {
        this.created_on = created_on;
    }

    public boolean isRoom_available() {
        return room_available;
    }

    public void setRoom_available(boolean room_available) {
        this.room_available = room_available;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_key() {
        return room_key;
    }

    public void setRoom_key(String room_key) {
        this.room_key = room_key;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public boolean isRoom_private() {
        return room_private;
    }

    public void setRoom_private(boolean room_private) {
        this.room_private = room_private;
    }

    public boolean isRoom_public_hall() {
        return room_public_hall;
    }

    public void setRoom_public_hall(boolean room_public_hall) {
        this.room_public_hall = room_public_hall;
    }

    public HashMap<String, Boolean> getFeed_actions_common() {
        return feed_actions_common;
    }

    public void setFeed_actions_common(HashMap<String, Boolean> feed_actions_common) {
        this.feed_actions_common = feed_actions_common;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.room_id);
        dest.writeString(this.room_name);
        dest.writeByte(this.room_available ? (byte) 1 : (byte) 0);
        dest.writeString(this.room_key);
        dest.writeByte(this.room_private ? (byte) 1 : (byte) 0);
        dest.writeByte(this.room_public_hall ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.feed_actions_common);
        dest.writeParcelable(this.created_on, flags);
    }

    protected FeedRooms(Parcel in) {
        this.room_id = in.readString();
        this.room_name = in.readString();
        this.room_available = in.readByte() != 0;
        this.room_key = in.readString();
        this.room_private = in.readByte() != 0;
        this.room_public_hall = in.readByte() != 0;
        this.feed_actions_common = (HashMap<String, Boolean>) in.readSerializable();
        this.created_on = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Parcelable.Creator<FeedRooms> CREATOR = new Parcelable.Creator<FeedRooms>() {
        @Override
        public FeedRooms createFromParcel(Parcel source) {
            return new FeedRooms(source);
        }

        @Override
        public FeedRooms[] newArray(int size) {
            return new FeedRooms[size];
        }
    };
}
