package com.survlogic.surveyhelper.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class FirestoreAppAccessKeys implements Parcelable {
    private @ServerTimestamp Date timestamp;
    private String access_key_id;
    private String access_key_secure;
    private String access_level;


    public FirestoreAppAccessKeys() {
    }

    public FirestoreAppAccessKeys(FirestoreAppAccessKeys appKey){
        this.access_key_id = appKey.getAccess_key_id();
        this.access_key_secure = appKey.getAccess_key_secure();
        this.access_level = appKey.getAccess_level();
    }

    public String getAccess_key_id() {
        return access_key_id;
    }

    public void setAccess_key_id(String access_key_id) {
        this.access_key_id = access_key_id;
    }

    public String getAccess_level() {
        return access_level;
    }

    public void setAccess_level(String access_level) {
        this.access_level = access_level;
    }

    public String getAccess_key_secure() {
        return access_key_secure;
    }

    public void setAccess_key_secure(String access_key_secure) {
        this.access_key_secure = access_key_secure;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
        dest.writeString(this.access_key_id);
        dest.writeString(this.access_key_secure);
        dest.writeString(this.access_level);

    }


    protected FirestoreAppAccessKeys(Parcel in) {
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
        this.access_key_id = in.readString();
        this.access_key_secure = in.readString();
        this.access_level = in.readString();
    }

    public static final Creator<FirestoreAppAccessKeys> CREATOR = new Creator<FirestoreAppAccessKeys>() {
        @Override
        public FirestoreAppAccessKeys createFromParcel(Parcel source) {
            return new FirestoreAppAccessKeys(source);
        }

        @Override
        public FirestoreAppAccessKeys[] newArray(int size) {
            return new FirestoreAppAccessKeys[size];
        }
    };
}
