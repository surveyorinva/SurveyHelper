package com.survlogic.surveyhelper.activity.appSettings.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.survlogic.surveyhelper.activity.appSettings.inter.ProfileControllerListener;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.database.Users.FirestoreDatabaseUser;
import com.survlogic.surveyhelper.database.Users_Access.FirestoreDatabaseUserAccess;
import com.survlogic.surveyhelper.database.Users_Access.FirestoreDatabaseUserAccessListener;
import com.survlogic.surveyhelper.database.Users.FirestoreDatabaseUserListener;
import com.survlogic.surveyhelper.model.FirestoreAppAccessKeys;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.GraphicRotationUtils;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileSettingsController implements FirestoreDatabaseUserListener, FirestoreDatabaseUserAccessListener{
    private static final String TAG = "ProfileSettingsControl";

    private Context mContext;
    private Activity mActivity;

    private AppSettings mSettings;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirestoreUser mFirestoreUser;
    private ArrayList<FirestoreUser> mListUsers = new ArrayList<>();

    private DocumentSnapshot mLastQueriedUsers;
    private boolean mGotUserProfileFromFireStore;

    private ProfileControllerListener mControllerListener;

    private PreferenceLoader preferenceLoader;

    private Bitmap mUserProfilePictureBitmap;
    private Uri mUserProfilePictureUri;
    private byte[] mUploadBytes;

    private double mProgressUpload = 0;

    public ProfileSettingsController(Context context, FirebaseAuth auth, ProfileControllerListener profileListener) {
        this.mContext = context;
        mActivity = (Activity) context;

        this.mAuth = auth;
        this.mControllerListener = profileListener;
        initController();
    }

    private void initController(){
        loadPreferences();

    }

    private void loadPreferences(){
        preferenceLoader = new PreferenceLoader(mContext);
        mSettings = preferenceLoader.getSettings();


    }

    public void onStart(FirebaseUser currentUser){
        this.mCurrentUser = currentUser;
        updateUI(mCurrentUser);
        //watchUI(mCurrentUser);
    }




    private void updateUI(FirebaseUser user){
        if (user != null){
            FirestoreDatabaseUser dbUser = new FirestoreDatabaseUser(mContext,this);
            dbUser.getUserDataFromFirestore(user);

        }else{
            mControllerListener.returnErrorNoUserLoggedIn();

        }

    }

    private void watchUI(FirebaseUser user){
        if(user !=null){
            FirestoreDatabaseUser dbUser = new FirestoreDatabaseUser(mContext,this);
            dbUser.updateUserProfileFromFirebaseRealTime(user);
        }
    }

    /**
     * Getters & Setters
     */

    public Bitmap getUserProfilePictureBitmap() {
        return mUserProfilePictureBitmap;
    }

    public void setUserProfilePictureBitmap(Bitmap userProfilePictureBitmap) {
        this.mUserProfilePictureBitmap = userProfilePictureBitmap;
    }

    public Uri getUserProfilePictureUri() {
        return mUserProfilePictureUri;
    }

    public void setUserProfilePictureUri(Uri userProfilePictureUri) {
        this.mUserProfilePictureUri = userProfilePictureUri;
    }

    /**
     * Upload Profile Picture to Cloud Storage
     */

    public void startProfilePictureUploadToCloud(){

        if(mUserProfilePictureBitmap != null && mUserProfilePictureUri == null){
            uploadNewPhoto(mUserProfilePictureBitmap);

        }else if (mUserProfilePictureBitmap == null && mUserProfilePictureUri != null){
            uploadNewPhoto(mUserProfilePictureUri);
        }

    }

    private void uploadNewPhoto(Bitmap uploadBitmap){
        BackgroundImageResize resizeBitmap = new BackgroundImageResize(uploadBitmap);
        Uri uri = null;
        resizeBitmap.execute(uri);
    }

    private void uploadNewPhoto(Uri imagePath){

        BackgroundImageResize resizeBitmap = new BackgroundImageResize(null);
        resizeBitmap.execute(imagePath);

    }

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]>{
        Bitmap mBitmap;

        public BackgroundImageResize(Bitmap bitmap) {

            if(bitmap != null){
                this.mBitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected byte[] doInBackground(Uri... params) {

            if(mBitmap == null){
                try{
                    GraphicRotationUtils rotateBitmap = new GraphicRotationUtils();
                    mBitmap = rotateBitmap.handleSamplingAndRotationBitmap(mActivity,params[0]);

                }catch (IOException e){
                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());
                }
            }
            byte[] bytes = null;
            bytes = getBytesFromBitmap(mBitmap, 25);
            return bytes;

        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mUploadBytes = bytes;
            executeUploadTask();

        }

    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream);
        return stream.toByteArray();
    }



    /**
     * Firestore Methods
     */

    @Override
    public void returnFirestoreUser(FirestoreUser user) {

        mFirestoreUser = user;

        FirestoreDatabaseUserAccess dbAccess = new FirestoreDatabaseUserAccess(mContext,this);
        dbAccess.getAccessKeySecureFromFirestore(user.getAccess_key_secure());

        mControllerListener.returnCurrentUser(user);
    }

    @Override
    public void returnFirestoreUserGetError(boolean isError) {
        mControllerListener.returnErrorNoUserLoggedIn();
    }

    @Override
    public void returnFirestoreUserAccess(FirestoreAppAccessKeys key) {
        mControllerListener.returnCurrentUserAccess(key);
    }

    @Override
    public void returnAllFirestoreUserAccess(ArrayList<FirestoreAppAccessKeys> mListKeys) {
        //Not Used
    }

    @Override
    public void returnFirestoreUserAccessGetError(boolean isError) {
        mControllerListener.returnErrorGettingAccessKeys();
    }


    @Override
    public void updateUserProfileSuccess(FirestoreUser user) {
        mControllerListener.returnCurrentUser(user);

    }


    @Override
    public void updateUserProfileGetError(boolean isError) {
        DialogUtils.showToast(mActivity,"Failed in updating");
    }

    /**
     * Firebase Storage Methods
     */

    private void executeUploadTask(){
        final FirestoreDatabaseUser dbUser = new FirestoreDatabaseUser(mContext,this);

        final String userID = mCurrentUser.getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("users/profile/pics/" + userID);

        UploadTask uploadTask = ref.putBytes(mUploadBytes);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if( currentProgress > (mProgressUpload + 10)){
                    mProgressUpload = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "onProgress: " + mProgressUpload);
                }
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "onSuccess: Upload File Saved at: " + downloadUri);
                    mFirestoreUser.setProfile_pic_url(downloadUri.toString());
                    dbUser.updateUserProfilePicToFirestore(mFirestoreUser);

                }
            }
        });
    }


}