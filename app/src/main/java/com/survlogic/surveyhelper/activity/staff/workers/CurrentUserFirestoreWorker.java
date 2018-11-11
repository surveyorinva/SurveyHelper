package com.survlogic.surveyhelper.activity.staff.workers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.survlogic.surveyhelper.database.Users.FirestoreDatabaseUser;
import com.survlogic.surveyhelper.database.Users.FirestoreDatabaseUserListener;
import com.survlogic.surveyhelper.database.Users_Access.FirestoreDatabaseUserAccess;
import com.survlogic.surveyhelper.database.Users_Access.FirestoreDatabaseUserAccessListener;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.model.FirestoreAppAccessKeys;
import com.survlogic.surveyhelper.model.FirestoreUser;

import java.util.ArrayList;

public class CurrentUserFirestoreWorker implements FirestoreDatabaseUserListener, FirestoreDatabaseUserAccessListener {

    private static final String TAG = "CurrentUserWorker";


    /**
     * Worker Listener
     */

    public interface CurrentUserWorkerListener {

        void sendMeCurrentFirestoreUser(FirestoreUser currentUser);

        void returnErrorNoUserLoggedIn();

    }


    /**
     * FirestoreDatabaseUserListener
     */

    @Override
    public void returnFirestoreUser(FirestoreUser user) {


    }

    @Override
    public void returnFirestoreUserGetError(boolean isError) {

    }

    @Override
    public void updateUserProfileSuccess(FirestoreUser user) {
        mFirestoreUser = user;

        FirestoreDatabaseUserAccess dbAccess = new FirestoreDatabaseUserAccess(mContext,this);
        dbAccess.getAccessKeySecureFromFirestore(user.getAccess_key_secure());
        //dbAccess.getAccessKeyFromFirestore(user.getAccess_level());

        mWorkerListener.sendMeCurrentFirestoreUser(mFirestoreUser);
    }

    @Override
    public void updateUserProfileGetError(boolean isError) {
        //Todo
    }


    /**
     * FirestoreDatabaseUserAccessListener
     */

    @Override
    public void returnFirestoreUserAccess(FirestoreAppAccessKeys key) {
        mFirestoreUserAccessKey = key;

    }

    @Override
    public void returnAllFirestoreUserAccess(ArrayList<FirestoreAppAccessKeys> mListKeys) {

    }

    @Override
    public void returnFirestoreUserAccessGetError(boolean isError) {

    }

    private Context mContext;
    private Activity mActivity;

    private CurrentUserWorkerListener mWorkerListener;

    private AppSettings mSettings;

    private FirestoreUser mFirestoreUser;
    private FirestoreAppAccessKeys mFirestoreUserAccessKey;

    public CurrentUserFirestoreWorker(Context context, CurrentUserWorkerListener listener) {
        Log.d(TAG, "Current User Firestore Worker: Started-------------------------------------> ");
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mWorkerListener = listener;
    }

    public boolean onStart(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            FirestoreDatabaseUser dbUser = new FirestoreDatabaseUser(mContext,this);
            dbUser.updateUserProfileFromFirebaseRealTime(user);
            return true;
        }else{
            mWorkerListener.returnErrorNoUserLoggedIn();
            return false;

        }

    }

}
