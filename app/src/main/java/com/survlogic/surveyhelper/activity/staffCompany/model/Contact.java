package com.survlogic.surveyhelper.activity.staffCompany.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Contact implements Parcelable {

    private String contact_id;
    private String user_id;
    private String first_name, last_name;
    private HashMap<String,Long> telephone;
    private String email;
    private String profile_pic_url;
    private String work_location;
    private String title;
    private long birthday;
    private HashMap<String,String> supervisor;

    public Contact() {
    }

    public Contact(Contact contact){
        this.contact_id = contact.contact_id;
        this.user_id = contact.user_id;
        this.first_name = contact.first_name;
        this.last_name = contact.last_name;
        this.telephone = contact.telephone;
        this.email = contact.email;
        this.profile_pic_url = contact.profile_pic_url;
        this.work_location = contact.work_location;
        this.title = contact.title;
        this.birthday = contact.birthday;
        this.supervisor = contact.supervisor;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public HashMap<String, Long> getTelephone() {
        return telephone;
    }

    public void setTelephone(HashMap<String, Long> telephone) {
        this.telephone = telephone;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public String getWork_location() {
        return work_location;
    }

    public void setWork_location(String work_location) {
        this.work_location = work_location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, String> getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(HashMap<String, String> supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contact_id);
        dest.writeString(this.user_id);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeSerializable(this.telephone);
        dest.writeString(this.email);
        dest.writeString(this.profile_pic_url);
        dest.writeString(this.work_location);
        dest.writeString(this.title);
        dest.writeLong(this.birthday);
        dest.writeSerializable(this.supervisor);
    }

    protected Contact(Parcel in) {
        this.contact_id = in.readString();
        this.user_id = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.telephone = (HashMap<String, Long>) in.readSerializable();
        this.email = in.readString();
        this.profile_pic_url = in.readString();
        this.work_location = in.readString();
        this.title = in.readString();
        this.birthday = in.readLong();
        this.supervisor = (HashMap<String, String>) in.readSerializable();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
