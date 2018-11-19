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
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedAnnouncement;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;

import java.util.ArrayList;
import java.util.Calendar;
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
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(endDate);
        final long todaysDayOfYear = cal1.get(Calendar.DAY_OF_YEAR);

        final ArrayList<FeedAnnouncement> mListAnnouncements = new ArrayList<>();
        final ArrayList<FeedAnnouncement> mListAnnouncementsToShow = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("feed_announcements");

        Query query = null;

        if(mLastQueriedAnnouncementList !=null){
            query = ref
                    //.whereLessThanOrEqualTo("date_expire_day_of_year", todaysDayOfYear)
                    .startAfter(mLastQueriedAnnouncementList)
            ;
        }else{
            query = ref
                    //.whereLessThanOrEqualTo("date_expire_day_of_year", todaysDayOfYear)
            ;
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        FeedAnnouncement announcement = document.toObject(FeedAnnouncement.class);
                        mListAnnouncements.add(announcement);
                    }

                    if(task.getResult().size() !=0){
                        for(int i=0;i<mListAnnouncements.size();i++){

                            //Todo Condition for announcements in the next calendar year!

                            FeedAnnouncement announcement = new FeedAnnouncement(mListAnnouncements.get(i));
                            long announcementExpireDay = announcement.getDate_expire_day_of_year();
                            long diff = announcementExpireDay - todaysDayOfYear;

                            if(diff>=0){
                                mListAnnouncementsToShow.add(announcement);
                            }
                        }
                        mListenerAnnouncement.fetchAnnouncementAll(mListAnnouncementsToShow);
                    }else{
                        mListenerAnnouncement.fetchFeedAnnouncementGetError(true);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerAnnouncement.fetchFeedAnnouncementGetError(true);
            }
        });

    }


}
