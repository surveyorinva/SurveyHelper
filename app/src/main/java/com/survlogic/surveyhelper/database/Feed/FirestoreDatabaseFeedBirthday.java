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
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedBirthday;
import com.survlogic.surveyhelper.model.FirestoreUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FirestoreDatabaseFeedBirthday {

    private static final String TAG = "FirestoreDatabaseFeedBi";

    public interface FeedBirthdayListener{
        void fetchBirthdayAll(ArrayList<FeedBirthday> birthdayListToday, ArrayList<FeedBirthday> birthdayListAhead);
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
        final ArrayList<FeedBirthday> mListFeedBirthdaysAhead = new ArrayList<>();

        final Calendar birthday = Calendar.getInstance();
        final Calendar compareToDateToday = Calendar.getInstance();
        final Calendar compareToDateTomorrow = Calendar.getInstance();
        final Calendar compareToDateDayAfterNext = Calendar.getInstance();

        compareToDateTomorrow.add(Calendar.DAY_OF_YEAR, 1);
        compareToDateDayAfterNext.add(Calendar.DAY_OF_YEAR,2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("users");

        Query query = null;

        if(mLastQueriedBirthdayList !=null){
            query = ref
                    .startAfter(mLastQueriedBirthdayList)
                    .orderBy("display_name");
        }else{
            query = ref
                    .orderBy("display_name");

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

                            if(user.getProfile_birthday() != null){
                                birthday.setTime(user.getProfile_birthday());
                                compareToDateToday.setTime(dateToCheck);

                                boolean sameDay = birthday.get(Calendar.DAY_OF_YEAR) == compareToDateToday.get(Calendar.DAY_OF_YEAR);
                                boolean sameTommorrow = birthday.get(Calendar.DAY_OF_YEAR) == compareToDateTomorrow.get(Calendar.DAY_OF_YEAR);
                                boolean sameDayAfterNext = birthday.get(Calendar.DAY_OF_YEAR) == compareToDateDayAfterNext.get(Calendar.DAY_OF_YEAR);

                                if(sameDay){
                                    FeedBirthday feedBirthday = new FeedBirthday();
                                    feedBirthday.setUser_id(user.getUser_id());
                                    feedBirthday.setProfile_name(user.getDisplay_name());
                                    feedBirthday.setUserProfilePicUrl(user.getProfile_pic_url());
                                    feedBirthday.setBirthDate(user.getProfile_birthday_long());

                                    mListFeedBirthdays.add(feedBirthday);
                                }

                                if(sameTommorrow || sameDayAfterNext){
                                    FeedBirthday feedBirthday = new FeedBirthday();
                                    feedBirthday.setUser_id(user.getUser_id());
                                    feedBirthday.setProfile_name(user.getDisplay_name());
                                    feedBirthday.setUserProfilePicUrl(user.getProfile_pic_url());

                                    Date dateBirthday = user.getProfile_birthday();
                                    long dateInLong = dateBirthday.getTime();
                                    feedBirthday.setBirthDate(dateInLong);

                                    mListFeedBirthdaysAhead.add(feedBirthday);
                                }
                            }
                        }

                        mListenerBirthday.fetchBirthdayAll(mListFeedBirthdays, mListFeedBirthdaysAhead);

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
