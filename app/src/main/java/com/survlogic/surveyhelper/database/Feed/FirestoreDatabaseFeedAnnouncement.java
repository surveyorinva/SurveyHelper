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
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedAnnouncement;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;

import java.util.ArrayList;
import java.util.Date;

public class FirestoreDatabaseFeedAnnouncement {

    private static final String TAG = "FirestoreDatabaseFeedAn";

    public interface FeedAnnouncmentListener {
        void fetchAnnouncementAll(ArrayList<FeedAnnouncement> announcementList);
        void fetchFeedAnnouncementGetError(boolean isError);
    }


    private Context mContext;
    private FeedAnnouncmentListener mListenerAnnouncement;

    private DocumentSnapshot mLastQueriedAnnouncementList;

    public FirestoreDatabaseFeedAnnouncement(Context context, FeedAnnouncmentListener listener) {
        this.mContext = context;
        this.mListenerAnnouncement = listener;
    }


    public void getFeedAnnouncementListFromFirestore(Date endDate){
        Log.d(TAG, "to_delete: Fetching...");

        long milliseconds = endDate.getTime();

        final ArrayList<FeedAnnouncement> mListAnnouncements = new ArrayList<>();
        DocumentSnapshot mLastQueriedAnnouncement;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("feed_announcements");

        Query query = null;

        if(mLastQueriedAnnouncementList !=null){
            query = ref
                    .whereLessThanOrEqualTo("date_expire", milliseconds)
                    .startAfter(mLastQueriedAnnouncementList);
        }else{
            query = ref
                    .whereLessThanOrEqualTo("date_expire", milliseconds);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "to_delete: inside Listener ");
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        FeedAnnouncement announcement = document.toObject(FeedAnnouncement.class);
                        mListAnnouncements.add(announcement);
                    }

                    if(task.getResult().size() !=0){
                        Log.d(TAG, "to_delete-Success: ");
                        mListenerAnnouncement.fetchAnnouncementAll(mListAnnouncements);
                    }else{
                        Log.d(TAG, "to_delete: Success, but none found in query ");
                        mListenerAnnouncement.fetchFeedAnnouncementGetError(true);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "to_delete: Failure");
                mListenerAnnouncement.fetchFeedAnnouncementGetError(true);
            }
        });

    }


}
