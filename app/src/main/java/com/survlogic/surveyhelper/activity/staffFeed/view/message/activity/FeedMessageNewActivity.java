package com.survlogic.surveyhelper.activity.staffFeed.view.message.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.photoGallery.dialog.ZoomablePhotoDialog;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedReflections;
import com.survlogic.surveyhelper.activity.staffFeed.view.message.controller.FeedNewMessageController;
import com.survlogic.surveyhelper.dialog.SelectPhotoDialogFromActivity;
import com.survlogic.surveyhelper.model.AppUserClient;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.services.maps.ClusterMarkerItem;
import com.survlogic.surveyhelper.services.maps.ClusterMarkerRenderer;
import com.survlogic.surveyhelper.utils.BaseActivity;
import com.survlogic.surveyhelper.utils.KeyboardUtils;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class FeedMessageNewActivity extends BaseActivity implements FeedNewMessageController.ControllerListener,
                                                                    SelectPhotoDialogFromActivity.OnPhotoSelectedListener,
                                                                    ZoomablePhotoDialog.DialogListener,
                                                                    OnMapReadyCallback {

    private static final String TAG = "CardFeedMessageNewActiv";


    /**
     * FeedNewMessageController.GeneralListener
     */

    @Override
    public void finishActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent data = new Intent();
                String text = "Result to be returned....";
                //---set the data to pass back---
                data.setData(Uri.parse(text));
                setResult(RESULT_OK, data);

                finish();
            }
        },delay_before_finish);

    }

    @Override
    public void showSnackBar(String message) {
        Snackbar.make(clRoot, message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void hideKeyboard() {
        KeyboardUtils.hideKeyboard(etUserEntry);
    }

    @Override
    public void controllerSetup(boolean isSetup) {
        if (isSetup) {
            isActivityControllerSetup = true;
        }
    }

    @Override
    public void openPhotoSingleView(String photo) {
        createDialogPhotoView(photo);
    }

    @Override
    public void openImageSelector() {
        requestImageDialogBox();
    }


    @Override
    public void returnRawLocation(Location location) {
        this.mLocationRaw = location;

        if(isMapSet){
            setCameraViewSelf();
        }

    }

    @Override
    public void returnBestLocation(Location location) {
        this.mLocationBest = location;
        this.isBestPositionAvailable = true;

        if(isMapSet){
            addMapMarkerSelf(true);
        }
    }

    @Override
    public void pingActivityForData() {

        String message = etUserEntry.getText().toString();
        checkAndSetMessage(message);

    }

    /**
     * SelectPhotoDialog.OnPhotoSelectedListener
     */

    @Override
    public void returnImagePath(Uri imagePath) {
        setSingleImageUri(imagePath);
    }

    @Override
    public void returnImageThumbnail(Bitmap bitmap) {

    }

    @Override
    public void returnImageFull(Bitmap bitmap) {
        setSingleImageBitmap(bitmap);
    }

    @Override
    public void returnImageFullError(boolean isError) {

    }

    @Override
    public Context getContextFromParent() {
        return mContext;
    }

    /**
     * ZoomablePhotoDialog.DialogListener
     */

    @Override
    public void isPopupOpen(boolean isOpen) {

    }

    private Context mContext;
    private Activity mActivity;

    private CoordinatorLayout clRoot;

    private FeedReflections mFeedRelection;
    private String mActivityTitle;

    private FeedNewMessageController mWorkController;
    private boolean isActivityControllerSetup = false;

    private int mMessageClass = 0;
    private static final int CLASS_REFLECTION = 1, CLASS_ACTION = 2;

    private String mMessageString;
    private String mRoomToPostMessage;
    private int mTypeOfMessage = 0;

    private Bundle mapViewBundle = null;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private boolean isMapInit = false, isMapSet = false;
    private boolean isBestPositionAvailable = false;
    private boolean isLocationLocked = false;

    private LatLngBounds mMapBoundaryFence;
    private Location mLocationRaw, mLocationBest;
    private Location mLocationLocked;

    private ClusterManager<ClusterMarkerItem> mClusterManager;
    private ClusterMarkerRenderer mClusterManagerRenderer;

    private  ExtendedEditText etUserEntry;
    private long typing_delay = 1000, delay_before_finish = 1000;
    private long last_text_edit = 0;
    private Handler handler = new Handler();


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_feed_item_message_new_activity);

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mContext = FeedMessageNewActivity.this;
        mActivity = (Activity) mContext;

        initMapView();

        getIntentDelivery();

        initControllers();
        initViewWidgets();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWorkController.onStart();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWorkController.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void getIntentDelivery(){

        mMessageClass = getIntent().getIntExtra(getString(R.string.KEY_MESSAGE_CLASS),0);

        switch (mMessageClass){
            case CLASS_REFLECTION:
                mActivityTitle = getIntent().getStringExtra(getString(R.string.KEY_EVENT_HEADER_TITLE));
                mFeedRelection = getIntent().getParcelableExtra(getString(R.string.KEY_FEED_PARCEL));
                mTypeOfMessage = getIntent().getIntExtra(getString(R.string.KEY_MESSAGE_TYPE_ID),0);
                mRoomToPostMessage = getIntent().getStringExtra(getString(R.string.KEY_MESSAGE_ROOM));

                break;

            case CLASS_ACTION:
                mRoomToPostMessage = getIntent().getStringExtra(getString(R.string.KEY_MESSAGE_ROOM));
                mTypeOfMessage = getIntent().getIntExtra(getString(R.string.KEY_MESSAGE_TYPE_ID),0);
                mActivityTitle = null;

                break;

            default:
                break;

        }


    }

    private void initControllers(){
        if(!isActivityControllerSetup){
            mWorkController = new FeedNewMessageController(mContext,this);

            mWorkController.setTypeOfMessage(mTypeOfMessage);
            mWorkController.setRoomToPostMessage(mRoomToPostMessage);

        }
    }

    private void initViewWidgets(){
        clRoot = findViewById(R.id.new_message_root_layout);

        Button ibAppBarSave = findViewById(R.id.appBar_top_action_finish);
        mWorkController.setSaveButton(ibAppBarSave);

        ImageButton ibAppBarBack = findViewById(R.id.appBar_top_action_nav_back);
        ibAppBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWorkController.isFeedItemSaved()){
                    mWorkController.deleteFeedItem();
                }
                finish();
            }
        });
        ibAppBarBack.setVisibility(View.VISIBLE);

        TextView tvAppBarTitle = findViewById(R.id.appBar_top_title);
        tvAppBarTitle.setText(getResources().getString(R.string.staff_feed_message_add_new_message_title));

        TextView tvMessageTitle = findViewById(R.id.message_type_title);
        tvMessageTitle.setText(mActivityTitle);

        GridView gvPhotoGrid = findViewById(R.id.photo_gallery_view_feed_item_new);
        TextView tvPhotoWarning = findViewById(R.id.photo_view_empty_message_feed_item_new);
        mWorkController.setGridView(gvPhotoGrid, tvPhotoWarning);

        etUserEntry = findViewById(R.id.message_entry_value);

        etUserEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String value = v.getText().toString();

                    checkAndSetMessage(value);

                    return true;
                }
                return false;
            }

        });

        etUserEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    EditText tv = (EditText) v;
                    String value = tv.getText().toString();

                    checkAndSetMessage(value);
                }
            }
        });

        etUserEntry.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               handler.removeCallbacks(etInputMessageTypingChecker);
           }

           @Override
           public void afterTextChanged(Editable s) {
               if (s.length() > 0) {
                   last_text_edit = System.currentTimeMillis();
                   handler.postDelayed(etInputMessageTypingChecker, typing_delay);
               }else{
                   mWorkController.showSaveButton(false);
               }
           }
       });

        FloatingActionButton fabPhotoAdd = findViewById(R.id.fab_photo);
        FABProgressCircle fabPhotoProgress = findViewById(R.id.fab_photo_container);
        mWorkController.setFabPhoto(fabPhotoAdd,fabPhotoProgress);

        ConstraintLayout clMapRoot = findViewById(R.id.cl_map_root);

        mWorkController.setLocationView(clMapRoot);

        FloatingActionButton fabMapAdd = findViewById(R.id.fab_map);
        FABProgressCircle fabMapProgress = findViewById(R.id.fab_map_container);
        mWorkController.setFabMap(fabMapAdd, fabMapProgress);

        mWorkController.onStart();

    }

    private void grabMessage(){
        String message = etUserEntry.getText().toString();
        hideKeyboard();

        checkAndSetMessage(message);

    }

    private void checkAndSetMessage(String message){
        if (!TextUtils.isEmpty(message)) {
            mWorkController.setMessage(message);
            mMessageString = message;
        }

    }

    private Runnable etInputMessageTypingChecker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + typing_delay - 500)) {
                mWorkController.showSaveButton(true);
                grabMessage();
            }
        }
    };

    //---------------------------------------------------------------------------------------------------------
    private void requestImageDialogBox() {
        SelectPhotoDialogFromActivity selectPhotoDialog = new SelectPhotoDialogFromActivity();
        selectPhotoDialog.setListener(this);
        selectPhotoDialog.show(getSupportFragmentManager(), getString(R.string.app_dialog_name_select_photo));

    }

    private void createDialogPhotoView(String photoURL){
        ZoomablePhotoDialog photoDialog = new ZoomablePhotoDialog().newInstance(this,photoURL);
        FragmentManager fm = getSupportFragmentManager();
        photoDialog.show(fm, getResources().getString(R.string.FRAGMENT_DIALOG_PHOTO_VIEW));
    }

    public void setSingleImageUri(Uri singleImageUri) {
        mWorkController.setUserUploadedPictureUri(singleImageUri);
        mWorkController.setUserUploadedPictureBitmap(null);

        mWorkController.startPictureUploadLocal();

    }

    public void setSingleImageBitmap(Bitmap singleImageBitmap) {
        mWorkController.setUserUploadedPictureUri(null);
        mWorkController.setUserUploadedPictureBitmap(singleImageBitmap);

        mWorkController.startPictureUploadLocal();
    }
    //----------------------------------------------------------------------------------------------

    /**
     *OnMapReadyCallback
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        drawMap();
    }


    private void initMapView(){
        mMapView = findViewById(R.id.map_view_in_new_item_message);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    private void drawMap(){
        if(!isMapSet){
            isMapSet = true;
        }
        drawPositionSelf();

    }

    private void drawPositionSelf(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    private void setCameraViewSelf(){
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLocationRaw.getLatitude(), mLocationRaw.getLongitude())));

    }

    private void addMapMarkerSelf(boolean isStatic){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);

        FirestoreUser user = ((AppUserClient) getApplicationContext()).getUser();
        Location markerLocation;

        if(isStatic){
            if(isBestPositionAvailable){
                if(!isLocationLocked){
                    mLocationLocked = mLocationBest;
                    isLocationLocked = true;
                }
                markerLocation = mLocationLocked;
            }else{
                if(!isLocationLocked){
                    mLocationLocked = mLocationRaw;
                    isLocationLocked = true;
                }
                markerLocation = mLocationLocked;
            }

        }else{
            if(isBestPositionAvailable){
                markerLocation = mLocationBest;
            }else{
                markerLocation = mLocationRaw;
            }
        }

        mWorkController.setUserLocation(markerLocation);

        if(mGoogleMap != null){
            if(mClusterManager == null){
                mClusterManager = new ClusterManager<>(getApplicationContext(), mGoogleMap);
            }

            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new ClusterMarkerRenderer(
                        mContext,
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            try{
                String snippet = "This is you";
                    ClusterMarkerItem clusterMarker = new ClusterMarkerItem(
                            new LatLng(markerLocation.getLatitude(), markerLocation.getLongitude()),
                            user.getDisplay_name(),
                            snippet,
                            user
                    );

                mClusterManager.addItem(clusterMarker);

            }catch (NullPointerException e){
                Log.e(TAG, "to_delete: addMapMarkerSelf: Null Pointer Exception" + e.getMessage() );
            }

        }

        mClusterManager.cluster();
        mWorkController.callFabMapFinish();
    }

}
