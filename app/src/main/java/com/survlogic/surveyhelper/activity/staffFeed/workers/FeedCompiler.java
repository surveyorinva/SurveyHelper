package com.survlogic.surveyhelper.activity.staffFeed.workers;

import android.content.Context;
import android.util.Log;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedAnnouncement;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedItem;

import java.util.ArrayList;

public class FeedCompiler  {

    private static final String TAG = "FeedCompiler";

    private Context mContext;

    private ArrayList<FeedAnnouncement> mListAnnouncements = new ArrayList<>();
    private ArrayList<FeedEvent> mListEvents = new ArrayList<>();
    private ArrayList<FeedItem> mListFeedItems = new ArrayList<>();
    private ArrayList<FeedBirthday> mListBirthdays = new ArrayList<>();
    private ArrayList<FeedBirthday> mListBirthdaysFuture = new ArrayList<>();

    public FeedCompiler(Context context) {
        this.mContext = context;
    }

    public void setListAnnouncements(ArrayList<FeedAnnouncement> mListAnnouncements) {
        this.mListAnnouncements = mListAnnouncements;
    }

    public void setListEvents(ArrayList<FeedEvent> mListEvents) {
        this.mListEvents = mListEvents;
    }

    public void setListFeedItems(ArrayList<FeedItem> mListFeedItems) {
        this.mListFeedItems = mListFeedItems;
    }

    public void setListBirthdays(ArrayList<FeedBirthday> mListBirthdays) {
        this.mListBirthdays = mListBirthdays;
    }

    public void setListBirthdaysFuture(ArrayList<FeedBirthday> mListBirthdaysFuture) {
        this.mListBirthdaysFuture = mListBirthdaysFuture;
    }


    /**
     * Compile Order
     * 0.) Feed Action Item
     * 1.) Announcements
     * 2.) Events
     * 3.) Birthday Today Header
     * 4.) Birthday Today
     * 5.) Birthday Future Header
     * 6.) Birthday Future
     * 7.) Feeds
     * 8.) Empty
     */


    public ArrayList<Feed> compileFeeds(){
        ArrayList<Feed> feeds = new ArrayList<>();

        Feed actionItemFeed = new Feed();
        actionItemFeed.setFeed_type(mContext.getResources().getInteger(R.integer.FEED_SYSTEM_ACTION_ITEM));
        feeds.add(actionItemFeed);

        if(mListAnnouncements.size() !=0){
            for(int i=0;i<mListAnnouncements.size();i++){
                Feed feed = new Feed();
                feed.setFeed_type(mContext.getResources().getInteger(R.integer.FEED_ANNOUNCEMENT));
                feed.setAnnouncement(mListAnnouncements.get(i));
                feeds.add(feed);
            }
        }

        if(mListEvents.size() !=0){
            for(int i=0;i<mListEvents.size();i++){
                Feed feed = new Feed();
                feed.setFeed_type(mContext.getResources().getInteger(R.integer.FEED_EVENT));
                feed.setEvent(mListEvents.get(i));
                feeds.add(feed);
            }
        }

        if(mListBirthdays.size() !=0){
            Feed feedHeader = new Feed();
            feedHeader.setFeed_type(mContext.getResources().getInteger(R.integer.FEED_BIRTHDAY_HEADER_TODAY));
            feeds.add(feedHeader);

            for(int i=0;i<mListBirthdays.size();i++){
                Feed feed = new Feed();
                feed.setFeed_type(mContext.getResources().getInteger(R.integer.FEED_BIRTHDAY_TODAY));
                feed.setBirthday(mListBirthdays.get(i));
                feeds.add(feed);
            }
        }

        if(mListBirthdaysFuture.size() !=0){
            Feed feedHeader = new Feed();
            feedHeader.setFeed_type(mContext.getResources().getInteger(R.integer.FEED_BIRTHDAY_HEADER_FUTURE));
            feeds.add(feedHeader);

            for(int i=0;i<mListBirthdaysFuture.size();i++){
                Feed feed = new Feed();
                feed.setFeed_type(mContext.getResources().getInteger(R.integer.FEED_BIRTHDAY_FUTURE));
                feed.setBirthday(mListBirthdaysFuture.get(i));
                feeds.add(feed);
            }
        }

        if(mListFeedItems.size() !=0){
            for(int i=0;i<mListFeedItems.size();i++){
                Feed feed = new Feed();
                feed.setFeed_type(mContext.getResources().getInteger(R.integer.FEED_FEED_ITEM));
                feed.setItem(mListFeedItems.get(i));
                feeds.add(feed);
            }
        }

        Feed feedEmpty = new Feed();
        feedEmpty.setFeed_type(mContext.getResources().getInteger(R.integer.FEED_SYSTEM_EMPTY));
        feeds.add(feedEmpty);

        return feeds;
    }


}
