package com.survlogic.surveyhelper.activity.staffFeed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class FeedEvent implements Parcelable {

    private String backgroundUrl;
    private String eventHeader;
    private String eventBody;
    private Date eventStartDate, eventEndDate;
    private String detailsUrl;
    private boolean isGoing;
    private Date postedDate, expireDate;

    public FeedEvent() {
    }

    public FeedEvent(FeedEvent event){
        this.backgroundUrl = event.backgroundUrl;
        this.eventHeader = event.eventHeader;
        this.eventBody = event.eventBody;

        this.eventStartDate = event.eventStartDate;
        this.eventEndDate = event.eventEndDate;

        this.detailsUrl = event.detailsUrl;
        this.isGoing = event.isGoing;

        this.postedDate = event.postedDate;
        this.expireDate = event.expireDate;

    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public String getEventHeader() {
        return eventHeader;
    }

    public void setEventHeader(String eventHeader) {
        this.eventHeader = eventHeader;
    }

    public String getEventBody() {
        return eventBody;
    }

    public void setEventBody(String eventBody) {
        this.eventBody = eventBody;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    public boolean isGoing() {
        return isGoing;
    }

    public void setGoing(boolean going) {
        isGoing = going;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.eventHeader);
        dest.writeString(this.eventBody);
        dest.writeLong(this.eventStartDate != null ? this.eventStartDate.getTime() : -1);
        dest.writeLong(this.eventEndDate != null ? this.eventEndDate.getTime() : -1);
        dest.writeString(this.backgroundUrl);
        dest.writeString(this.detailsUrl);
        dest.writeByte(this.isGoing ? (byte) 1 : (byte) 0);
        dest.writeLong(this.postedDate != null ? this.postedDate.getTime() : -1);
        dest.writeLong(this.expireDate != null ? this.expireDate.getTime() : -1);
    }

    protected FeedEvent(Parcel in) {
        this.eventHeader = in.readString();
        this.eventBody = in.readString();
        long tmpEventStartDate = in.readLong();
        this.eventStartDate = tmpEventStartDate == -1 ? null : new Date(tmpEventStartDate);
        long tmpEventEndDate = in.readLong();
        this.eventEndDate = tmpEventEndDate == -1 ? null : new Date(tmpEventEndDate);
        this.backgroundUrl = in.readString();
        this.detailsUrl = in.readString();
        this.isGoing = in.readByte() != 0;
        long tmpPostedDate = in.readLong();
        this.postedDate = tmpPostedDate == -1 ? null : new Date(tmpPostedDate);
        long tmpExpireDate = in.readLong();
        this.expireDate = tmpExpireDate == -1 ? null : new Date(tmpExpireDate);
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
