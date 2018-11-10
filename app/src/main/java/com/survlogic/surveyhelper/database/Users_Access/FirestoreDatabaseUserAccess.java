package com.survlogic.surveyhelper.database.Users_Access;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.survlogic.surveyhelper.model.FirestoreAppAccessKeys;

import java.util.ArrayList;

public class FirestoreDatabaseUserAccess {

    private static final String TAG = "FirestoreDbUserAccess";

    private Context mContext;
    private Activity mActivity;

    private FirestoreDatabaseUserAccessListener mListener;

    private DocumentSnapshot mLastQueriedAccessKey;
    private ArrayList<FirestoreAppAccessKeys> mListKeys = new ArrayList<>();


    public FirestoreDatabaseUserAccess(Context context, FirestoreDatabaseUserAccessListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;
        this.mListener = listener;
    }

    public void getAllAccessKeysFromFirestore(){
        Log.d(TAG, "getAccessKeys: Started");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference accessKeysRef = db.collection("app_access_keys");

        Query accessKeysQuery = null;
        if(mLastQueriedAccessKey !=null){
            accessKeysQuery = accessKeysRef
                    .startAfter(mLastQueriedAccessKey);
        }else{
            accessKeysQuery = accessKeysRef;

        }

        accessKeysQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        FirestoreAppAccessKeys appAccessKeys = document.toObject(FirestoreAppAccessKeys.class);
                        mListKeys.add(appAccessKeys);
                    }

                    if(task.getResult().size() !=0){
                        mLastQueriedAccessKey = task.getResult().getDocuments()
                                .get(task.getResult().size() -1);
                    }
                    Log.d(TAG, "getAccessKeys-Success");
                    mListener.returnAllFirestoreUserAccess(mListKeys);

                }else{
                    Log.d(TAG, "getAccessKeys-Failure");
                    mListener.returnFirestoreUserAccessGetError(true);
                }
            }
        });

    }

    public void getAccessKeyFromFirestore(String keyToFind){
        Log.d(TAG, "getAccessKeys: Started");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference accessKeysRef = db.collection("app_access_keys");

        Query accessKeysQuery = accessKeysRef
                .whereEqualTo("access_key_id", keyToFind);


        accessKeysQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        FirestoreAppAccessKeys appAccessKeys = document.toObject(FirestoreAppAccessKeys.class);
                        mListKeys.add(appAccessKeys);
                    }

                    Log.d(TAG, "getAccessKeys-Success");
                    mListener.returnFirestoreUserAccess(mListKeys.get(0));

                }else{
                    Log.d(TAG, "getAccessKeys-Failure");
                    mListener.returnFirestoreUserAccessGetError(true);
                }
            }
        });

    }

    public void getAccessKeySecureFromFirestore(String keyToFind){
        Log.d(TAG, "getAccessKeys: Started");
        Log.d(TAG, "getAccessKeys: key_secure: " + keyToFind);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference accessKeysRef = db.collection("app_access_keys");

        Query accessKeysQuery = accessKeysRef
                .whereEqualTo("access_key_secure", keyToFind);


        accessKeysQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        FirestoreAppAccessKeys appAccessKeys = document.toObject(FirestoreAppAccessKeys.class);
                        mListKeys.add(appAccessKeys);
                    }

                    Log.d(TAG, "getAccessKeys-Success");
                    if(mListKeys.size() > 0){
                        mListener.returnFirestoreUserAccess(mListKeys.get(0));
                    }else{
                        Log.d(TAG, "getAccessKeys-Failure");
                        mListener.returnFirestoreUserAccessGetError(true);
                    }


                }else{
                    Log.d(TAG, "getAccessKeys-Failure");
                    mListener.returnFirestoreUserAccessGetError(true);
                }
            }
        });

    }



}
