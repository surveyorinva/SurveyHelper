package com.survlogic.surveyhelper.services.gnss;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GnssController {
    private static final String TAG = "GnssController";

    public void setupController(){
        bindLocationService();
    }

    public void onStart(){
        registerLocationReceiver();
    }

    public void onStop(){
        unregisterLocationReceiver();
    }

    public void obtainBestPosition(int delayInSeconds){
        long delayInMilli = delayInSeconds * 1000;

        if(!areReceiveresRegistered){
            onStart();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(hasFilteredLocation){
                    Log.d(TAG, "to_delete: Gnss has Filtered Location ");
                    mListenerToGeneralInterface.returnBestPosition(mCurrentLocationPredicted);
                }else if(hasLocation){
                    Log.d(TAG, "to_delete: Gnss has raw Location");
                    mListenerToGeneralInterface.returnBestPosition(mCurrentLocationRaw);
                }else{
                    //no GPS Position, call network loader
                    Log.d(TAG, "to_delete: No Location ");
                    mListenerToGeneralInterface.returnGnssError(true);
                }
            }
        },delayInMilli);

    }

    //----------------------------------------------------------------------------------------------//
    public interface PositionListener {
        void refreshGnss();
        void controllerGnssSetup(boolean isSetup);
        void returnLiveRawPosition(Location rawLocation);
        void returnBestPosition(Location bestPosition);
        void returnGnssError(boolean isError);
    }

    public interface GnssMetadataListener {
        void returnGnssMetaData();
    }

    private Context mContext;
    private Activity mActivity;

    private PositionListener mListenerToGeneralInterface;

    private boolean areReceiveresRegistered = false;

    //Location Service
    private GnssService locationService;
    private BroadcastReceiver rawLocationReceiver;
    private BroadcastReceiver predictedLocationReceiver;
    private BroadcastReceiver locationMetadataReceiver;
    private BroadcastReceiver gnssStatusReceiver;

    //Location Variables
    private boolean hasLocation = false, hasFilteredLocation = false;
    private int gpsRawCount=0, gpsFilteredCount =0, networkCount = 0;

    //System Variables
    private int criteriaGpsFilteredCount = 1, criteriaGpsRawCount = 1;

    //Output Variables;
    private Location mCurrentLocationPredicted, mCurrentLocationRaw, mCurrentLocationNetwork;

    public GnssController(Context context, PositionListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mListenerToGeneralInterface = listener;

        setupController();
    }

    private void bindLocationService(){
        final Intent locationService = new Intent(mActivity.getApplication(),GnssService.class);
        mActivity.getApplication().startService(locationService);
        mActivity.getApplication().bindService(locationService, locationServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();

            if (name.endsWith("GnssService")) {
                locationService = ((GnssService.LocationServiceBinder) service).getService();
                locationService.startGPS();
                gpsRawCount = 0;
                gpsFilteredCount = 0;

                mListenerToGeneralInterface.controllerGnssSetup(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("GnssService")) {
                locationService.stopGPS();
                locationService = null;
            }
        }
    };

    private void registerLocationReceiver() {
        rawLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location rawLocation = intent.getParcelableExtra("location");

                gpsRawCount++;

                if (!hasLocation) {
                    if (gpsRawCount > criteriaGpsRawCount) {
                        hasLocation = true;
                    }
                } else {
                    if(!locationService.getGpsLogging()){
                        locationService.startLogging();
                    }

                    mCurrentLocationRaw = rawLocation;
                    mListenerToGeneralInterface.returnLiveRawPosition(rawLocation);
                }

            }
        };

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(
                rawLocationReceiver,
                new IntentFilter("LocationUpdated")
        );

        //------------------------------------------------------------------------------------------

        predictedLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location predicatedLocation = intent.getParcelableExtra("location");

                gpsFilteredCount++;

                if (!hasFilteredLocation) {
                    if (gpsFilteredCount > criteriaGpsFilteredCount) {
                        hasFilteredLocation = true;
                    }
                } else {
                    mCurrentLocationPredicted = predicatedLocation;

                }

            }
        };

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(
                predictedLocationReceiver,
                new IntentFilter("PredictLocation")
        );


        //------------------------------------------------------------------------------------------

        gnssStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int mSvCount = 0;
                int mUsedInFixCount = 0;

                mSvCount = intent.getIntExtra("svCount",mSvCount);
                mUsedInFixCount = intent.getIntExtra("usedInFixCount",mUsedInFixCount);


            }
        };

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(
                gnssStatusReceiver,
                new IntentFilter("GnssStatus")
        );

        locationMetadataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                float mAzimuth = 0;
                float mSpeed = 0;

                mAzimuth = intent.getFloatExtra("azimuth",mAzimuth);
                mSpeed = intent.getFloatExtra("speed",mSpeed);


            }
        };

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(
                locationMetadataReceiver,
                new IntentFilter("GPSLocationMetadata")
        );

        areReceiveresRegistered = true;
    }

    private void unregisterLocationReceiver(){
        Log.d(TAG, "to_delete: Unregister Location Receivers ");
        try{
            if(rawLocationReceiver !=null){
                Log.d(TAG, "to_delete: rawLocationReceiver ");
                mActivity.unregisterReceiver(rawLocationReceiver);

            }

            if (predictedLocationReceiver != null) {
                Log.d(TAG, "to_delete: predictedLocationReceiver ");
                mActivity.unregisterReceiver(predictedLocationReceiver);
            }

            if(gnssStatusReceiver !=null){
                Log.d(TAG, "to_delete: gnssStatusReceiver ");
                mActivity.unregisterReceiver(gnssStatusReceiver);
            }

            if(locationMetadataReceiver !=null){
                Log.d(TAG, "to_delete: locationMetadataReceiver ");
                mActivity.unregisterReceiver(locationMetadataReceiver);
            }

            areReceiveresRegistered = false;

        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }

    }


}
