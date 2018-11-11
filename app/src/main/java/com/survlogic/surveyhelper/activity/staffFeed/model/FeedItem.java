package com.survlogic.surveyhelper.activity.staffFeed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

@IgnoreExtraProperties
public class FeedItem implements Parcelable {

    private String user_id;
    private String display_name;
    private String userProfilePicUrl;
    private int feed_post_type;
    private String extra_entry;
    private @ServerTimestamp Timestamp postedOn;

    public FeedItem() {
    }

    public FeedItem(FeedItem item){
        this.user_id = item.user_id;
        this.display_name = item.display_name;
        this.userProfilePicUrl = item.userProfilePicUrl;
        this.feed_post_type = item.feed_post_type;
        this.extra_entry = item.extra_entry;
        this.postedOn = item.postedOn;

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

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
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

    public Timestamp getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Timestamp postedOn) {
        this.postedOn = postedOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.feed_post_type);
        dest.writeString(this.user_id);
        dest.writeString(this.display_name);
        dest.writeString(this.userProfilePicUrl);
        dest.writeString(this.extra_entry);
        dest.writeParcelable(this.postedOn, flags);
    }

    protected FeedItem(Parcel in) {
        this.feed_post_type = in.readInt();
        this.user_id = in.readString();
        this.display_name = in.readString();
        this.userProfilePicUrl = in.readString();
        this.extra_entry = in.readString();
        this.postedOn = in.readParcelable(Timestamp.class.getClassLoader());
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
