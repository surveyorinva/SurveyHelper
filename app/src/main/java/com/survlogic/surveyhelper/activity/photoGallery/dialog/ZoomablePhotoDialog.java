package com.survlogic.surveyhelper.activity.photoGallery.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;

public class ZoomablePhotoDialog extends DialogFragment {

    public interface DialogListener{
        void isPopupOpen(boolean isOpen);
    }

    private static final String TAG = "ZoomablePhotoDialog";
    private Context mContext;

    private PhotoView ivPhoto;
    private ProgressBar pbPhoto;
    private FloatingActionButton fabClose;

    private String photoURL;
    private ZoomablePhotoDialog.DialogListener mListener;



    public static ZoomablePhotoDialog newInstance(ZoomablePhotoDialog.DialogListener listener, String mImageURL) {
        ZoomablePhotoDialog frag = new ZoomablePhotoDialog();

        frag.mListener = listener;
        frag.photoURL = mImageURL;

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogExplode);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.photo_gallery_dialog_view,null);
        builder.setView(v);

        builder.create();
        return builder.show();


    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: Creating Image");
        mContext = getActivity();
        final AlertDialog alertDialog = (AlertDialog) getDialog();

        ivPhoto = getDialog().findViewById(R.id.photo_view);
        pbPhoto = getDialog().findViewById(R.id.photo_view_progress);

        setImage();
    }




    private void setImage(){
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(photoURL, ivPhoto, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(pbPhoto !=null){
                    pbPhoto.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(pbPhoto !=null){
                    pbPhoto.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(pbPhoto !=null){
                    pbPhoto.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(pbPhoto !=null){
                    pbPhoto.setVisibility(View.GONE);
                }
            }
        });
    }

}
