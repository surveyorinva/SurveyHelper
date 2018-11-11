package com.survlogic.surveyhelper.database.Feed;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;
import com.survlogic.surveyhelper.model.FirestoreUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FirestoreDatabaseFeedBirthday {

    private static final String TAG = "FirestoreDatabaseFeedBi";

    public interface FeedBirthdayListener{
        void fetchBirthdayAll(ArrayList<FeedBirthday> birthdayList);
        void fetchFeedBirthdayGetError(boolean isError);
    }

    private Context mContext;
    private FeedBirthdayListener mListenerBirthday;

    private DocumentSnapshot mLastQueriedBirthdayList;

    public FirestoreDatabaseFeedBirthday(Context context, FeedBirthdayListener listener) {
        this.mContext = context;
        this.mListenerBirthday = listener;
    }

    public void getFeedBirthdayFromFirestore(final Date dateToCheck){
        final ArrayList<FirestoreUser> mListUsers = new ArrayList<>();
        final ArrayList<FeedBirthday> mListFeedBirthdays = new ArrayList<>();
        DocumentSnapshot mLastQueriedUserst;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("users");

        Query query = null;

        if(mLastQueriedBirthdayList !=null){
            query = ref
                    .startAfter(mLastQueriedBirthdayList);
        }else{
            query = ref;

        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        FirestoreUser user = document.toObject(FirestoreUser.class);
                        mListUsers.add(user);

                    }

                    if(task.getResult().size() !=0){

                        for (int i = 0; i < mListUsers.size(); i++){

                            FirestoreUser user = new FirestoreUser();
                            user.setFirestoreUser(mListUsers.get(i));

                            Calendar birthday = Calendar.getInstance();
                            Calendar compareToDate = Calendar.getInstance();

                            birthday.setTime(user.getProfile_birthday());
                            compareToDate.setTime(dateToCheck);

                            boolean sameDay = birthday.get(Calendar.DAY_OF_YEAR) == compareToDate.get(Calendar.DAY_OF_YEAR);

                            if(sameDay){
                                FeedBirthday feedBirthday = new FeedBirthday();
                                feedBirthday.setUser_id(user.getUser_id());
                                feedBirthday.setUserProfilePicUrl(user.getProfile_pic_url());
                                feedBirthday.setBirthDate(user.getProfile_birthday_long());

                                mListFeedBirthdays.add(feedBirthday);
                            }

                        }

                        mListenerBirthday.fetchBirthdayAll(mListFeedBirthdays);
                    }else{
                        mListenerBirthday.fetchFeedBirthdayGetError(true);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerBirthday.fetchFeedBirthdayGetError(true);
            }
        });

    }


}
