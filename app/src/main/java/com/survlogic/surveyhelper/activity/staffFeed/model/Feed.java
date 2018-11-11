package com.survlogic.surveyhelper.activity.staffFeed.model;

public class Feed {

    private int feed_type;
    private FeedAnnouncement announcement;
    private FeedBirthday birthday;
    private FeedEvent event;
    private FeedItem item;

    public Feed() {
    }

    public int getFeed_type() {
        return feed_type;
    }

    public void setFeed_type(int feed_type) {
        this.feed_type = feed_type;
    }

    public FeedAnnouncement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(FeedAnnouncement announcement) {
        this.announcement = announcement;
    }

    public FeedBirthday getBirthday() {
        return birthday;
    }

    public void setBirthday(FeedBirthday birthday) {
        this.birthday = birthday;
    }

    public FeedEvent getEvent() {
        return event;
    }

    public void setEvent(FeedEvent event) {
        this.event = event;
    }

    public FeedItem getItem() {
        return item;
    }

    public void setItem(FeedItem item) {
        this.item = item;
    }
}
