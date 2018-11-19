package com.survlogic.surveyhelper.activity.staffFeed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;

@IgnoreExtraProperties
public class FeedItem implements Parcelable {

    private String user_id;
    private String display_name;
    private String user_profile_pic_url;
    private int feed_post_type;
    private String extra_entry;
    private String room_id;
    private @ServerTimestamp Timestamp postedOn;
    private long postedOn_day_of_year;
    private ArrayList<String> photo_link;

    public FeedItem() {
    }

    public FeedItem(FeedItem item){
        this.user_id = item.user_id;
        this.display_name = item.display_name;
        this.user_profile_pic_url = item.user_profile_pic_url;
        this.feed_post_type = item.feed_post_type;
        this.extra_entry = item.extra_entry;
        this.room_id = item.room_id;
        this.postedOn = item.postedOn;
        this.postedOn_day_of_year = item.postedOn_day_of_year;
        this.photo_link = item.photo_link;

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getUser_profile_pic_url() {
        return user_profile_pic_url;
    }

    public void setUser_profile_pic_url(String user_profile_pic_url) {
        this.user_profile_pic_url = user_profile_pic_url;
    }

    public int getFeed_post_type() {
        return feed_post_type;
    }

    public void setFeed_post_type(int feed_post_type) {
        this.feed_post_type = feed_post_type;
    }

    public String getExtra_entry() {
        return extra_entry;
    }

    public void setExtra_entry(String extra_entry) {
        this.extra_entry = extra_entry;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public Timestamp getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Timestamp postedOn) {
        this.postedOn = postedOn;
    }

    public long getPostedOn_day_of_year() {
        return postedOn_day_of_year;
    }

    public void setPostedOn_day_of_year(long postedOn_day_of_year) {
        this.postedOn_day_of_year = postedOn_day_of_year;
    }

    public ArrayList<String> getPhoto_link() {
        return photo_link;
    }

    public void setPhoto_link(ArrayList<String> photo_link) {
        this.photo_link = photo_link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.feed_post_type);
        dest.writeString(this.room_id);
        dest.writeString(this.user_id);
        dest.writeString(this.display_name);
        dest.writeString(this.user_profile_pic_url);
        dest.writeString(this.extra_entry);
        dest.writeParcelable(this.postedOn, flags);
        dest.writeLong(this.postedOn_day_of_year);
        dest.writeStringList(this.photo_link);
    }

    protected FeedItem(Parcel in) {
        this.feed_post_type = in.readInt();
        this.room_id = in.readString();
        this.user_id = in.readString();
        this.display_name = in.readString();
        this.user_profile_pic_url = in.readString();
        this.extra_entry = in.readString();
        this.postedOn = in.readParcelable(Timestamp.class.getClassLoader());
        this.postedOn_day_of_year = in.readLong();
        this.photo_link = in.createStringArrayList();
    }

    public static final Parcelable.Creator<FeedItem> CREATOR = new Parcelable.Creator<FeedItem>() {
        @Override
        public FeedItem createFromParcel(Parcel source) {
            return new FeedItem(source);
        }

        @Override
        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };
}
