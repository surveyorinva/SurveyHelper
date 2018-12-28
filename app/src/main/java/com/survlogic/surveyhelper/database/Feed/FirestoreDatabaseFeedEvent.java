package com.survlogic.surveyhelper.database.Feed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.activity.staffFeed.view.event.controller.CardFeedEventController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FirestoreDatabaseFeedEvent {

    private static final String TAG = "FirestoreDatabaseFeedEv";

    public interface FeedEventListener {
        void fetchEventsAll(ArrayList<FeedEvent> eventList);
        void fetchFeedEventsGetError(boolean isError);
        void fetchEventLive(FeedEvent event);
        void fetchEventLiveGetError(boolean isError);
        void eventPhotoAddedSuccessfully(FeedEvent event);
        void eventPhotoAddedGetError(boolean isError);
        void eventUserAddedToListSuccessful(int listAdded);
        void eventUserAddedToListGetError(boolean isError);
    }

    private Context mContext;
    private FeedEventListener mListenerEvent;

    private DocumentSnapshot mLastQueriedEventList;

    public FirestoreDatabaseFeedEvent(Context context, FeedEventListener listener) {
        this.mContext = context;
        this.mListenerEvent = listener;
    }

    public void getFeedEventListFromFirestore(Date endDate){

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(endDate);

        final long todaysDayOfYear = cal1.get(Calendar.DAY_OF_YEAR);
        final ArrayList<FeedEvent> mListEvents = new ArrayList<>();
        final ArrayList<FeedEvent> mListEventsToShow = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("feed_events");

        Query query = null;

        if(mLastQueriedEventList !=null){
            query = ref
                    //.whereLessThanOrEqualTo("date_expire_day_of_year", todaysDayOfYear)
                    .startAfter(mLastQueriedEventList)
            ;
        }else{
            query = ref
                    //.whereLessThanOrEqualTo("date_expire_day_of_year", todaysDayOfYear);
            ;
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        FeedEvent event = document.toObject(FeedEvent.class);
                        mListEvents.add(event);
                    }

                    if(task.getResult().size() != 0){
                        for (int i = 0; i < mListEvents.size(); i++){
                            //Todo Condition for events in the next calendar year!

                            FeedEvent event = new FeedEvent(mListEvents.get(i));
                            long eventExpireDay = event.getDate_expire_day_of_year();
                            long diff = eventExpireDay - todaysDayOfYear;

                            if(diff >= 0){
                                mListEventsToShow.add(event);
                            }
                        }
                        mListenerEvent.fetchEventsAll(mListEventsToShow);

                    }else{
                        mListenerEvent.fetchFeedEventsGetError(true);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerEvent.fetchFeedEventsGetError(true);
            }
        });

    }

    public void startUpdatingEventFromFirebaseRealTime(final FeedEvent event){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("feed_events")
                .document(event.getEvent_id());

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG, "onEvent: Listen onFailure", e );
                    mListenerEvent.fetchEventLiveGetError(true);
                }

                if(documentSnapshot !=null && documentSnapshot.exists()){
                    Log.d(TAG, "Current Data: " + documentSnapshot.getData());

                    FeedEvent feedEvent = documentSnapshot.toObject(FeedEvent.class);
                    mListenerEvent.fetchEventLive(feedEvent);
                }
            }
        });

    }

    public void updateEventUploadedEventPictureToFirestore(final FeedEvent event){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference eventReference = db.collection("feed_events")
                .document(event.getEvent_id());

        eventReference.update(
                "pictures_url",event.getPictures_url()

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "updateProfilePic: Success");
                    mListenerEvent.eventPhotoAddedSuccessfully(event);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "updateProfilePic: onFailure: ", e );
                mListenerEvent.eventPhotoAddedGetError(true);
            }
        });

    }

    public void updateEventUploadedEventPictureToFirestore(final FeedEvent event, final String picturePath){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference eventReference = db.collection("feed_events")
                .document(event.getEvent_id());

        eventReference.update(
                "pictures_url",FieldValue.arrayUnion(picturePath)

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "updateProfilePic: Success");
                    mListenerEvent.eventPhotoAddedSuccessfully(event);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "updateProfilePic: onFailure: ", e );
                mListenerEvent.eventPhotoAddedGetError(true);
            }
        });

    }

    public void updateEventUserGoingToEventToFirestore(final FeedEvent event){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getUid();

        DocumentReference eventReference = db.collection("feed_events")
                .document(event.getEvent_id());

        eventReference.update(
                "whos_going",FieldValue.arrayUnion(userID),
                "whos_not_going",FieldValue.arrayRemove(userID)

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "updateEventUserGoingToEventToFirestore: Success");
                    mListenerEvent.eventUserAddedToListSuccessful(CardFeedEventController.LIST_GOING);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "updateEventUserGoingToEventToFirestore: onFailure: ", e );
                mListenerEvent.eventUserAddedToListGetError(true);
            }
        });

    }

    public void updateEventUserNotGoingToEventToFirestore(final FeedEvent event){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getUid();

        DocumentReference eventReference = db.collection("feed_events")
                .document(event.getEvent_id());

        eventReference.update(
                "whos_not_going",FieldValue.arrayUnion(userID),
                "whos_going",FieldValue.arrayRemove(userID),
                "whos_checked_in",FieldValue.arrayRemove(userID)

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "updateEventUserGoingToEventToFirestore: Success");
                    mListenerEvent.eventUserAddedToListSuccessful(CardFeedEventController.LIST_GOING);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "updateEventUserGoingToEventToFirestore: onFailure: ", e );
                mListenerEvent.eventUserAddedToListGetError(true);
            }
        });

    }

    public void updateEventUserCheckedInToEventToFirestore(final FeedEvent event){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getUid();

        DocumentReference eventReference = db.collection("feed_events")
                .document(event.getEvent_id());

        eventReference.update(
                "whos_checked_in",FieldValue.arrayUnion(userID)

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "updateEventUserGoingToEventToFirestore: Success");
                    mListenerEvent.eventUserAddedToListSuccessful(CardFeedEventController.LIST_GOING);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "updateEventUserGoingToEventToFirestore: onFailure: ", e );
                mListenerEvent.eventUserAddedToListGetError(true);
            }
        });

    }

}
