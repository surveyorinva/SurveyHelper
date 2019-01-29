package com.survlogic.surveyhelper.activity.staffFeed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.photoGallery.dialog.ZoomablePhotoDialog;
import com.survlogic.surveyhelper.activity.staffFeed.controller.StaffFeedController;
import com.survlogic.surveyhelper.activity.staff.inter.StaffActivityListener;
import com.survlogic.surveyhelper.dialog.SelectPhotoDialog;
import com.survlogic.surveyhelper.model.AppUserClient;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.Calendar;
import java.util.Date;

public class StaffFeedFragment extends Fragment implements  StaffFeedController.StaffFeedControllerListener,
                                                            SelectPhotoDialog.OnPhotoSelectedListener,
                                                            ZoomablePhotoDialog.DialogListener{

    private static final String TAG = "StaffFeedFragment";

    /**
     * StaffFeedControllerListener
     */

    @Override
    public void refreshFragmentUI() {

    }

    @Override
    public void sendFeedCategoryNameToAppBar(String feedCategory) {
        tvFragmentName.setText(feedCategory);
    }

    @Override
    public void requestImageDialogBox() {
        SelectPhotoDialog selectPhotoDialog = new SelectPhotoDialog();
        selectPhotoDialog.show(getFragmentManager(),getString(R.string.app_dialog_name_select_photo));
        selectPhotoDialog.setTargetFragment(StaffFeedFragment.this,1);
    }

    @Override
    public void requestPhotoViewDialogBox(String photoURL) {
        Log.d(TAG, "to_delete: In Fragment!");
        createDialogPhotoView(photoURL);
    }

    /**
     * SelectPhotoDialog.OnPhotoSelectedListener
     */

    @Override
    public void returnImagePath(Uri imagePath) {
        mFeedController.returnToRecyclerURI(imagePath);
    }

    @Override
    public void returnImageThumbnail(Bitmap bitmap) {

    }

    @Override
    public void returnImageFull(Bitmap bitmap) {
        Log.d(TAG, "to_delete: Returned to fragment a bitmap! ");
        mFeedController.returnToRecyclerBitmap(bitmap);
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

    private View v;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageButton ibBackdrop, ibNavigator, ibFeedNavigator;
    private TextView tvFragmentName;

    private StaffActivityListener mActivityListener;
    private StaffFeedController mFeedController;

    private StaffFeedFragment mThisFragment;
    private FirestoreUser mFirestoreUser;

    private boolean isUserSetInController = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
        mActivity = (Activity) context;

        mActivityListener = (StaffActivityListener) getActivity();
        mFeedController = new StaffFeedController(mContext,this,this);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.staff_feed_fragment, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

    }


    @Override
    public void onResume() {
        super.onResume();

        Date today = Calendar.getInstance().getTime();

        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);
        String feed_room = preferenceLoader.getFeedPublicRoom();

        mFeedController.setFeedQueryDateToShow(today);
        mFeedController.setFeedRoomToShow(feed_room);
        mFeedController.setFeedRoomPublic(feed_room);
        mFeedController.fetchPublicRoomFeeds();

        showAnnouncementAtStart();

        if(mFirestoreUser == null){
            FirestoreUser user = ((AppUserClient) mActivity.getApplicationContext()).getUser();
            if(user != null){
                mFeedController.setFirestoreUser(user);
                mFeedController.buildPrivateFeedRooms();

            }
        }

    }

    private void initView(){
        TextView tvActivityName = v.findViewById(R.id.appBar_top_title);
        tvActivityName.setText(getActivity().getString(R.string.app_set_for_staff));
        tvActivityName.setVisibility(View.VISIBLE);

        tvFragmentName = v.findViewById(R.id.appBackdrop_top_title);
        tvFragmentName.setText(getActivity().getString(R.string.staff_feed_navigation_feed_corporate));
        tvFragmentName.setVisibility(View.VISIBLE);

        ibNavigator = v.findViewById(R.id.appBar_top_action_nav_menu);
        ibNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityListener.callNavigationDrawer();
            }
        });

        mFeedController.setFragmentView(v);
    }

    private void showAnnouncementAtStart(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mFeedController.getBannerFeedAnnouncement();
            }
        }, 2000);
    }

    public void setFirestoreUser(FirestoreUser user) {
        this.mFirestoreUser = user;

        if(!isUserSetInController){
            mFeedController.setFirestoreUser(user);
            isUserSetInController = true;
        }

        mFeedController.buildPrivateFeedRooms();

    }

    private void createDialogPhotoView(String photo){
        ZoomablePhotoDialog photoDialog = new ZoomablePhotoDialog().newInstance(this,photo);
        FragmentManager fm = getChildFragmentManager();
        photoDialog.show(fm, getResources().getString(R.string.FRAGMENT_DIALOG_PHOTO_VIEW));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "to_delete: onActivityResult: Started");
    }
}
