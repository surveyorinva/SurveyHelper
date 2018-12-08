package com.survlogic.surveyhelper.activity.staff.controller;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appSettings.SettingsActivity;
import com.survlogic.surveyhelper.activity.staff.workers.CurrentUserFirestoreWorker;
import com.survlogic.surveyhelper.model.AppSettings;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.GraphicUtils;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

public class StaffController implements CurrentUserFirestoreWorker.CurrentUserWorkerListener {
    private static final String TAG = "StaffFeedActController";

    public interface StaffControllerListener {

        void refreshUI();
        void returnCurrentUser(FirestoreUser currentUser);
        void returnErrorNoUserLoggedIn();
    }

    /**
     * CurrentUserWorkerListener
     */

    @Override
    public void sendMeCurrentFirestoreUser(FirestoreUser currentUser) {
        mFirestoreUser = currentUser;
        mActivityListener.returnCurrentUser(currentUser);

        showCurrentUserInformationInActivity();
        initMessageService();

    }

    @Override
    public void returnErrorNoUserLoggedIn() {
        //Todo Log person out and return to splash screen
    }


    private Context mContext;
    private Activity mActivity;
    private StaffControllerListener mActivityListener;

    private FirestoreUser mFirestoreUser;

    private PreferenceLoader preferenceLoader;
    private AppSettings settings;

    private CurrentUserFirestoreWorker currentUserWorker;
    private boolean isCurrentUserWorkerSetup = false;

    private FloatingActionButton fabActionAnnouncement;
    private boolean isPopupWindowShown = false;


    public StaffController(Context context, StaffControllerListener listener) {
        Log.d(TAG, "Staff Feed Controler Started----------------------------------------------->");
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mActivityListener = listener;

        initController();
        initNavigationView();
        initViewWidgets();

    }

    private void initController(){
        loadPreferences();
        initWorkers();
    }


    private void initNavigationView(){
        ImageButton ibNavigateToSettings = mActivity.findViewById(R.id.btn_feed_navigation_settings);
        ibNavigateToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, SettingsActivity.class);
                i.putExtra(mActivity.getResources().getString(R.string.KEY_PARENT_ACTIVITY),mActivity.getString(R.string.CLASS_STAFF_FEED));
                mActivity.startActivity(i);
                mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

    }

    private void initViewWidgets(){



    }

    private void loadPreferences(){
        preferenceLoader = new PreferenceLoader(mContext);
        settings = preferenceLoader.getSettings();

    }

    private void initWorkers(){
        if(!isCurrentUserWorkerSetup){
            currentUserWorker = new CurrentUserFirestoreWorker(mContext,this);
            isCurrentUserWorkerSetup = currentUserWorker.onStart();
        }


    }

    private void showCurrentUserInformationInActivity(){
        TextView tvUserName = mActivity.findViewById(R.id.profile_current_user_display_name);
        tvUserName.setText(mActivity.getResources().getString(R.string.firebase_welcome_fmt,mFirestoreUser.getDisplay_name()));

        String profilePicURL = mFirestoreUser.getProfile_pic_url();

        ImageView ivUserProfilePicture = mActivity.findViewById(R.id.profile_current_user_image);
        final ProgressBar pbUserProfilePictureProgress = mActivity.findViewById(R.id.profile_current_user_image_progress);

        if(profilePicURL == null){
            Bitmap bitmap = GraphicUtils.drawableToBitmap(ContextCompat.getDrawable(mActivity, R.drawable.ic_person_outline_dark_24dp));

            ivUserProfilePicture.setImageBitmap(bitmap);
            pbUserProfilePictureProgress.setVisibility(View.INVISIBLE);

        }else{
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(profilePicURL, ivUserProfilePicture, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    if(pbUserProfilePictureProgress !=null){
                        pbUserProfilePictureProgress.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    if(pbUserProfilePictureProgress !=null){
                        pbUserProfilePictureProgress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if(pbUserProfilePictureProgress !=null){
                        pbUserProfilePictureProgress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    if(pbUserProfilePictureProgress !=null){
                        pbUserProfilePictureProgress.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    public void initMessageService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId  = mActivity.getResources().getString(R.string.default_notification_channel_id);
            String channelName = mActivity.getResources().getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    mActivity.getSystemService(NotificationManager.class);

            NotificationChannel notificationChannelAlpha = new NotificationChannel(channelId,
                    channelName,NotificationManager.IMPORTANCE_HIGH);


            notificationChannelAlpha.setDescription(mActivity.getResources().getString(R.string.default_notification_channel_description));
            notificationChannelAlpha.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannelAlpha.setShowBadge(true);

            notificationManager.createNotificationChannel(notificationChannelAlpha);

        }

        if (mActivity.getIntent().getExtras() != null) {
            for (String key : mActivity.getIntent().getExtras().keySet()) {
                Object value = mActivity.getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        startMessageService();
    }


    private void startMessageService(){
        FirebaseMessaging.getInstance().subscribeToTopic("christopher")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Log.d(TAG, "to_delete: Message Subscription Failed");
                            //Todo
                        }
                    }
                });

        detectDeviceTokenId();
    }

    private void detectDeviceTokenId(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            //Todo
                        }
                        String token = task.getResult().getToken();

                        preferenceLoader = new PreferenceLoader(mContext);
                        String savedToken = preferenceLoader.getUserFirebaseToken();

                        if(savedToken !=null){
                            if(!savedToken.equals(token)){
                                //token not the same, resave new token
                                saveUserToken(token);
                            }
                        }else{
                            //token has not been saved, save now
                            saveUserToken(token);
                        }

                    }
                });
    }

    private void saveUserToken(String token){
        currentUserWorker.onMessageTokenChange(token);
        preferenceLoader.setUserFirebaseToken(token,true);
    }




}
