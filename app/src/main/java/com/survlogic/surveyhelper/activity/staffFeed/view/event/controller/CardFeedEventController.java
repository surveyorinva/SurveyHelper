package com.survlogic.surveyhelper.activity.staffFeed.view.event.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.photoGallery.PhotoGalleryActivity;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedEvent;
import com.survlogic.surveyhelper.activity.staffFeed.view.event.CardFeedEvent;
import com.survlogic.surveyhelper.activity.staffFeed.view.event.photo.EventPhotoAdapter;
import com.survlogic.surveyhelper.database.Feed.FirestoreDatabaseFeedEvent;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.GraphicRotationUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CardFeedEventController implements FirestoreDatabaseFeedEvent.FeedEventListener, FABProgressListener {

    private static final String TAG = "CardFeedEventController";

    public interface CardFeedEventWorkerListener{
        void returnSuccessful();
        void returnInError(boolean isError);
        void callZoomablePhotoDialog(String photoURL);
        void updateFeedEventInAdapter(FeedEvent event);
    }

    /**
     * FirestoreDatabaseFeedEvent.FeedEventListener
     */

    @Override
    public void fetchEventsAll(ArrayList<FeedEvent> eventList) {

    }

    @Override
    public void fetchFeedEventsGetError(boolean isError) {
        //Todo
        Log.d(TAG, "to_delete: Error Message Here ");
    }

    @Override
    public void eventPhotoAddedSuccessfully(FeedEvent event) {

    }

    @Override
    public void eventPhotoAddedGetError(boolean isError) {
        //Todo
        Log.d(TAG, "to_delete: Error Message Here for Photos Upload ");
    }


    @Override
    public void eventUserAddedToListSuccessful(int listAdded) {
    }

    @Override
    public void eventUserAddedToListGetError(boolean isError) {
        //Todo
        Log.d(TAG, "to_delete: User Not Added, Error Message Here ");

    }

    @Override
    public void fetchEventLive(FeedEvent event) {
        mEvent = event;
        workerListener.updateFeedEventInAdapter(event);

        checkForGridView();

        if(isGridViewSetup && isEventPhotoGalleryNeeded){
            showPhotoGridView();
        }
    }

    @Override
    public void fetchEventLiveGetError(boolean isError) {

    }

    /**
     * FABProgressListener
     */

    @Override
    public void onFABProgressAnimationEnd() {
        Snackbar.make(fabProgressCircle, R.string.staff_feed_card_event_image_upload_complete, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();


    }


    private Context mContext;
    private Activity mActivity;
    private FeedEvent mEvent;
    private String mUserID;

    private CardFeedEventWorkerListener workerListener;

    private Bitmap mUserUploadedEventPictureBitmap;
    private Uri mUserUploadedEventPictureUri;
    private byte[] mUploadBytes;
    private double mProgressUpload = 0;

    private int mPhotoQuality = 90;

    public static final int LIST_GOING =  0,  LIST_NOT_GOING = 1, LIST_CHECKED_IN = 2;
    private static final int TIME_MINUTES = 1,  TIME_HOURS = 2, TIME_DAYS = 3,  TIME_WEEKS = 4;

    private GridView photoGridView;
    private EventPhotoAdapter photoGridAdapter;
    private boolean isPhotoGridAdapterSetup = false;
    private TextView tvWarning;

    private ArrayList<String> mPhotoList;

    private boolean isPopupWindowShown = false;
    private boolean isGridViewSetup = false;
    private boolean isEventUpdatingLive = false;
    private boolean isEventPhotoGalleryNeeded = false;

    private FABProgressCircle fabProgressCircle;

    public CardFeedEventController(Context context, CardFeedEventWorkerListener workerListener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.workerListener = workerListener;
    }

    public FeedEvent getEvent() {
        return mEvent;
    }

    public void setEvent(FeedEvent event) {
        this.mEvent = event;
        this.mUserID = setUserID();

        if(!isEventUpdatingLive){
            startLiveEvent();
            isEventUpdatingLive = true;
        }

        int result = determineEventPhase();
    }


    public void setUserUploadedEventPictureBitmap(Bitmap bitmap) {
        this.mUserUploadedEventPictureBitmap = bitmap;
    }

    public void setUserUploadedEventPictureUri(Uri uri) {
        this.mUserUploadedEventPictureUri = uri;
    }

    public int getPhotoQuality() {
        return mPhotoQuality;
    }

    public void setPhotoQuality(int photoQuality) {
        this.mPhotoQuality = photoQuality;
    }

    public void setGridView(GridView gridView){
        this.photoGridView = gridView;
        isGridViewSetup = true;

        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity mActivity = (Activity) mContext;
                if(position == 3){
                    Intent intentOpenGallery = new Intent(mContext,PhotoGalleryActivity.class);
                    intentOpenGallery.putExtra(mActivity.getResources().getString(R.string.KEY_GALLERY_PHOTO_STRING_ARRAY_URL),mPhotoList);
                    mActivity.startActivity(intentOpenGallery);
                }else{
                    String photoURL = mPhotoList.get(position);
                    createDialogPhotoView(photoURL);
                }

            }
        });

        photoGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DialogUtils.showAlertDialog(mContext,mActivity.getResources().getString(R.string.staff_feed_event_todo_message_delete_photo_title),mActivity.getResources().getString(R.string.staff_feed_event_todo_message_delete_photo_summary));
                return true;
            }
        });

    }

    public void setWarningMessageView(TextView warningMessage) {
        this.tvWarning = warningMessage;
    }

    private String setUserID(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

    //----------------------------------------------------------------------------------------------//
        private void createDialogPhotoView(String photoURL){
            workerListener.callZoomablePhotoDialog(photoURL);
        }

    //----------------------------------------------------------------------------------------------//

    public int determineEventPhase(){
        int result;

        Date todayDate = Calendar.getInstance().getTime();
        long todayMilliseconds = todayDate.getTime();

        //determine if event has expired
        boolean isEventExpired = isEventExpired();

        if(isEventExpired){
            result =  CardFeedEvent.EVENT_PHASE_OVER;
            return result;
        }

        // determine if user is in the event to go list  or the no-go list
        boolean isUserGoing = isUserInList(LIST_GOING);
        boolean isUserNotGoing = isUserInList(LIST_NOT_GOING);

        if(isUserGoing){
            long howLongUntilEventDays = convertMillisecondsToReadableTime(TIME_DAYS, numberOfMillisecondsBetweenNowAndEvent());

            if(howLongUntilEventDays >= 1){
                result = CardFeedEvent.EVENT_PHASE_COUNTDOWN_DAYS;
                return result;
            }

            long howLongUntilEventStartHours = convertMillisecondsToReadableTime(TIME_HOURS, numberOfMillisecondsBetweenNowAndEvent());
            long howLongUntilEventStartsMinutes = convertMillisecondsToReadableTime(TIME_MINUTES, numberOfMillisecondsBetweenNowAndEvent());

            if(howLongUntilEventStartHours > 1 && howLongUntilEventStartHours <= 24){
                result = CardFeedEvent.EVENT_PHASE_COUNTDOWN_DAYS;
                return result;
            }


            if(howLongUntilEventStartsMinutes >= 0 && howLongUntilEventStartsMinutes <= 60){
                result = CardFeedEvent.EVENT_PHASE_DAY_OF_EVENT;
                return result;
            }

            long howLongUntilEventEndsMinutes = convertMillisecondsToReadableTime(TIME_MINUTES, numberOfMillisecondsBetweenNowAndEventEnd());

            if(howLongUntilEventStartsMinutes <= 0 && howLongUntilEventEndsMinutes > 0){

                if(isUserInList(LIST_CHECKED_IN)){
                    result = CardFeedEvent.EVENT_PHASE_IN_EVENT;
                    return result;
                }else{
                    result = CardFeedEvent.EVENT_PHASE_DAY_OF_EVENT;
                    return result;
                }

            }

            if(howLongUntilEventEndsMinutes <0){
                result = CardFeedEvent.EVENT_PHASE_REMINISCE;
                return result;
            }


        }else if(isUserNotGoing){
            result = CardFeedEvent.EVENT_PHASE_HIDE;
            return result;
        }else{
            result = CardFeedEvent.EVENT_PHASE_INTRO;
            return result;
        }

       return 0;

    }


    //----------------------------------------------------------------------------------------------//

    private boolean isEventExpired(){
        Date today = Calendar.getInstance().getTime();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);

        long todaysDayOfYear = cal1.get(Calendar.DAY_OF_YEAR);
        long eventExpireDay = mEvent.getDate_expire_day_of_year();

        long diff = eventExpireDay - todaysDayOfYear;

        return diff <= 0;

    }

    private long numberOfMillisecondsBetweenNowAndEvent(){
        Date todayDate = Calendar.getInstance().getTime();
        long todayMilliseconds = todayDate.getTime();

        long difference = mEvent.getDate_event_start() - todayMilliseconds;

        return difference;

    }

    private long numberOfMillisecondsBetweenNowAndEventEnd(){
        Date todayDate = Calendar.getInstance().getTime();
        long todayMilliseconds = todayDate.getTime();

        long difference = mEvent.getDate_event_end() - todayMilliseconds;

        return difference;
    }

    private long convertMillisecondsToReadableTime(int timeUnit, long milliseconds){
        long SECOND_IN_MILLIS = 1000;
        long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
        long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
        long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
        long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;

        long results = -1;

        switch (timeUnit){
            case TIME_MINUTES:
                results = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
                break;

            case TIME_HOURS:
                results = TimeUnit.MILLISECONDS.toHours(milliseconds);
                break;

            case TIME_DAYS:
                results = TimeUnit.MILLISECONDS.toDays(milliseconds);
                break;

            case TIME_WEEKS:
                results = milliseconds/WEEK_IN_MILLIS;
                break;

        }

        return results;
    }

    public boolean isUserInList(int listToSearch){
        switch (listToSearch){

            case LIST_GOING:
                return mEvent.getWhos_going().contains(mUserID);

            case LIST_NOT_GOING:
                return mEvent.getWhos_not_going().contains(mUserID);

            case LIST_CHECKED_IN:
                return mEvent.getWhos_checked_in().contains(mUserID);

            default:
                return false;

        }

    }

    //----------------------------------------------------------------------------------------------
    private void startLiveEvent(){
        FirestoreDatabaseFeedEvent dbevent = new FirestoreDatabaseFeedEvent(mContext,this);
        dbevent.startUpdatingEventFromFirebaseRealTime(mEvent);
    }

    //----------------------------------------------------------------------------------------------
    public void addUserToList(int list){

        FirestoreDatabaseFeedEvent dbevent = new FirestoreDatabaseFeedEvent(mContext,this);

        switch (list){
            case LIST_GOING:
                dbevent.updateEventUserGoingToEventToFirestore(mEvent);
                break;

            case LIST_NOT_GOING:
                dbevent.updateEventUserNotGoingToEventToFirestore(mEvent);
                break;

            case LIST_CHECKED_IN:
                dbevent.updateEventUserCheckedInToEventToFirestore(mEvent);
                break;
        }
    }

    public void startEventPictureUploadToCloud(){

        if(mUserUploadedEventPictureBitmap != null && mUserUploadedEventPictureUri == null){
            uploadNewPhoto(mUserUploadedEventPictureBitmap);

        }else if (mUserUploadedEventPictureBitmap == null && mUserUploadedEventPictureUri != null){
            uploadNewPhoto(mUserUploadedEventPictureUri);
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

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {
        Bitmap mBitmap;

        private BackgroundImageResize(Bitmap bitmap) {

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
            GraphicRotationUtils rotateBitmap = new GraphicRotationUtils();
            if(mBitmap == null){
                try{
                    mBitmap = rotateBitmap.handleSamplingAndRotationBitmap(mActivity,params[0]);

                }catch (IOException e){
                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());
                }

            }
            byte[] bytes = null;
            bytes = getBytesFromBitmap(mBitmap, mPhotoQuality);
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
     * Firebase Storage Methods
     */

    private void executeUploadTask(){

        fabProgressCircle.show();


        final FirestoreDatabaseFeedEvent dbevent = new FirestoreDatabaseFeedEvent(mContext,this);

        String eventID = mEvent.getEvent_id();
        String userID = FirebaseAuth.getInstance().getUid();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pictureName = userID + "_" + timeStamp;

        final StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("feed/events/" + eventID + "/pics/" + pictureName);

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
                    fabProgressCircle.hide();
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    fabProgressCircle.beginFinalAnimation();

                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "onSuccess: Upload File Saved at: " + downloadUri);
                    ArrayList<String>  photoList;

                    if(mEvent.getPictures_url() == null){
                        photoList = new ArrayList<>();
                    }else{
                        photoList = mEvent.getPictures_url();

                    }

//                    photoList.add(downloadUri.toString());
//                    mEvent.setPictures_url(photoList);
                    dbevent.updateEventUploadedEventPictureToFirestore(mEvent, downloadUri.toString());

                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    public void showPhotoGridView(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runGridView();
            }
        },100);

    }

    private void checkForGridView(){
        if(mEvent.getPictures_url() != null) {
            isEventPhotoGalleryNeeded = true;
        }
    }

    private void runGridView(){
        checkForGridView();

        if(isEventPhotoGalleryNeeded){
            mPhotoList = mEvent.getPictures_url();

            if(!isPhotoGridAdapterSetup){
                photoGridAdapter = new EventPhotoAdapter(mContext, R.layout.staff_feed_item_event_card_content_photo,mPhotoList);
                photoGridView.setAdapter(photoGridAdapter);
                isPhotoGridAdapterSetup = true;

            }else{
                photoGridAdapter.swapItems(mPhotoList);
            }

            if(!photoGridView.isShown()){
                photoGridView.setVisibility(View.VISIBLE);
                tvWarning.setVisibility(View.GONE);
            }

        }

    }

    //----------------------------------------------------------------------------------------------
    public void setupFabProgressCircle(FABProgressCircle progressCircle){
        this.fabProgressCircle = null;

        this.fabProgressCircle = progressCircle;
        fabProgressCircle.attachListener(this);

    }

}
