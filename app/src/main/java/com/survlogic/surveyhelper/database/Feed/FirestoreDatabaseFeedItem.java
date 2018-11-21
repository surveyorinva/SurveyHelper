package com.survlogic.surveyhelper.database.Feed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FirestoreDatabaseFeedItem {

    private static final String TAG = "FirestoreDatabaseFeedIt";

    public interface FeedItemListener{
        void fetchFeedItemsAll(ArrayList<FeedItem> itemList);
        void fetchFeedItemsNoneFound();
        void fetchFeedItemsGetError(boolean isError);

    }
    private Context mContext;
    private FeedItemListener mListenerEventItem;

    private DocumentSnapshot mLastQueriedEventItemList;

    public FirestoreDatabaseFeedItem(Context context, FeedItemListener listener) {
        this.mContext = context;
        this.mListenerEventItem = listener;
    }

    public void getFeedItemListFromFirestore(Date queryDate){

        Calendar queryCalendar = Calendar.getInstance();
        queryCalendar.setTime(queryDate);
        final long queryDayOfYear = queryCalendar.get(Calendar.DAY_OF_YEAR);

        final ArrayList<FeedItem> mListItemsFetched = new ArrayList<>();
        final ArrayList<FeedItem> mListItemsToReturn = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("feed_items");

        Query query = null;

        if(mLastQueriedEventItemList !=null){
            query = ref
                    .whereLessThanOrEqualTo("postedOn_day_of_year", queryDayOfYear)
                    .startAfter(mLastQueriedEventItemList);
        }else{
            query = ref
                    .whereLessThanOrEqualTo("postedOn_day_of_year", queryDayOfYear);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        FeedItem feedItem = document.toObject(FeedItem.class);
                        mListItemsFetched.add(feedItem);
                    }

                    if(task.getResult().size() !=0){
                        for(int i = 0;i <mListItemsFetched.size();i++){
                            FeedItem item = new FeedItem((mListItemsFetched.get(i)));
                            long itemShowDay = item.getPostedOn_day_of_year();
                            long diff = itemShowDay - queryDayOfYear;

                            if(diff >= 0){
                                mListItemsToReturn.add(item);
                            }

                        }

                        mListenerEventItem.fetchFeedItemsAll(mListItemsToReturn);
                    }else{
                        mListenerEventItem.fetchFeedItemsGetError(true);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerEventItem.fetchFeedItemsGetError(true);
            }
        });

    }

    public void getFeedItemListFromFirestore(Date queryDate, String room_id){
        Calendar queryCalendar = Calendar.getInstance();
        queryCalendar.setTime(queryDate);
        final long queryDayOfYear = queryCalendar.get(Calendar.DAY_OF_YEAR);

        final ArrayList<FeedItem> mListItemsFetched = new ArrayList<>();
        final ArrayList<FeedItem> mListItemsToReturn = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("feed_items");

        Query query = null;

        if(mLastQueriedEventItemList !=null){
            query = ref
                    .whereLessThanOrEqualTo("postedOn_day_of_year", queryDayOfYear)
                    .whereEqualTo("room_id",room_id)
                    .startAfter(mLastQueriedEventItemList);
        }else{
            query = ref
                    .whereLessThanOrEqualTo("postedOn_day_of_year", queryDayOfYear)
                    .whereEqualTo("room_id",room_id);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        FeedItem feedItem = document.toObject(FeedItem.class);
                        mListItemsFetched.add(feedItem);
                    }

                    if(task.getResult().size() !=0){
                        for(int i = 0;i <mListItemsFetched.size();i++){
                            FeedItem item = new FeedItem((mListItemsFetched.get(i)));
                            long itemShowDay = item.getPostedOn_day_of_year();
                            long diff = itemShowDay - queryDayOfYear;

                            if(diff >= 0){
                                mListItemsToReturn.add(item);
                            }

                        }

                        mListenerEventItem.fetchFeedItemsAll(mListItemsToReturn);
                    }else{
                        mListenerEventItem.fetchFeedItemsNoneFound();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerEventItem.fetchFeedItemsGetError(true);
            }
        });

    }

    public void getFeedItemListFromFirestore(String room_id){
        final ArrayList<FeedItem> mListItemsFetched = new ArrayList<>();
        final ArrayList<FeedItem> mListItemsToReturn = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("feed_items");

        Query query = null;

        if(mLastQueriedEventItemList !=null){
            query = ref
                    .whereEqualTo("room_id",room_id)
                    .orderBy("postedOn", Query.Direction.ASCENDING)
                    .startAfter(mLastQueriedEventItemList);
        }else{
            query = ref
                    .whereEqualTo("room_id",room_id)
                    .orderBy("postedOn", Query.Direction.DESCENDING);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        FeedItem feedItem = document.toObject(FeedItem.class);
                        mListItemsFetched.add(feedItem);
                    }

                    if(task.getResult().size() !=0){
                        for(int i = 0;i <mListItemsFetched.size();i++){
                            FeedItem item = new FeedItem((mListItemsFetched.get(i)));
                            mListItemsToReturn.add(item);

                        }

                        mListenerEventItem.fetchFeedItemsAll(mListItemsToReturn);
                    }else{
                        mListenerEventItem.fetchFeedItemsGetError(true);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerEventItem.fetchFeedItemsGetError(true);
            }
        });

    }

}
