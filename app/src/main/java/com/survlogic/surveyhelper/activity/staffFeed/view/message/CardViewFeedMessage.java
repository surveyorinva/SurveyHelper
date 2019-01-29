package com.survlogic.surveyhelper.activity.staffFeed.view.message;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.auth.User;
import com.google.maps.android.clustering.ClusterManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.photoGallery.PhotoGalleryActivity;
import com.survlogic.surveyhelper.activity.staffFeed.adapter.StaffFeedAdapter;
import com.survlogic.surveyhelper.activity.staffFeed.model.Feed;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedItem;
import com.survlogic.surveyhelper.activity.staffFeed.view.message.adapter.MessagePhotoAdapter;
import com.survlogic.surveyhelper.model.AppUserClient;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.services.maps.ClusterMarkerItem;
import com.survlogic.surveyhelper.services.maps.ClusterMarkerRenderer;
import com.survlogic.surveyhelper.utils.DateUtils;
import com.survlogic.surveyhelper.utils.DialogUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CardViewFeedMessage extends RecyclerView.ViewHolder implements OnMapReadyCallback {

    private static final String TAG = "CardViewFeedMessage";

    private Context mContext;
    private Activity mActivity;
    private StaffFeedAdapter.AdapterListener mListner;

    public LinearLayout llPostDetails, llPhotoGallery;
    private ConstraintLayout clPostMap, clPhoto;

    private FeedItem mFeedItem;
    private FirestoreUser mUser;

    //Map View
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private boolean isMapInit = false, isMapSet = false;

    //Map Zooming Variables
    private boolean isMapZoomable = true, didInitialMapZoom = false;
    private Timer mapZoomBlockingTimer;
    private Handler handlerOnUIThread;

    //Photos
    private GridView gvPhotoGallery;
    private ImageView ivPhoto;
    private ProgressBar pbImageLoad;

    private ArrayList<String> mPhotoList;
    private MessagePhotoAdapter photoGridAdapter;

    private boolean isPhotoGridAdapterSetup = false;
    private boolean isGridViewSetup = false;
    private boolean isItemPhotoGalleryNeeded = false;

    //Profile
    private ImageView ivProfileImage;
    private ProgressBar pbProfileImage;

    public TextView tvUserName, tvPostDate, tvPostDetails;

    //Message
    private String mItemMessage;

    public CardViewFeedMessage(@NonNull View itemView, Context context, StaffFeedAdapter.AdapterListener listener) {
        super(itemView);
        this.mContext = context;
        mActivity = (Activity) context;

        this.mListner = listener;

        initViewWidgets();
    }

    private void initViewWidgets(){
        ivProfileImage = itemView.findViewById(R.id.feed_user_profile_pic);
        pbProfileImage = itemView.findViewById(R.id.feed_user_profile_progress);

        tvUserName = itemView.findViewById(R.id.feed_user_name);
        tvPostDate = itemView.findViewById(R.id.feed_post_date);
        tvPostDetails = itemView.findViewById(R.id.feed_post_body);

        llPostDetails = itemView.findViewById(R.id.feed_ll_post_details);

        clPostMap = itemView.findViewById(R.id.feed_cl_post_map);

        clPhoto = itemView.findViewById(R.id.feed_cl_post_picture);
        llPhotoGallery = itemView.findViewById(R.id.feed_ll_post_pictures);

        ivPhoto = itemView.findViewById(R.id.image_view_in_item_message);

        pbImageLoad = itemView.findViewById(R.id.progress_bar_for_image_in_item_message);

        gvPhotoGallery = itemView.findViewById(R.id.feed_post_pictures);

    }


    public void configureViewHolder(ArrayList<Feed> mFeedList, int position){
        Feed feed = mFeedList.get(position);
        FeedItem item = feed.getItem();

        this.mFeedItem = item;

        resetPostDetailsView();
        resetPhotoView();
        resetMapView();

        getUserProfile(mFeedItem);
        getPostDetails(mFeedItem);
        getPostPhotos(mFeedItem);
        getMapItem(mFeedItem);

    }

    private void getUserProfile(FeedItem item){
        mUser = new FirestoreUser();
        mUser.setDisplay_name(item.getDisplay_name());
        mUser.setProfile_pic_url(item.getUser_profile_pic_url());

        tvUserName.setText(item.getDisplay_name());

        Timestamp timeStamp = item.getPostedOn();
        Date date = timeStamp.toDate();

        String day = DateUtils.getTimeStamp(date);
        tvPostDate.setText(day);

        switch (item.getFeed_post_type()){
            default:
                break;

        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(item.getUser_profile_pic_url(), ivProfileImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(pbProfileImage !=null){
                    pbProfileImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(pbProfileImage !=null){
                    pbProfileImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(pbProfileImage !=null){
                    pbProfileImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(pbProfileImage !=null){
                    pbProfileImage.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getPostDetails(FeedItem item){
        tvPostDetails.setText(item.getExtra_entry());

        if(item.getExtra_entry() != null){
            llPostDetails.setVisibility(View.VISIBLE);
        }

    }

    private void resetPostDetailsView(){
        llPostDetails.setVisibility(View.GONE);
    }

    private void resetPhotoView(){
        clPhoto.setVisibility(View.GONE);
        llPhotoGallery.setVisibility(View.GONE);

    }

    private void resetMapView(){
        clPostMap.setVisibility(View.GONE);

    }
    private void getPostPhotos(FeedItem item){

        resetPhotoView();

        ArrayList<String> listPhotos = item.getPhoto_link();
        if(listPhotos !=null){

            if(!isMapItemAvailable(item)){
                if(listPhotos.size() == 1) {
                    loadSingleImage(listPhotos);
                }else {
                    loadGalleryImages(item);
                }
            }else{
                    loadGalleryImages(item);
                }
        }else{
            resetPhotoView();
        }
    }


    private void loadSingleImage(ArrayList<String> photos){
        clPhoto.setVisibility(View.VISIBLE);

        final String photo_url = photos.get(0);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               createDialogPhotoView(photo_url);
           }
       });

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(photo_url, ivPhoto, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(pbImageLoad !=null){
                    pbImageLoad.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(pbImageLoad !=null){
                    pbImageLoad.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(pbImageLoad !=null){
                    pbImageLoad.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(pbImageLoad !=null){
                    pbImageLoad.setVisibility(View.GONE);
                }
            }
        });

    }

    private void loadGalleryImages(FeedItem item){
        mPhotoList = item.getPhoto_link();
        setGridView();

    }

    private void setGridView(){
        isGridViewSetup = true;

        gvPhotoGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        gvPhotoGallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DialogUtils.showAlertDialog(mContext,mActivity.getResources().getString(R.string.staff_feed_event_todo_message_delete_photo_title),mActivity.getResources().getString(R.string.staff_feed_event_todo_message_delete_photo_summary));
                return true;
            }
        });

        showPhotoGridView();

    }

    private void showPhotoGridView(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runGridView();
            }
        },100);

    }

    private void checkForGridView(){
        if(mPhotoList != null) {
            isItemPhotoGalleryNeeded = true;
        }
    }

    private void runGridView(){
        checkForGridView();

        if(isItemPhotoGalleryNeeded){
            if(!isPhotoGridAdapterSetup){
                photoGridAdapter = new MessagePhotoAdapter(mContext, R.layout.staff_feed_item_event_card_content_photo,mPhotoList);
                gvPhotoGallery.setAdapter(photoGridAdapter);
                isPhotoGridAdapterSetup = true;

            }else{
                photoGridAdapter.swapItems(mPhotoList);
            }

            if(!gvPhotoGallery.isShown()){
                llPhotoGallery.setVisibility(View.VISIBLE);
                gvPhotoGallery.setVisibility(View.VISIBLE);
            }
        }
    }

    private void createDialogPhotoView(String photoURL){
        mListner.openPhotoViewDialog(photoURL);
    }

    //----------------------------------------------------------------------------------------------

    private boolean isMapItemAvailable(FeedItem item){
        return item.getLocation() != null;
    }

    private void getMapItem(FeedItem item){

        if(isMapItemAvailable(item)){
            initMapView();
            clPostMap.setVisibility(View.VISIBLE);
        }

    }

    private void initMapView(){
        mMapView = itemView.findViewById(R.id.map_view_in_item_message);

        mMapView.onCreate(null);
        mMapView.getMapAsync(this);
        mMapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        try {
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    DialogUtils.showToast(mContext,"Click on Map");
                }
            });
            mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                @Override
                public void onCameraMoveStarted(int reason) {

                    if(reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
                        Log.d(TAG, "onCameraMoveStarted: User zoom action ");

                        isMapZoomable = false;

                        if(mapZoomBlockingTimer !=null){
                            mapZoomBlockingTimer.cancel();
                        }

                        handlerOnUIThread = new Handler();

                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                handlerOnUIThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mapZoomBlockingTimer = null;
                                        isMapZoomable = true;
                                    }
                                });
                            }
                        };

                        mapZoomBlockingTimer = new Timer();
                        mapZoomBlockingTimer.schedule(task,10*1000);

                    }
                }
            });

        } catch (SecurityException ex) {
            Log.e(TAG, "onMapReady: " + ex);
        }

        drawMap();
    }

    private void drawMap(){
        if(!isMapSet){
            isMapSet = true;
        }
        addMapMarkerLocation(mFeedItem);

    }



    private void addMapMarkerLocation(FeedItem item){
        ClusterManager<ClusterMarkerItem> mClusterManager = new ClusterManager<>(mActivity.getApplicationContext(), mGoogleMap);
        ClusterMarkerRenderer mClusterManagerRenderer  = new ClusterMarkerRenderer(
                mContext,
                mGoogleMap,
                mClusterManager
        );
        mClusterManager.setRenderer(mClusterManagerRenderer);;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);
        HashMap<String,Double> mServerLocation = item.getLocation();

        Location markerLocation = returnLocationFromHashMap(mServerLocation);
        setCameraViewSelf(markerLocation);

        if(mGoogleMap != null) {
            try {
                String snippet = "This is you";
                ClusterMarkerItem clusterMarker = new ClusterMarkerItem(
                        new LatLng(markerLocation.getLatitude(), markerLocation.getLongitude()),
                        mUser.getDisplay_name(),
                        snippet,
                        mUser
                );

                mClusterManager.addItem(clusterMarker);

            } catch (NullPointerException e) {
                Log.e(TAG, "to_delete: addMapMarkerSelf: Null Pointer Exception" + e.getMessage());
            }

            mClusterManager.cluster();

        }
    }

    private void setCameraViewSelf(Location zoomToLocation){
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(zoomToLocation.getLatitude(), zoomToLocation.getLongitude())));

    }


    private Location returnLocationFromHashMap(HashMap<String,Double> mServerLocation){
        Location markerLocation = new Location("provider");
        markerLocation.setLatitude(mServerLocation.get(mActivity.getResources().getString(R.string.HASH_KEY_LAT)));
        markerLocation.setLongitude(mServerLocation.get(mActivity.getResources().getString(R.string.HASH_KEY_LON)));

        return markerLocation;
    }
    //----------------------------------------------------------------------------------------------



}
