package com.survlogic.surveyhelper.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class FirestoreUser implements Parcelable {
    private @ServerTimestamp
    Timestamp timestamp;
    private String user_id;
    private String display_name;
    private String email;
    private long telephone_office;
    private long telephone_mobile;
    private String access_level;
    private String access_key_secure;
    private String profile_pic_url;
    private Date profile_birthday;

    private int feed_posts, rewards_total, rewards_current;

    public FirestoreUser() {
    }

    public void setFirestoreUser(FirestoreUser user){
        this.user_id = user.getUser_id();
        this.display_name = user.display_name;
        this.email = user.email;
        this.telephone_mobile = user.telephone_mobile;
        this.telephone_office = user.telephone_office;
        this.profile_birthday = user.profile_birthday;
        this.access_level = user.access_level;
        this.access_key_secure = user.access_key_secure;
        this.profile_pic_url = user.profile_pic_url;
        this.feed_posts = user.feed_posts;
        this.rewards_total = user.rewards_total;
        this.rewards_current = user.rewards_current;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTelephone_office() {
        return telephone_office;
    }

    public void setTelephone_office(long telephone_office) {
        this.telephone_office = telephone_office;
    }

    public long getTelephone_mobile() {
        return telephone_mobile;
    }

    public void setTelephone_mobile(long telephone_mobile) {
        this.telephone_mobile = telephone_mobile;
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

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public Date getProfile_birthday() {
        return profile_birthday;
    }

    public void setProfile_birthday(Date profile_birthday) {
        this.profile_birthday = profile_birthday;
    }

    public int getFeed_posts() {
        return feed_posts;
    }

    public void setFeed_posts(int feed_posts) {
        this.feed_posts = feed_posts;
    }

    public int getRewards_total() {
        return rewards_total;
    }

    public void setRewards_total(int rewards_total) {
        this.rewards_total = rewards_total;
    }

    public int getRewards_current() {
        return rewards_current;
    }

    public void setRewards_current(int rewards_current) {
        this.rewards_current = rewards_current;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.user_id);
        dest.writeString(this.display_name);
        dest.writeString(this.access_level);
        dest.writeString(this.access_key_secure);
        dest.writeString(this.profile_pic_url);
        dest.writeString(this.email);
        dest.writeLong(this.telephone_office);
        dest.writeLong(this.telephone_mobile);
        dest.writeLong(this.profile_birthday != null ? this.profile_birthday.getTime() : -1);
        dest.writeInt(this.feed_posts);
        dest.writeInt(this.rewards_current);
        dest.writeInt(this.rewards_total);
        dest.writeParcelable(this.timestamp, flags);
    }


    protected FirestoreUser(Parcel in) {
        this.user_id = in.readString();
        this.display_name = in.readString();
        this.access_level = in.readString();
        this.access_key_secure = in.readString();
        this.profile_pic_url = in.readString();
        this.email = in.readString();
        this.telephone_office = in.readLong();
        this.telephone_mobile = in.readLong();
        long tmpProfile_birthday = in.readLong();
        this.profile_birthday = tmpProfile_birthday == -1 ? null : new Date(tmpProfile_birthday);
        this.feed_posts = in.readInt();
        this.rewards_current = in.readInt();
        this.rewards_total = in.readInt();
        this.timestamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Parcelable.Creator<FirestoreUser> CREATOR = new Parcelable.Creator<FirestoreUser>() {
        @Override
        public FirestoreUser createFromParcel(Parcel source) {
            return new FirestoreUser(source);
        }

        @Override
        public FirestoreUser[] newArray(int size) {
            return new FirestoreUser[size];
        }
    };
}
