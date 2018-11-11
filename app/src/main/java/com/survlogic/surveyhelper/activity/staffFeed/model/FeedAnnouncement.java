package com.survlogic.surveyhelper.activity.staffFeed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class FeedAnnouncement implements Parcelable {

    private String announcement_id;
    private String background_url;
    private String news_header;
    private String news_body;
    private String details_url;
    private long date_posted, date_expire;


    public FeedAnnouncement() {
    }

    public FeedAnnouncement(FeedAnnouncement announcement){
        this.announcement_id = announcement.getAnnouncement_id();
        this.background_url = announcement.getBackground_url();
        this.news_header = announcement.getNews_header();
        this.news_body = announcement.news_body;
        this.details_url = announcement.details_url;
        this.date_posted = announcement.date_posted;
        this.date_expire = announcement.date_expire;

    }

    public String getAnnouncement_id() {
        return announcement_id;
    }

    public void setAnnouncement_id(String announcement_id) {
        this.announcement_id = announcement_id;
    }

    public String getBackground_url() {
        return background_url;
    }

    public void setBackground_url(String background_url) {
        this.background_url = background_url;
    }

    public String getNews_header() {
        return news_header;
    }

    public void setNews_header(String news_header) {
        this.news_header = news_header;
    }

    public String getNews_body() {
        return news_body;
    }

    public void setNews_body(String news_body) {
        this.news_body = news_body;
    }

    public String getDetails_url() {
        return details_url;
    }

    public void setDetails_url(String details_url) {
        this.details_url = details_url;
    }

    public long getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(long date_posted) {
        this.date_posted = date_posted;
    }

    public long getDate_expire() {
        return date_expire;
    }

    public void setDate_expire(long date_expire) {
        this.date_expire = date_expire;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.announcement_id);
        dest.writeString(this.news_header);
        dest.writeString(this.news_body);
        dest.writeString(this.background_url);
        dest.writeString(this.details_url);
        dest.writeLong(this.date_posted);
        dest.writeLong(this.date_expire);
    }

    protected FeedAnnouncement(Parcel in) {
        this.announcement_id = in.readString();
        this.news_header = in.readString();
        this.news_body = in.readString();
        this.background_url = in.readString();
        this.details_url = in.readString();
        this.date_posted = in.readLong();
        this.date_expire = in.readLong();
    }

    public static final Parcelable.Creator<FeedAnnouncement> CREATOR = new Parcelable.Creator<FeedAnnouncement>() {
        @Override
        public FeedAnnouncement createFromParcel(Parcel source) {
            return new FeedAnnouncement(source);
        }

        @Override
        public FeedAnnouncement[] newArray(int size) {
            return new FeedAnnouncement[size];
        }
    };
}
