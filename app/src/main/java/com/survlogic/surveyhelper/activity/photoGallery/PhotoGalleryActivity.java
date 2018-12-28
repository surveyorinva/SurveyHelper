package com.survlogic.surveyhelper.activity.photoGallery;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.photoGallery.adapter.PhotoGalleryAdapter;
import com.survlogic.surveyhelper.activity.photoGallery.dialog.ZoomablePhotoDialog;
import com.survlogic.surveyhelper.utils.BaseActivity;

import java.util.ArrayList;

public class PhotoGalleryActivity extends BaseActivity implements PhotoGalleryAdapter.PhotoGalleryAdapterListener,
                                                                    ZoomablePhotoDialog.DialogListener{

    private static final String TAG = "PhotoGalleryActivity";

    /**
     * PhotoGalleryAdapter.PhotoGalleryAdapterListener
     */

    @Override
    public void openPhotoViewDialog(String photoURL) {
        createDialogPhotoView(photoURL);
    }


    /**
     * ZoomablePhotoDialog.DialogListener
     */

    @Override
    public void isPopupOpen(boolean isOpen) {

    }

    private Context mContext;
    private GridView photoGridView;
    private PhotoGalleryAdapter photoGridAdapter;
    private boolean isPhotoGridAdapterSetup = false;

    private ArrayList<String> mPhotoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_gallery_activity);

        mContext = PhotoGalleryActivity.this;

        getBundleInformation();
        initViewWidgets();

    }

    @Override
    protected void onResume() {
        super.onResume();
        showPhotoGridView();
    }

    private void getBundleInformation(){
        Bundle extras = getIntent().getExtras();
        mPhotoList = extras.getStringArrayList(getResources().getString(R.string.KEY_GALLERY_PHOTO_STRING_ARRAY_URL));
    }

    private void initViewWidgets(){

        TextView tvAppBarTitle = findViewById(R.id.appBar_top_title);
        tvAppBarTitle.setText(getResources().getString(R.string.gallery_name));
        tvAppBarTitle.setVisibility(View.VISIBLE);

        ImageButton ibAppBarBack = findViewById(R.id.appBar_top_action_nav_back);
        ibAppBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ibAppBarBack.setVisibility(View.VISIBLE);

        photoGridView = findViewById(R.id.photo_grid_view);

        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "to_delete: Click ");

                Activity mActivity = (Activity) mContext;

                String photoURL = mPhotoList.get(position);
                createDialogPhotoView(photoURL);


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
        },200);

    }

    private void runGridView(){
        Log.d(TAG, "to_delete: Running Grid View ");

        if(!isPhotoGridAdapterSetup){
            photoGridAdapter = new PhotoGalleryAdapter(mContext, R.layout.staff_feed_item_event_card_content_photo,mPhotoList,this);
            photoGridView.setAdapter(photoGridAdapter);
            isPhotoGridAdapterSetup = true;

        }else{
            photoGridAdapter.swapItems(mPhotoList);
        }

        if(!photoGridView.isShown()){
            photoGridView.setVisibility(View.VISIBLE);
        }

    }



    private void createDialogPhotoView(String photo){
        ZoomablePhotoDialog photoDialog = new ZoomablePhotoDialog().newInstance(this,photo);
        FragmentManager fm = getSupportFragmentManager();
        photoDialog.show(fm, getResources().getString(R.string.FRAGMENT_DIALOG_PHOTO_VIEW));

    }
}
