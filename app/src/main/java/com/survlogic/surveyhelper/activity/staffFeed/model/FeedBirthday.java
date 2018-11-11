package com.survlogic.surveyhelper.activity.staffFeed.model;

import java.util.Date;

public class FeedBirthday {

    private String user_id;
    private String userProfilePicUrl;
    private long birthDate;

    public FeedBirthday() {
    }

    public FeedBirthday(FeedBirthday birthday){
        this.user_id = birthday.getUser_id();
        this.userProfilePicUrl = birthday.userProfilePicUrl;
        this.birthDate = birthday.birthDate;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }
}
