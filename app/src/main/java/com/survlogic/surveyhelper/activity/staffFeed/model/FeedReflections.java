package com.survlogic.surveyhelper.activity.staffFeed.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class FeedReflections implements Parcelable {

    private String summary;
    private String answer;
    private int type;
    private boolean complete;
    private long completedOn;

    public FeedReflections() {
    }

    public FeedReflections(FeedReflections reflection){
        this.summary = reflection.summary;
        this.answer = reflection.answer;
        this.type = reflection.type;
        this.complete = reflection.complete;
        this.completedOn = reflection.completedOn;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public long getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(long completedOn) {
        this.completedOn = completedOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.summary);
        dest.writeString(this.answer);
        dest.writeInt(this.type);
        dest.writeByte(this.complete ? (byte) 1 : (byte) 0);
        dest.writeLong(this.completedOn);
    }

    protected FeedReflections(Parcel in) {
        this.summary = in.readString();
        this.answer = in.readString();
        this.type = in.readInt();
        this.complete = in.readByte() != 0;
        this.completedOn = in.readLong();
    }

    public static final Parcelable.Creator<FeedReflections> CREATOR = new Parcelable.Creator<FeedReflections>() {
        @Override
        public FeedReflections createFromParcel(Parcel source) {
            return new FeedReflections(source);
        }

        @Override
        public FeedReflections[] newArray(int size) {
            return new FeedReflections[size];
        }
    };
}
