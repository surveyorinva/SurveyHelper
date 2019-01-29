package com.survlogic.surveyhelper.activity.staffFeed.view.message.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.photoGallery.PhotoGalleryActivity;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedItem;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedReflections;
import com.survlogic.surveyhelper.activity.staffFeed.view.message.adapter.NewMessagePhotoAdapter;
import com.survlogic.surveyhelper.database.Feed.FirestoreDatabaseFeedItem;
import com.survlogic.surveyhelper.model.AppUserClient;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.services.gnss.GnssController;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.GraphicRotationUtils;
import com.survlogic.surveyhelper.utils.HapticFeedbackUtils;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class FeedNewMessageController implements   FirestoreDatabaseFeedItem.FeedItemListener,
                                                    GnssController.PositionListener,
                                                    FABProgressListener {

    private static final String TAG = "CardFeedMessageControl";

    public interface ControllerListener{
        void finishActivity();
        void showSnackBar(String message);
        void hideKeyboard();
        void controllerSetup(boolean isSetup);
        void openPhotoSingleView(String photo);
        void openImageSelector();
        void returnRawLocation(Location location);
        void returnBestLocation(Location location);
        void pingActivityForData();
    }


    /**
     * FirestoreDatabaseFeedItem.FeedItemListener
     */

    @Override
    public void fetchFeedItemsAll(ArrayList<FeedItem> itemList) {

    }

    @Override
    public void fetchFeedItemsNoneFound() {

    }

    @Override
    public void fetchFeedItemsGetError(boolean isError) {

    }

    @Override
    public void addNewFeedItemSuccess(String feedItem_id) {
        mFeedMessage.setItem_id(feedItem_id);

        if(!isFeedListenerLiveOn){
            startLiveFeedListener();
            isFeedListenerLiveOn = true;
        }

        if(btSaveFeedMessage.getVisibility() != View.VISIBLE){
            btSaveFeedMessage.setVisibility(View.VISIBLE);
        }

        if(isFeedMessageSaved){
            //finish Activity
            finishMessageAndExit();

        }

    }

    @Override
    public void addNewFeedItemFailure(boolean isError) {
        Log.d(TAG, "to_delete: Error ");
    }


    @Override
    public void updateNewFeedItemSuccess() {
        Log.d(TAG, "to_delete: updateNewFeedItemSuccess: ");
        finishMessageAndExit();
    }

    @Override
    public void updateNewFeedItemFailure(boolean isError) {
        Log.d(TAG, "to_delete: Error ");
    }

    @Override
    public void fetchFeedItemLive(FeedItem feedItem) {
        this.mFeedMessage = feedItem;

        checkForGridView();

        if(isGridViewSetup && isPhotoGalleryNeeded){
            showPhotoGridView();
        }

    }

    @Override
    public void fetchFeedItemLiveGetError(boolean isError) {
        Log.d(TAG, "to_delete: Error ");
    }

    @Override
    public void itemPhotoAddedSuccessfully() {

    }

    @Override
    public void itemPhotoAddedGetError(boolean isError) {

    }

    /**
     * GnssController.PositionListener
     */
    @Override
    public void refreshGnss() {

    }

    @Override
    public void controllerGnssSetup(boolean isSetup) {
        isWorkerControllerGnssSetup = true;
    }

    @Override
    public void returnLiveRawPosition(Location rawLocation) {
        this.mGnssRawLocation = rawLocation;

        mGnssRawCount++;

        switch (mGnssRawCount){
            case 1:
                if(!mIsRawLocationAvailable){
                    mIsRawLocationAvailable = true;
                }
                break;

            case 2:
                mListenerToActivity.returnRawLocation(rawLocation);

            case 3:
                showMapView();
                break;

            default:
                mListenerToActivity.returnRawLocation(rawLocation);
                break;
        }

    }

    @Override
    public void returnBestPosition(Location bestPosition) {
        this.mGnssBestLocation = bestPosition;
        mListenerToActivity.returnBestLocation(bestPosition);

        if(btSaveFeedMessage.getVisibility() != View.VISIBLE){
            btSaveFeedMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void returnGnssError(boolean isError) {
        //Todo
    }


    /**
     * FABProgressListener
     */
    @Override
    public void onFABProgressAnimationEnd() {

    }

    private Context mContext;
    private Activity mActivity;


    private FeedNewMessageController.ControllerListener mListenerToActivity;

    private boolean isAllRequiredControllersReadyToGo = false;

    //FeedItem
    private FirestoreUser mFirestoreUser;
    private FeedItem mFeedMessage;
    private boolean isFeedMessageSet = false, isFeedMessageSavedForPlaceHolder = false, isFeedMessageSaved = false;
    private boolean isFeedListenerLiveOn = false;

    private int mTypeOfMessage = 0;
    private String mRoomToPostMessage;

    private Button btSaveFeedMessage;

    //Photo - Local
    private ArrayList<String> mPhotoList;

    private Bitmap mUserUploadedPictureBitmap;
    private Uri mUserUploadedPictureUri;
    private byte[] mUploadBytes;
    private double mProgressUpload = 0;

    private int mPhotoQuality = 90;

    private GridView photoGridView;
    private NewMessagePhotoAdapter photoGridAdapter;
    private boolean isGridViewSetup = false, isPhotoGridAdapterSetup = false, isPhotoGalleryNeeded = false;
    private TextView tvWarning;

    //GnssController
    private GnssController mGnssController;
    private boolean isWorkerControllerGnssInit = false, isWorkerControllerGnssSetup = false;
    private boolean isGnssRunning = false;

    private boolean mIsRawLocationAvailable = false;
    private boolean isUserLocationSet = false;
    private Location mGnssBestLocation, mGnssRawLocation, mLocationUser;
    private int mGnssRawCount = 0;

    private ConstraintLayout mViewMapRoot;
    private FABProgressCircle mFabMapProgress, mFabPhotoProgress;
    private FloatingActionButton mFabMap, mFabPhoto;

    public FeedNewMessageController(Context context, ControllerListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mListenerToActivity = listener;

        createNewFeedItem();

    }

    public void setLocationView(ConstraintLayout layout){
        this.mViewMapRoot = layout;
    }

    public void setFabPhoto(final FloatingActionButton fab, FABProgressCircle progressCircle){
        this.mFabPhoto = fab;
        this.mFabPhotoProgress = progressCircle;

        mFabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabPhotoProgress.show();
                HapticFeedbackUtils.init(mActivity);
                HapticFeedbackUtils.once(50);

                mListenerToActivity.pingActivityForData();

                mListenerToActivity.openImageSelector();

                if(!isFeedMessageSavedForPlaceHolder){
                    //Save Feed and start listening for changes
                    saveFeedItemInFirestore(true);
                }
            }
        });

    }


    public void setFabMap(final FloatingActionButton fabMap, FABProgressCircle progressCircle) {
        this.mFabMap = fabMap;
        this.mFabMapProgress = progressCircle;

        mFabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListenerToActivity.pingActivityForData();

                if(!isGnssRunning){
                    mFabMapProgress.show();
                    mGnssController.obtainBestPosition(2);
                }
            }
        });

    }

    public void callFabMapFinish(){
        mFabMapProgress.beginFinalAnimation();
    }

    public void setSaveButton(Button view){
        this.btSaveFeedMessage = view;

        btSaveFeedMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFeedMessageSavedForPlaceHolder){
                    //Save Feed and start listening for changes
                    saveFeedItemInFirestore(false);
                }else{
                    updateFeedItem();
                }
            }
        });

    }

    public void setTypeOfMessage(int typeOfMessage) {
        this.mTypeOfMessage = typeOfMessage;
    }

    public void setRoomToPostMessage(String roomToPostMessage) {
        this.mRoomToPostMessage = roomToPostMessage;
    }

    public void setMessage(String message){
        String fullMessage = getExtrasFromMessageType(message);
        mFeedMessage.setExtra_entry(fullMessage);
    }

    public void showSaveButton(boolean showButton){

        if(showButton){
            if(btSaveFeedMessage.getVisibility() != View.VISIBLE){
                btSaveFeedMessage.setVisibility(View.VISIBLE);
            }
        }else{
            if(btSaveFeedMessage.getVisibility() == View.VISIBLE){
                btSaveFeedMessage.setVisibility(View.GONE);
            }
        }

    }

    public void onStart(){
        setupRequiredChildControllers();
    }

    public void onPause(){
        mGnssController.onStop();
    }

    //_---------------------------------------------------------------------------------------------
    private void setupRequiredChildControllers(){
        if(!isWorkerControllerGnssInit){
            mGnssController = new GnssController(mContext,this);
            mGnssController.onStart();
            isWorkerControllerGnssInit = true;
        }
        mListenerToActivity.controllerSetup(true);

    }

    //----------------------------------------------------------------------------------------------
    private void createNewFeedItem(){
        mFirestoreUser = ((AppUserClient) (mActivity.getApplicationContext())).getUser();

        if(!isFeedMessageSet){
            mFeedMessage = new FeedItem();
            isFeedMessageSet = true;
        }

    }

    private void saveFeedItemInFirestore(boolean saveItemPlace){
        //Internal parts of Feed to save - user Info
            mFeedMessage.setUser_id(mFirestoreUser.getUser_id());
            mFeedMessage.setDisplay_name(mFirestoreUser.getDisplay_name());
            mFeedMessage.setUser_profile_pic_url(mFirestoreUser.getProfile_pic_url());

        //External parts of Feed to save - user entry
            mFeedMessage.setFeed_post_type(mTypeOfMessage);
            mFeedMessage.setRoom_id(mRoomToPostMessage);

        if(saveItemPlace){
            isFeedMessageSavedForPlaceHolder = true;
        }else{
            isFeedMessageSavedForPlaceHolder = true;
            isFeedMessageSaved = true;
            mFeedMessage.setPublished(true);
        }

        //Save item Initially
        FirestoreDatabaseFeedItem dbItem = new FirestoreDatabaseFeedItem(mContext,this);
        dbItem.addNewMessage(mFeedMessage);

    }

    private void updateFeedItem(){
        isFeedMessageSavedForPlaceHolder = true;
        isFeedMessageSaved = true;
        mFeedMessage.setPublished(true);

        FirestoreDatabaseFeedItem dbItem = new FirestoreDatabaseFeedItem(mContext,this);
        dbItem.updateMessage(mFeedMessage);


    }

    public boolean isFeedItemSaved(){
        return isFeedMessageSavedForPlaceHolder;
    }

    public void deleteFeedItem(){
        FirestoreDatabaseFeedItem dbItem = new FirestoreDatabaseFeedItem(mContext,this);
        dbItem.deleteMessage(mFeedMessage);
    }

    private void startLiveFeedListener(){
        FirestoreDatabaseFeedItem dbItem = new FirestoreDatabaseFeedItem(mContext,this);
        dbItem.startUpdatingFeedItemFromFirebaseRealTime(mFeedMessage);
    }

    //----------------------------------------------------------------------------------------------
    private String getExtrasFromMessageType(String user_entry_message){

        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        FeedReflections reflection;

        String reflection_question;
        String returnValue;

        switch (mTypeOfMessage){

            case 201:  // FEED_MESSAGE_TYPE_REFLECTION_MORNING
                reflection = preferenceLoader.getFeedReflectionDailyMorningLocal();
                reflection_question = reflection.getSummary();
                returnValue = mActivity.getResources().getString(R.string.staff_feed_message_prefix_reflection_morning_fmt,
                        reflection_question,
                        user_entry_message);
                break;

            case 202:   //FEED_MESSAGE_TYPE_REFLECTION_EVENING
                reflection = preferenceLoader.getFeedReflectionDailyEveningLocal();
                reflection_question = reflection.getSummary();
                returnValue = mActivity.getResources().getString(R.string.staff_feed_message_prefix_reflection_evening_fmt,
                        reflection_question,
                        user_entry_message);
                break;

            case 203:   //FEED_MESSAGE_TYPE_REFLECTION_WEEKLY
                reflection = preferenceLoader.getFeedReflectionWeeklyLocal();
                reflection_question = reflection.getSummary();
                returnValue = mActivity.getResources().getString(R.string.staff_feed_message_prefix_reflection_weekly_fmt,
                        reflection_question,
                        user_entry_message);
                break;

            default:
                returnValue = user_entry_message;
                break;

        }

        return returnValue;

    }

    private void finishMessageAndExit(){
        setExtrasFromMessageType();  //This sets the local preferences that user has completed a feed item (if needed)
        giveUserAReward(); //Gives the current user a reward for adding a feed item

        mListenerToActivity.finishActivity();
    }

    private void setExtrasFromMessageType(){
        Log.d(TAG, "to_delete: setExtrasFromMessageType: " + mTypeOfMessage);

        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        FeedReflections reflection;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        switch (mTypeOfMessage){

            case 201:  // FEED_MESSAGE_TYPE_REFLECTION_MORNING
                reflection = preferenceLoader.getFeedReflectionDailyMorningLocal();
                reflection.setComplete(true);

                reflection.setCompletedOn(calendar.getTimeInMillis());

                preferenceLoader.setFeedReflection(PreferenceLoader.REFLECTION_MORNING,reflection,true);
                break;

            case 202:   //FEED_MESSAGE_TYPE_REFLECTION_EVENING
                Log.d(TAG, "to_delete: setExtrasFromMessageType: setting reflection");

                reflection = preferenceLoader.getFeedReflectionDailyEveningLocal();
                reflection.setComplete(true);

                reflection.setCompletedOn(calendar.getTimeInMillis());

                preferenceLoader.setFeedReflection(PreferenceLoader.REFLECTION_EVENING,reflection,true);

                break;

            case 203:   //FEED_MESSAGE_TYPE_REFLECTION_WEEKLY
                reflection = preferenceLoader.getFeedReflectionWeeklyLocal();
                reflection.setComplete(true);

                reflection.setCompletedOn(calendar.getTimeInMillis());

                preferenceLoader.setFeedReflection(PreferenceLoader.REFLECTION_WEEKLY,reflection,true);
                break;

            default:
                Log.d(TAG, "to_delete: setExtrasFromMessageType: Error");
                break;

        }
    }

    private void giveUserAReward(){
        String message = mActivity.getResources().getString(R.string.app_reward_user_earned_points_fmt,
                "5");

        mListenerToActivity.showSnackBar(message);

    }

    //----------------------------------------------------------------------------------------------//

    public void setUserUploadedPictureBitmap(Bitmap bitmap) {
        this.mUserUploadedPictureBitmap = bitmap;
    }

    public void setUserUploadedPictureUri(Uri uri) {
        this.mUserUploadedPictureUri = uri;
    }

    public void startPictureUploadLocal(){
        if(mUserUploadedPictureBitmap != null && mUserUploadedPictureUri == null){
            uploadNewPhoto(mUserUploadedPictureBitmap);

        }else if (mUserUploadedPictureBitmap == null && mUserUploadedPictureUri != null){
            uploadNewPhoto(mUserUploadedPictureUri);
        }
    }

    public int getPhotoQuality() {
        return mPhotoQuality;
    }

    public void setPhotoQuality(int photoQuality) {
        this.mPhotoQuality = photoQuality;
    }

    public void setGridView(GridView gridView, TextView tvWarning){
        this.photoGridView = gridView;
        this.tvWarning = tvWarning;

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
                    mListenerToActivity.openPhotoSingleView(photoURL);
                }

            }
        });

        photoGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DialogUtils.showAlertDialog(mContext,mActivity.getResources().getString(R.string.staff_feed_event_todo_message_delete_photo_title),mActivity.getResources().getString(R.string.staff_feed_add_new_message_todo_message_delete_photo_summary));
                return true;
            }
        });

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
        mFabPhotoProgress.show();

        final FirestoreDatabaseFeedItem dbItem = new FirestoreDatabaseFeedItem(mContext,this);

        //Upload Photo to feed item
        String itemID = mFeedMessage.getItem_id();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pictureName = itemID + "_" + timeStamp;

        final StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("feed/messages/pics/" + pictureName);

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
                    mFabPhotoProgress.hide();
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    mFabPhotoProgress.beginFinalAnimation();

                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "onSuccess: Upload File Saved at: " + downloadUri);
                    ArrayList<String>  photoList;

                    if(mFeedMessage.getPhoto_link() == null){
                        photoList = new ArrayList<>();
                    }else{
                        photoList = mFeedMessage.getPhoto_link();

                    }

                    dbItem.updateFeedItemUploadedItemPictureToFirestore(mFeedMessage, downloadUri.toString());

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
        if(mFeedMessage.getPhoto_link() != null) {
            isPhotoGalleryNeeded = true;
        }
    }

    private void runGridView(){
        checkForGridView();

        if(isPhotoGalleryNeeded){
            mPhotoList = mFeedMessage.getPhoto_link();

            if(!isPhotoGridAdapterSetup){
                photoGridAdapter = new NewMessagePhotoAdapter(mContext, R.layout.staff_feed_item_event_card_content_photo,mPhotoList);
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

    private void showMapView(){
        if(mViewMapRoot.getVisibility() != View.VISIBLE){
            mViewMapRoot.setVisibility(View.VISIBLE);
        }

    }

    public void setUserLocation(Location location){
        this.mLocationUser = location;

        HashMap<String,Double> locationForMessage = new HashMap<>();

        locationForMessage.put(mActivity.getResources().getString(R.string.HASH_KEY_LAT),location.getLatitude());
        locationForMessage.put(mActivity.getResources().getString(R.string.HASH_KEY_LON),location.getLongitude());

        mFeedMessage.setLocation(locationForMessage);

        isUserLocationSet = true;

    }


}
