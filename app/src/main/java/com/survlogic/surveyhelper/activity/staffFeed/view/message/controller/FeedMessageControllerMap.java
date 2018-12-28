package com.survlogic.surveyhelper.activity.staffFeed.view.message.controller;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FeedMessageControllerMap {

    private static final String TAG = "CardFeedMessageControll";

    public interface ControllerListener{
        void refreshMap();
        SupportMapFragment returnMapFragmentView();
        Location returnMapPinLocation();
        void controllerMapSetup(boolean isSetup);

    }

    /**
     * OnMapReadyCallback
     */

    private Context mContext;
    private Activity mActivity;

    private  ControllerListener mListenerToMainController;

    //    Mapping Variables
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private int mapType = 1;


    public FeedMessageControllerMap(Context context, FeedMessageControllerMap.ControllerListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mListenerToMainController = listener;

        setupController();
    }

    private void setupController(){
        if(mMapFragment == null){
            mMapFragment = mListenerToMainController.returnMapFragmentView();
            mMapFragment.getView().setVisibility(View.INVISIBLE);

        }
        mListenerToMainController.controllerMapSetup(true);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mUiSettings = googleMap.getUiSettings();
        setMapOptions(googleMap, mUiSettings);

    }

    public void mapLocation(){
        addMarkers();
    }


    private void setMapOptions(GoogleMap map, UiSettings settings) {
        switch (mapType) {

            case 1:  //Normal
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case 4:  //Hybrid
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            case 2: //Satellite
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case 3:  //Terrain
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

        }
        settings.setMapToolbarEnabled(false);
        settings.setZoomControlsEnabled(false);
        settings.setMyLocationButtonEnabled(false);
        settings.setCompassEnabled(false);
    }

    private void addMarkers(){
        Location pinLocation = mListenerToMainController.returnMapPinLocation();
        LatLng latlng = new LatLng(pinLocation.getLatitude(), pinLocation.getLongitude());

        if (mMap != null) {
            Marker targetMarker = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title("Target")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));

        }else{
            Log.d(TAG, "to_delete: Map is Null");
        }
        mMapFragment.getView().setVisibility(View.VISIBLE);

    }

}
