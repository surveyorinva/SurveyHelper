package com.survlogic.surveyhelper.activity.staffFeed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class FeedEvent implements Parcelable {

    private String event_id;
    private String background_url;
    private String event_header;
    private String event_body;
    private String event_location;
    private String event_address;
    private long date_event_start;
    private long date_event_end;
    private String details_url;
    private long date_expire;
    private long date_expire_day_of_year;
    private ArrayList<String> whos_going;
    private ArrayList<String> whos_not_going;
    private ArrayList<String> whos_checked_in;
    private ArrayList<String> pictures_url;

    public FeedEvent() {
    }


    public FeedEvent(FeedEvent event){
        this.event_id = event.event_id;

        this.event_header = event.event_header;
        this.event_body = event.event_body;

        this.event_location = event.event_location;
        this.event_address = event.event_address;

        this.date_event_start = event.date_event_start;
        this.date_event_end = event.date_event_end;

        this.details_url = event.details_url;
        this.background_url = event.background_url;

        this.date_expire = event.date_expire;
        this.date_expire_day_of_year = event.date_expire_day_of_year;

        this.whos_going = event.whos_going;
        this.whos_not_going = event.whos_not_going;
        this.whos_checked_in = event.whos_checked_in;

        this.pictures_url = event.pictures_url;
    }


    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
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

    public long getDate_event_start() {
        return date_event_start;
    }

    public void setEvent_start_date(long event_start_date) {
        this.date_event_start = event_start_date;
    }

    public long getDate_event_end() {
        return date_event_end;
    }

    public void setDate_event_end(long date_event_end) {
        this.date_event_end = date_event_end;
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

    public ArrayList<String> getWhos_not_going() {
        return whos_not_going;
    }

    public void setWhos_not_going(ArrayList<String> whos_not_going) {
        this.whos_not_going = whos_not_going;
    }

    public ArrayList<String> getWhos_checked_in() {
        return whos_checked_in;
    }

    public void setWhos_checked_in(ArrayList<String> whos_checked_in) {
        this.whos_checked_in = whos_checked_in;
    }

    public ArrayList<String> getPictures_url() {
        return pictures_url;
    }

    public void setPictures_url(ArrayList<String> pictures_url) {
        this.pictures_url = pictures_url;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getEvent_address() {
        return event_address;
    }

    public void setEvent_address(String event_address) {
        this.event_address = event_address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.event_id);
        dest.writeString(this.background_url);
        dest.writeString(this.event_header);
        dest.writeString(this.event_body);
        dest.writeString(this.event_location);
        dest.writeString(this.event_address);
        dest.writeLong(this.date_event_start);
        dest.writeLong(this.date_event_end);
        dest.writeString(this.details_url);
        dest.writeLong(this.date_expire);
        dest.writeLong(this.date_expire_day_of_year);
        dest.writeStringList(this.whos_going);
        dest.writeStringList(this.whos_not_going);
        dest.writeStringList(this.whos_checked_in);
        dest.writeStringList(this.pictures_url);
    }

    protected FeedEvent(Parcel in) {
        this.event_id = in.readString();
        this.background_url = in.readString();
        this.event_header = in.readString();
        this.event_body = in.readString();
        this.event_location = in.readString();
        this.event_address = in.readString();
        this.date_event_start = in.readLong();
        this.date_event_end = in.readLong();
        this.details_url = in.readString();
        this.date_expire = in.readLong();
        this.date_expire_day_of_year = in.readLong();
        this.whos_going = in.createStringArrayList();
        this.whos_not_going = in.createStringArrayList();
        this.whos_checked_in = in.createStringArrayList();
        this.pictures_url = in.createStringArrayList();
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
