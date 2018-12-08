package com.survlogic.surveyhelper.messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staff.StaffActivity;

import java.util.Map;
import java.util.Random;


public class AppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "AppFirebaseMessagingSer";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }
        }

        if(remoteMessage.getData().isEmpty()){
            if(remoteMessage.getNotification() != null){
                sendNotification(remoteMessage.getNotification().getBody());
            }

        }else{
            sendNotification(remoteMessage.getData());
        }



    }


    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);

    }


    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(String messageBody) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = getString(R.string.default_notification_channel_id);

        Intent intent = new Intent(this, StaffActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_announcement_dark_24dp)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[]{0,1000,500,1000})
                        .setContentIntent(pendingIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = setupChannels(channelId);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

    private void sendNotification(Map<String, String> data) {

        String messageTitle = data.get("title").toString();
        String messageBody = data.get("body").toString();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = getString(R.string.default_notification_channel_id);

        Intent intent = new Intent(this, StaffActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_announcement_dark_24dp)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = setupChannels(channelId);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel setupChannels(String channel_id){
        CharSequence adminChannelName = getString(R.string.default_notification_channel_name);
        String adminChannelDescription = getString(R.string.default_notification_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(channel_id, adminChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.setVibrationPattern(new long[]{0,1000,500,1000});
        adminChannel.setShowBadge(true);

        return adminChannel;
    }


}
