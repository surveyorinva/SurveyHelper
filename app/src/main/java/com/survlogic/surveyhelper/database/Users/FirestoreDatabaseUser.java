package com.survlogic.surveyhelper.database.Users;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.DialogUtils;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FirestoreDatabaseUser {

    private static final String TAG = "FirestoreDatabaseUser";
    private Context mContext;
    private FirestoreDatabaseUserListener mListener;


    public FirestoreDatabaseUser(Context context, FirestoreDatabaseUserListener listener) {
        this.mContext = context;
        this.mListener = listener;

    }


    public void getUserDataFromFirestore(FirebaseUser currentUser){
        final ArrayList<FirestoreUser> mListUsers = new ArrayList<>();
        DocumentSnapshot mLastQueriedUser;
        final FirestoreUser userData = new FirestoreUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference usersRef = db.collection("users");

        Query userQuery = usersRef
                .whereEqualTo("user_id", currentUser.getUid());


        userQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        FirestoreUser user = document.toObject(FirestoreUser.class);
                        mListUsers.add(user);
                    }

                    if(task.getResult().size() !=0){
                        Log.d(TAG, "getUser-Success");
                        userData.setFirestoreUser(mListUsers.get(0));
                        mListener.returnFirestoreUser(userData);
                    }

                }else{
                    Log.d(TAG, "getUser-Failure");
                    mListener.returnFirestoreUserGetError(true);
                }
            }
        });

    }


    public void updateUserProfileFromFirebaseRealTime(final FirebaseUser user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("users")
                .document(user.getUid());

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG, "onEvent: Listen onFailure", e );
                    mListener.updateUserProfileGetError(true);
                }

                if(documentSnapshot !=null && documentSnapshot.exists()){
                    Log.d(TAG, "Current Data: " + documentSnapshot.getData());

                    FirestoreUser user = documentSnapshot.toObject(FirestoreUser.class);
                    mListener.updateUserProfileSuccess(user);
                }
            }
        });

    }

    public void updateUserProfilePicToFirestore(final FirestoreUser user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userReference = db.collection("users")
                .document(user.getUser_id());

        userReference.update(
                "profile_pic_url",user.getProfile_pic_url()

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "updateProfilePic: Success");
                    mListener.updateUserProfileSuccess(user);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "updateProfilePic: onFailure: ", e );
                mListener.updateUserProfileGetError(true);
            }
        });

    }

    public void updateUserProfileMetadataToFirestore(final FirestoreUser user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userReference = db.collection("users")
                .document(user.getUser_id());

        userReference.update(
                "display_name",user.getDisplay_name(),
                "telephone_mobile",user.getTelephone_mobile(),
                "telephone_office",user.getTelephone_office(),
                "profile_birthday",user.getProfile_birthday(),
                "profile_birthday_long",user.getProfile_birthday_long()

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "updateProfilePic: Success");
                    mListener.updateUserProfileSuccess(user);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "updateProfilePic: onFailure: ", e );
                mListener.updateUserProfileGetError(true);
            }
        });
    }

    public void updateUserWithMessageToken(final FirestoreUser user, String token){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userReference = db.collection("users")
                .document(user.getUser_id());

        userReference.update(
                "user_token_id",token

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "updateUserWithMessageToken: Success");
                    mListener.updateUserProfileSuccess(user);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "updateUserWithMessageToken: onFailure: ", e );
                mListener.updateUserProfileGetError(true);
            }
        });
    }


}
