package com.survlogic.surveyhelper.database.Feed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FirestoreDatabaseFeedItem {

    private static final String TAG = "FirestoreDatabaseFeedIt";

    public interface FeedItemListener{
        void fetchFeedItemsAll(ArrayList<FeedItem> itemList);
        void fetchFeedItemsNoneFound();
        void fetchFeedItemsGetError(boolean isError);
        void addNewFeedItemSuccess(String feedItem_id);
        void addNewFeedItemFailure(boolean isError);
        void updateNewFeedItemSuccess();
        void updateNewFeedItemFailure(boolean isError);
        void fetchFeedItemLive(FeedItem feedItem);
        void fetchFeedItemLiveGetError(boolean isError);
        void itemPhotoAddedSuccessfully();
        void itemPhotoAddedGetError(boolean isError);


    }
    private Context mContext;
    private FeedItemListener mListenerFeedItem;

    private DocumentSnapshot mLastQueriedEventItemList;

    public FirestoreDatabaseFeedItem(Context context, FeedItemListener listener) {
        this.mContext = context;
        this.mListenerFeedItem = listener;
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

                        mListenerFeedItem.fetchFeedItemsAll(mListItemsToReturn);
                    }else{
                        mListenerFeedItem.fetchFeedItemsGetError(true);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerFeedItem.fetchFeedItemsGetError(true);
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
                    .whereEqualTo("published",true)
                    .startAfter(mLastQueriedEventItemList);
        }else{
            query = ref
                    .whereLessThanOrEqualTo("postedOn_day_of_year", queryDayOfYear)
                    .whereEqualTo("room_id",room_id)
                    .whereEqualTo("published",true);
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

                        mListenerFeedItem.fetchFeedItemsAll(mListItemsToReturn);
                    }else{
                        mListenerFeedItem.fetchFeedItemsNoneFound();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerFeedItem.fetchFeedItemsGetError(true);
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
                    .whereEqualTo("published",true)
                    .orderBy("postedOn", Query.Direction.ASCENDING)
                    .startAfter(mLastQueriedEventItemList);
        }else{
            query = ref
                    .whereEqualTo("room_id",room_id)
                    .whereEqualTo("published",true)
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

                        mListenerFeedItem.fetchFeedItemsAll(mListItemsToReturn);
                    }else{
                        mListenerFeedItem.fetchFeedItemsGetError(true);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerFeedItem.fetchFeedItemsGetError(true);
            }
        });

    }

    public void startUpdatingFeedItemFromFirebaseRealTime(final FeedItem item){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("feed_items")
                .document(item.getItem_id());

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    mListenerFeedItem.fetchFeedItemLiveGetError(true);
                }

                if(documentSnapshot !=null && documentSnapshot.exists()){
                    FeedItem feeditem = documentSnapshot.toObject(FeedItem.class);
                    mListenerFeedItem.fetchFeedItemLive(feeditem);
                }
            }
        });

    }

    public void addNewMessage(FeedItem newMessage){
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String dateString = s.format(new Date());

        final String feed_id = dateString + "_" + user_id;
        newMessage.setItem_id(feed_id);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int mDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        newMessage.setPostedOn_day_of_year(mDayOfYear);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newFeedItemRef = db
                .collection("feed_items")
                .document(feed_id);

        newFeedItemRef.set(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Added NewFeedItem - Success");
                        mListenerFeedItem.addNewFeedItemSuccess(feed_id);
                }else{
                    Log.d(TAG, "Added NewFeedItem - Failed");
                        mListenerFeedItem.addNewFeedItemFailure(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Added NewFeedItem - Failed");
                mListenerFeedItem.addNewFeedItemFailure(true);

            }
        });
    }

    public void updateMessage(FeedItem message){
        String feed_id = message.getItem_id();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newFeedItemRef = db
                .collection("feed_items")
                .document(feed_id);

        newFeedItemRef.set(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Updated NewFeedItem - Success");
                    mListenerFeedItem.updateNewFeedItemSuccess();
                }else{
                    Log.d(TAG, "Updated NewFeedItem - Failed");
                    mListenerFeedItem.addNewFeedItemFailure(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Updated NewFeedItem - Failed");
                mListenerFeedItem.updateNewFeedItemFailure(true);

            }
        });

    }

    public void deleteMessage(final FeedItem item){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("feed_items").document(item.getItem_id())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "to_delete: Item Deleted ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "to_delete: Error Deleting Item ");
                    }
                });
    }

    public void updateFeedItemUploadedItemPictureToFirestore(final FeedItem item, final String picturePath){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference eventReference = db.collection("feed_items")
                .document(item.getItem_id());

        eventReference.update(
                "photo_link",FieldValue.arrayUnion(picturePath)

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mListenerFeedItem.itemPhotoAddedSuccessfully();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerFeedItem.itemPhotoAddedGetError(true);
            }
        });

    }

}
