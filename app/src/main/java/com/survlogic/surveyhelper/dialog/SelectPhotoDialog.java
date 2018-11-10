package com.survlogic.surveyhelper.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;

public class SelectPhotoDialog extends DialogFragment {

    private static final String TAG = "SelectPhotoDialog";
    private static final int PICKFILE_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 2000;

    public interface OnPhotoSelectedListener{
        void returnImagePath(Uri imagePath);
        void returnImageBitmap(Bitmap bitmap);
    }
    OnPhotoSelectedListener mOnPhotoSelectedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.app_dialog_choose_image, container, false);

        TextView selectPhoto = view.findViewById(R.id.dialogChoosePhoto);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });

        TextView takePhoto  = view.findViewById(R.id.dialogOpenCamera);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
            Results when selecting a new image from memory
         */
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            //send the uri to PostFragment & dismiss dialog
            mOnPhotoSelectedListener.returnImagePath(selectedImageUri);
            getDialog().dismiss();
        }
        /*
            Results when taking a new photo with camera
         */
        else if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            //send the bitmap to PostFragment and dismiss dialog
            mOnPhotoSelectedListener.returnImageBitmap(bitmap);
            getDialog().dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnPhotoSelectedListener = (OnPhotoSelectedListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }

}
