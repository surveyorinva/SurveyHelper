package com.survlogic.surveyhelper.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.utils.GraphicRotationUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectPhotoDialog extends DialogFragment {

    private static final String TAG = "SelectPhotoDialog";
    private static final int PICKFILE_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 2000;

    public interface OnPhotoSelectedListener{
        void returnImagePath(Uri imagePath);
        void returnImageThumbnail(Bitmap bitmap);
        void returnImageFull(Bitmap bitmap);
        void returnImageFullError(boolean isError);
        Context getContextFromParent();
    }
    private OnPhotoSelectedListener mOnPhotoSelectedListener;

    private String mCurrentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

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
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    Log.d(TAG, "to_delete: Photo File: " + photoFile);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    //Todo
                }

                if (photoFile != null) {
                    Context context = mOnPhotoSelectedListener.getContextFromParent();

                    Uri photoURI = FileProvider.getUriForFile(context,
                            context.getApplicationInfo().packageName + ".provider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                     startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }

            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "to_delete: Request Code: " + requestCode + ", Status: " + resultCode);
        /*
            Results when selecting a new image from memory
         */
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            //send the uri to PostFragment & dismiss dialog
            mOnPhotoSelectedListener.returnImagePath(selectedImageUri);
            getDialog().dismiss();
        }
        else if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.d(TAG, "to_delete: Camera Request Code OK ");

            Context context = mOnPhotoSelectedListener.getContextFromParent();
            Activity activity = (Activity)  context;
            try {
                Bitmap mImageBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.parse(mCurrentPhotoPath));

                GraphicRotationUtils bitmapUtils = new GraphicRotationUtils();
                bitmapUtils.setContext(mOnPhotoSelectedListener.getContextFromParent());
                Bitmap mImageBitmapProcessed = bitmapUtils.rotateImageIfRequired(mImageBitmap,Uri.parse(mCurrentPhotoPath));

                mOnPhotoSelectedListener.returnImageFull(mImageBitmapProcessed);
                Log.d(TAG, "to_delete: Returned Bitmap Full ");
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        Context context = mOnPhotoSelectedListener.getContextFromParent();
        Activity activity = (Activity)  context;

        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}
