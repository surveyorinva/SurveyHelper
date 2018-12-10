package com.survlogic.surveyhelper.activity.staffFeed.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.view.CardFeedAnnouncement;
import com.survlogic.surveyhelper.activity.staffFeed.view.CardFeedBirthday;
import com.survlogic.surveyhelper.activity.staffFeed.view.CardFeedBirthdayHeader;
import com.survlogic.surveyhelper.activity.staffFeed.view.CardFeedEmpty;
import com.survlogic.surveyhelper.activity.staffFeed.view.Event.CardFeedEvent;
import com.survlogic.surveyhelper.activity.staffFeed.view.CardFeedItem;
import java.util.ArrayList;

public class StaffFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "StaffFeedAdapter";

    public interface AdapterListener{
        void refreshView();
        void openImageGetterDialog(int returnFeedTo, int atPosition);
        void returnToRecyclerURI(Uri uri);
        void returnToRecyclerBitmap(Bitmap bitmap);

    }

    private Context mContext;
    private AdapterListener mAdapterListener;
    private ArrayList<Feed> mFeedList;

    public final static int FEED_SYSTEM_DEFAULT = 0,
                            FEED_SYSTEM_EMPTY = 1,
                            FEED_ANNOUNCEMENT = 101,
                            FEED_EVENT = 201,
                            FEED_BIRTHDAY_HEADER_TODAY = 301,
                            FEED_BIRTHDAY_TODAY = 302,
                            FEED_BIRTHDAY_HEADER_FUTURE = 303,
                            FEED_BIRTHDAY_FUTURE = 304,
                            FEED_FEED_ITEM = 401;

    private CardFeedEvent vhEvent;

    public StaffFeedAdapter(Context context, AdapterListener listener, ArrayList<Feed> feedList) {
        this.mContext = context;
        this.mAdapterListener = listener;
        this.mFeedList = feedList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case FEED_ANNOUNCEMENT:
                View vAnnouncement = mInflater.inflate(R.layout.staff_feed_content_card_announcement,parent,false);
                viewHolder = new CardFeedAnnouncement(vAnnouncement,mContext,mAdapterListener);
                break;

            case FEED_EVENT:
                View vEvent = mInflater.inflate(R.layout.staff_feed_content_card_event,parent,false);
                viewHolder = new CardFeedEvent(vEvent,mContext,mAdapterListener);
                break;

            case FEED_BIRTHDAY_HEADER_TODAY:
                View vBirthdayHeaderToday = mInflater.inflate(R.layout.staff_feed_content_list_header_birthday_today,parent,false);
                viewHolder = new CardFeedBirthdayHeader(vBirthdayHeaderToday);
                break;

            case FEED_BIRTHDAY_TODAY:
                View vBirthdayToday = mInflater.inflate(R.layout.staff_feed_content_card_birthday,parent,false);
                viewHolder = new CardFeedBirthday(vBirthdayToday,mContext,mAdapterListener,CardFeedBirthday.TYPE_TODAY);
                break;

            case FEED_BIRTHDAY_HEADER_FUTURE:
                View vBirthdayHeaderFuture = mInflater.inflate(R.layout.staff_feed_content_list_header_birthday_future,parent,false);
                viewHolder = new CardFeedBirthdayHeader(vBirthdayHeaderFuture);
                break;

            case FEED_BIRTHDAY_FUTURE:
                View vBirthdayFuture = mInflater.inflate(R.layout.staff_feed_content_card_birthday_future,parent,false);
                viewHolder = new CardFeedBirthday(vBirthdayFuture,mContext,mAdapterListener,CardFeedBirthday.TYPE_FUTURE);
                break;

            case FEED_FEED_ITEM:
                View vFeedItem = mInflater.inflate(R.layout.staff_feed_content_card_feed,parent,false);
                viewHolder = new CardFeedItem(vFeedItem,mContext,mAdapterListener);
                break;

            case FEED_SYSTEM_EMPTY:
                View vEmpty = mInflater.inflate(R.layout.staff_feed_content_list_empty,parent,false);
                viewHolder = new CardFeedEmpty(vEmpty);
                break;

            default:
                View v = mInflater.inflate(R.layout.staff_feed_content_card_feed,parent,false);
                viewHolder = new CardFeedItem(v,mContext,mAdapterListener);
                break;
        }

        return viewHolder;


    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = mFeedList.get(position);
        return feed.getFeed_type();

    }

    public boolean isFullCard(int position){
        Feed feed = mFeedList.get(position);
        if (feed.getFeed_type() == FEED_BIRTHDAY_TODAY){
            return false;
        }else return feed.getFeed_type() != FEED_BIRTHDAY_FUTURE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()){

            case FEED_ANNOUNCEMENT:
                CardFeedAnnouncement vhAnnouncement = (CardFeedAnnouncement) viewHolder;
                vhAnnouncement.configureViewHolder(mFeedList,position);
                break;

            case FEED_EVENT:
                vhEvent = (CardFeedEvent) viewHolder;
                vhEvent.configureViewHolder(mFeedList,position);
                break;

            case FEED_BIRTHDAY_HEADER_TODAY:
                CardFeedBirthdayHeader vhBirthdayHeaderToday = (CardFeedBirthdayHeader) viewHolder;
                vhBirthdayHeaderToday.configureViewHolder(position);
                break;

            case FEED_BIRTHDAY_TODAY:
                CardFeedBirthday vhBirthdayToday = (CardFeedBirthday) viewHolder;
                vhBirthdayToday.configureViewHolder(mFeedList,position, true);
                break;

            case FEED_BIRTHDAY_HEADER_FUTURE:
                CardFeedBirthdayHeader vhBirthdayHeaderFuture = (CardFeedBirthdayHeader) viewHolder;
                vhBirthdayHeaderFuture.configureViewHolder(position);
                break;

            case FEED_BIRTHDAY_FUTURE:
                CardFeedBirthday vhBirthdayFuture = (CardFeedBirthday) viewHolder;
                vhBirthdayFuture.configureViewHolder(mFeedList,position, false);
                break;

            case FEED_FEED_ITEM:
                CardFeedItem vhFeedItem = (CardFeedItem) viewHolder;
                vhFeedItem.configureViewHolder(mFeedList,position);

                break;

            case FEED_SYSTEM_EMPTY:
                CardFeedEmpty vhFeedEmpty = (CardFeedEmpty) viewHolder;
                vhFeedEmpty.configureViewHolder(position);
        }

    }

    @Override
    public int getItemCount() {
        return mFeedList == null ? 0 : mFeedList.size();
    }


    public void swapItems(ArrayList<Feed> items) {
        this.mFeedList = items;
        notifyDataSetChanged();
    }


    public void setImageUriForItem(int item, int position, Uri uri){
        switch (item){
            case FEED_EVENT:
                vhEvent.setSingleImageUri(position, uri);
                break;

        }


    }

    public void setImageBitmapForItem(int item, int position, Bitmap bitmap){
        switch (item){
            case FEED_EVENT:
                vhEvent.setSingleImageBitmap(position, bitmap);
                break;

        }
    }







}
