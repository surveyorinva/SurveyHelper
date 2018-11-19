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
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FirestoreDatabaseFeedEvent {

    private static final String TAG = "FirestoreDatabaseFeedEv";

    public interface FeedEventListener {
        void fetchEventsAll(ArrayList<FeedEvent> eventList);
        void fetchFeedEventsGetError(boolean isError);
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
}
