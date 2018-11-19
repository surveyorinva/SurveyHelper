package com.survlogic.surveyhelper.activity.staffFeed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class FeedEvent implements Parcelable {

    private String background_url;
    private String event_header;
    private String event_body;
    private long date_event;
    private String details_url;
    private long date_expire;
    private long date_expire_day_of_year;
    private ArrayList<String> whos_going;

    public FeedEvent() {
    }


    public FeedEvent(FeedEvent event){
        this.event_header = event.event_header;
        this.event_body = event.event_body;

        this.date_event = event.date_event;

        this.details_url = event.details_url;
        this.background_url = event.background_url;

        this.date_expire = event.date_expire;
        this.date_expire_day_of_year = event.date_expire_day_of_year;

        this.whos_going = event.whos_going;

    }

    public String getBackground_url() {
        return background_url;
    }

    public void setBackground_url(String background_url) {
        this.background_url = background_url;
    }

    public String getEvent_header() {
        return event_header;
    }

    public void setEvent_header(String event_header) {
        this.event_header = event_header;
    }

    public String getEvent_body() {
        return event_body;
    }

    public void setEvent_body(String event_body) {
        this.event_body = event_body;
    }

    public long getEvent_start_date() {
        return date_event;
    }

    public void setEvent_start_date(long event_start_date) {
        this.date_event = event_start_date;
    }

    public String getDetails_url() {
        return details_url;
    }

    public void setDetails_url(String details_url) {
        this.details_url = details_url;
    }

    public long getExpire_date() {
        return date_expire;
    }

    public void setExpire_date(long expire_date) {
        this.date_expire = expire_date;
    }

    public long getDate_expire_day_of_year() {
        return date_expire_day_of_year;
    }

    public void setDate_expire_day_of_year(long date_expire_day_of_year) {
        this.date_expire_day_of_year = date_expire_day_of_year;
    }

    public ArrayList<String> getWhos_going() {
        return whos_going;
    }

    public void setWhos_going(ArrayList<String> whos_going) {
        this.whos_going = whos_going;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.event_header);
        dest.writeString(this.event_body);
        dest.writeLong(this.date_event);
        dest.writeString(this.background_url);
        dest.writeString(this.details_url);
        dest.writeLong(this.date_expire);
        dest.writeLong(this.date_expire_day_of_year);
        dest.writeStringList(this.whos_going);
    }

    protected FeedEvent(Parcel in) {
        this.event_header = in.readString();
        this.event_body = in.readString();
        this.date_event = in.readLong();
        this.background_url = in.readString();
        this.details_url = in.readString();
        this.date_expire = in.readLong();
        this.date_expire_day_of_year = in.readLong();
        this.whos_going = in.createStringArrayList();
    }

    public static final Parcelable.Creator<FeedEvent> CREATOR = new Parcelable.Creator<FeedEvent>() {
        @Override
        public FeedEvent createFromParcel(Parcel source) {
            return new FeedEvent(source);
        }

        @Override
        public FeedEvent[] newArray(int size) {
            return new FeedEvent[size];
        }
    };
}
