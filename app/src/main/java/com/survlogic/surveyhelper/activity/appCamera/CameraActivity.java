package com.survlogic.surveyhelper.activity.appCamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appCamera.inter.ICallback;
import com.survlogic.surveyhelper.activity.appCamera.inter.ICameraActivity;
import com.survlogic.surveyhelper.activity.appCamera.utils.CameraUtils;
import com.survlogic.surveyhelper.activity.appCamera.view.DrawableImageView;
import com.survlogic.surveyhelper.activity.appCamera.view.ScalingTextureView;
import com.survlogic.surveyhelper.activity.appCamera.view.VerticalSlideColorPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CameraActivity extends AppCompatActivity implements
                                                View.OnClickListener,
                                                View.OnTouchListener{

    private static final String TAG = "CameraActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    /** Time it takes for icons to fade (in milliseconds) */
    private static final int ICON_FADE_DURATION  = 400;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /** The current state of camera state for taking pictures.
     * @see #mCaptureCallback */
    private int mState = STATE_PREVIEW;

    /** Camera state: Showing camera preview. */
    private static final int STATE_PREVIEW = 0;

    /** Camera state: Waiting for the focus to be locked. */
    private static final int STATE_WAITING_LOCK = 1;

    /** Camera state: Waiting for the exposure to be precapture state. */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /** Camera state: Waiting for the exposure state to be something other than precapture. */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /** Camera state: Picture was taken. */
    private static final int STATE_PICTURE_TAKEN = 4;

    /** States for the flash */
    private static final int FLASH_STATE_OFF = 0;
    private static final int FLASH_STATE_ON = 1;
    private static final int FLASH_STATE_AUTO = 2;


    //vars
    /** A {@link Semaphore} to prevent the app from exiting before closing the camera. */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /** A {@link CameraCaptureSession } for camera preview. */
    private CameraCaptureSession mCaptureSession;

    /** A reference to the opened {@link CameraDevice}. */
    private CameraDevice mCameraDevice;

    /** ID of the current {@link CameraDevice}. */
    private String mCameraId;

    /** The {@link android.util.Size} of camera preview. */
    private Size mPreviewSize;

    /** Orientation of the camera sensor */
    private int mSensorOrientation;

    /** An {@link ScalingTextureView} for camera preview. */
    private ScalingTextureView mTextureView;

    /** {@link CaptureRequest.Builder} for the camera preview */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /** {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder} */
    private CaptureRequest mPreviewRequest;

    /** An additional thread for running tasks that shouldn't block the UI. */
    private HandlerThread mBackgroundThread;

    /** A {@link Handler} for running tasks in the background. */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /** Max preview width that is guaranteed by Camera2 API */
    private int MAX_PREVIEW_WIDTH = 1920;

    /** Max preview height that is guaranteed by Camera2 API */
    private int MAX_PREVIEW_HEIGHT = 1080;

    private int SCREEN_WIDTH = 0;

    private int SCREEN_HEIGHT = 0;

    private float ASPECT_RATIO_ERROR_RANGE = 0.1f;

    private Image mCapturedImage;

    private boolean mIsImageAvailable = false;

    private Bitmap mCapturedBitmap;

    private BackgroundImageRotater mBackgroundImageRotater;

    private boolean mIsDrawingEnabled = false;

    boolean mIsCurrentlyDrawing = false;

    private int mFlashState = 0;

    private boolean mFlashSupported;

    private String mCameraOrientation = "none"; // Front-facing or back-facing
    private static String CAMERA_POSITION_FRONT;
    private static String CAMERA_POSITION_BACK;
    private static String MAX_ASPECT_RATIO;

    //widgets
    private RelativeLayout mStillshotContainer, mFlashContainer, mSwitchOrientationContainer,
            mCaptureBtnContainer, mCloseStillshotContainer,
            mSaveContainer;
    private DrawableImageView mStillshotImageView;
    private ImageButton mTrashIcon, mFlashIcon;
    private VerticalSlideColorPicker mVerticalSlideColorPicker;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        initViewWidgets();
        setMaxSizes();

    }

    private void initViewWidgets(){
        Log.d(TAG, "onViewCreated: created view.");
        findViewById(R.id.stillshot).setOnClickListener(this);
        findViewById(R.id.switch_orientation).setOnClickListener(this);
        findViewById(R.id.save_stillshot).setOnClickListener(this);

        mFlashIcon = findViewById(R.id.flash_toggle);
        mFlashContainer = findViewById(R.id.flash_container);
        mSaveContainer = findViewById(R.id.save_container);
        mCloseStillshotContainer = findViewById(R.id.close_stillshot_view);
        mStillshotImageView = findViewById(R.id.stillshot_imageview);
        mStillshotContainer = findViewById(R.id.stillshot_container);
        mFlashContainer = findViewById(R.id.flash_container);
        mSwitchOrientationContainer = findViewById(R.id.switch_orientation_container);
        mCaptureBtnContainer = findViewById(R.id.capture_button_container);
        mTextureView = findViewById(R.id.texture_view);

        mFlashIcon.setOnClickListener(this);

        mCloseStillshotContainer.setOnClickListener(this);

        mStillshotImageView.setOnTouchListener(this);
        mTextureView.setOnTouchListener(this);

        progressBar = findViewById(R.id.progress_bar_camera);

        setMaxSizes();
        resetIconVisibilities();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stillshot: {
                if(!mIsImageAvailable){
                    Log.d(TAG, "onClick: taking picture.");
                    takePicture();
                }
                break;
            }

            case R.id.switch_orientation: {
                Log.d(TAG, "onClick: switching camera orientation.");
                toggleCameraDisplayOrientation();
                break;
            }

            case R.id.close_stillshot_view:{
                hideStillshotContainer();
                break;
            }

            case R.id.undo_container:{
                undoAction();
                break;
            }

            case R.id.save_stillshot:{
                saveCapturedStillshotToDisk();
                break;
            }

            case R.id.flash_toggle: {
                if(!mIsImageAvailable){
                    toggleFlashState();
                }
                break;
            }
        }
    }

    private void toggleFlashState(){
        if(mFlashState == FLASH_STATE_OFF){
            mFlashState = FLASH_STATE_ON;
        }
        else if(mFlashState == FLASH_STATE_ON){
            mFlashState = FLASH_STATE_AUTO;
        }
        else if(mFlashState == FLASH_STATE_AUTO){
            mFlashState = FLASH_STATE_OFF;
        }
        setFlashIcon();
    }

    private void setFlashIcon(){
        if(mFlashState == FLASH_STATE_OFF){
            Glide.with(this)
                    .load(R.drawable.ic_flash_off_dark_24dp)
                    .into(mFlashIcon);
        }
        else if(mFlashState == FLASH_STATE_ON){
            Glide.with(this)
                    .load(R.drawable.ic_flash_on_dark_24dp)
                    .into(mFlashIcon);
        }
        else if(mFlashState == FLASH_STATE_AUTO){
            Glide.with(this)
                    .load(R.drawable.ic_flash_auto_dark_24dp)
                    .into(mFlashIcon);
        }
        setAutoFlash(mPreviewRequestBuilder);
    }


    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            if(mFlashState == FLASH_STATE_OFF){
                requestBuilder.set(CaptureRequest.FLASH_MODE,
                        CaptureRequest.FLASH_MODE_OFF);
            }
            else if(mFlashState == FLASH_STATE_ON){
                requestBuilder.set(CaptureRequest.FLASH_MODE,
                        CaptureRequest.FLASH_MODE_SINGLE);
            }
            else if(mFlashState == FLASH_STATE_AUTO){
                requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            }
        }
    }

    public void setTrashIconSize(int width, int height){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTrashIcon.getLayoutParams();

        final float scale = getResources().getDisplayMetrics().density;

        params.height = (int) (width * scale + 0.5f);
        params.width = (int) (height * scale + 0.5f);
        mTrashIcon.setLayoutParams(params);
    }


    private void saveCapturedStillshotToDisk(){
        if(mIsImageAvailable){
            Log.d(TAG, "saveCapturedStillshotToDisk: saving image to disk.");

            final ICallback callback = new ICallback() {
                @Override
                public void done(Exception e) {
                    if(e == null){
                        Log.d(TAG, "onImageSavedCallback: image saved!");
                        showSnackBar("Image saved", Snackbar.LENGTH_SHORT);
                        finish();

                    }
                    else{
                        Log.d(TAG, "onImageSavedCallback: error saving image: " + e.getMessage());
                        showSnackBar("Error saving image", Snackbar.LENGTH_SHORT);
                    }
                }
            };

            if(mCapturedImage != null){

                Log.d(TAG, "saveCapturedStillshotToDisk: saving to disk.");

                mStillshotImageView.invalidate();
                Bitmap bitmap = Bitmap.createBitmap(mStillshotImageView.getDrawingCache());

                ImageSaver imageSaver = new ImageSaver(
                        bitmap,
                        getExternalFilesDir(null),
                        callback
                );
                mBackgroundHandler.post(imageSaver);
            }
        }
    }

    private void undoAction(){
        if(mIsDrawingEnabled){
            mStillshotImageView.removeLastPath();
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:{
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                break;
            }

            case MotionEvent.ACTION_UP:{
                float endX = motionEvent.getX();
                float endY = motionEvent.getY();
                if (isAClick(startX, endX, startY, endY)) {
                    if(view.getId() == R.id.texture && view.getId() != R.id.stillshot){
                        Log.d(TAG, "onTouch: MANUAL FOCUS.");
                        startManualFocus(view, motionEvent);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:{
                break;
            }
        }

        return true;
    }

    private boolean mManualFocusEngaged = false;
    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
    }

    private boolean isMeteringAreaAFSupported() {

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = null;
        try {
            characteristics = manager.getCameraCharacteristics(mCameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return false;
        }
        return characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
    }

    private boolean startManualFocus(View view, MotionEvent motionEvent){
        Log.d(TAG, "startManualFocus: called");

        if (mManualFocusEngaged) {
            Log.d(TAG, "startManualFocus: Manual focus already engaged");
            return true;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = null;
        try {
            characteristics = manager.getCameraCharacteristics(mCameraId);

            final Rect sensorArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);

            //TODO: here I just flip x,y, but this needs to correspond with the sensor orientation (via SENSOR_ORIENTATION)
            final int y = (int)((motionEvent.getX() / (float)view.getWidth())  * (float)sensorArraySize.height());
            final int x = (int)((motionEvent.getY() / (float)view.getHeight()) * (float)sensorArraySize.width());
            final int halfTouchWidth  = 150; //(int)motionEvent.getTouchMajor(); //TODO: this doesn't represent actual touch size in pixel. Values range in [3, 10]...
            final int halfTouchHeight = 150; //(int)motionEvent.getTouchMinor();
            MeteringRectangle focusAreaTouch = new MeteringRectangle(Math.max(x - halfTouchWidth,  0),
                    Math.max(y - halfTouchHeight, 0),
                    halfTouchWidth  * 2,
                    halfTouchHeight * 2,
                    MeteringRectangle.METERING_WEIGHT_MAX - 1);

            CameraCaptureSession.CaptureCallback captureCallbackHandler = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    mManualFocusEngaged = false;

                    if (request.getTag() == "FOCUS_TAG") {
                        //the focus trigger is complete -
                        //resume repeating (preview surface will get frames), clear AF trigger
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);

                        //reset to get ready to capture a picture
                        try {
                            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                    Log.e(TAG, "startManualFocus: Manual AF failure: " + failure);
                    mManualFocusEngaged = false;
                }
            };

            //first stop the existing repeating request
            mCaptureSession.stopRepeating();

            //cancel any existing AF trigger (repeated touches, etc.)
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), captureCallbackHandler, mBackgroundHandler);

            //Now add a new AF trigger with focus region
            if (isMeteringAreaAFSupported()) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{focusAreaTouch});
            }
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);

            mPreviewRequestBuilder.setTag("FOCUS_TAG"); //we'll capture this later for resuming the preview

            //then we ask for a single request (not repeating!)
            mCaptureSession.capture(mPreviewRequestBuilder.build(), captureCallbackHandler, mBackgroundHandler);
            mManualFocusEngaged = true;


        } catch (CameraAccessException e) {
            e.printStackTrace();
            return true;
        }

        return true;
    }

    private void hideStillshotContainer(){
        showStatusBar();

        if(mIsImageAvailable){
            mIsImageAvailable = false;
            mCapturedBitmap = null;
            mStillshotImageView.setImageBitmap(null);

            mIsDrawingEnabled = false;
            mStillshotImageView.reset();
            mStillshotImageView.setDrawingIsEnabled(mIsDrawingEnabled);
            mStillshotImageView.setImageBitmap(null);

            resetIconVisibilities();

            mTextureView.resetScale();

            reopenCamera();
        }
    }

    private void resetIconVisibilities(){
        mStillshotContainer.setVisibility(View.INVISIBLE);

        mFlashContainer.setVisibility(View.VISIBLE);
        mSwitchOrientationContainer.setVisibility(View.VISIBLE);
        mCaptureBtnContainer.setVisibility(View.VISIBLE);

    }

    /**
     * Initiate a still image capture.
     */
    private void takePicture()  {
        lockFocus();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);

            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;

            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable: w: " + width + ", h: " + height);
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            Log.d(TAG, "onSurfaceTextureSizeChanged: w: " + width + ", h: " + height);
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    public void reopenCamera() {
        Log.d(TAG, "reopenCamera: called.");
        if (mTextureView.isAvailable()) {
            Log.d(TAG, "reopenCamera: a surface is available.");
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            Log.d(TAG, "reopenCamera: no surface is available.");
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

    }

    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    else if(afState == CaptureResult.CONTROL_AF_STATE_INACTIVE){
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        Log.d(TAG, "captureStillPicture: capturing picture.");
        try {
            if (null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            setAutoFlash(captureBuilder);

            // Orientation
            // Rotate the image from screen orientation to image orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d(TAG, "onImageAvailable: called.");

            if(!mIsImageAvailable){
                mCapturedImage = reader.acquireLatestImage();

                Log.d(TAG, "onImageAvailable: captured image width: " + mCapturedImage.getWidth());
                Log.d(TAG, "onImageAvailable: captured image height: " + mCapturedImage.getHeight());

                saveTempImageToStorage();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Context context = CameraActivity.this;
                        Glide.with(context)
                                .load(mCapturedImage)
                                .into(mStillshotImageView);

                        showStillshotContainer();
                    }
                });

            }

        }
    };
    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void
    unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);

            setAutoFlash(mPreviewRequestBuilder);

            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);

            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;

            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            Log.d(TAG, "onError: " + error);
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            finish();
        }
    };

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);


            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;

                            try {
                                // Auto focus should be continuous for camera preview.
                                // Most new-ish phones can auto focus
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                // Flash is automatically enabled when necessary.
                                setAutoFlash(mPreviewRequestBuilder);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            showSnackBar("Failed", Snackbar.LENGTH_LONG);
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /** Closes the current {@link CameraDevice}. */
    private void closeCamera() {

        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /** Starts a background thread and its {@link Handler}. */
    private void startBackgroundThread() {
        Log.d(TAG, "startBackgroundThread: called.");
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());

    }

    /** Stops the background thread and its {@link Handler}. */
    private void stopBackgroundThread() {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: called.");
        super.onResume();

        startBackgroundThread();

        if(mIsImageAvailable){
            hideStatusBar();
        }
        else{
            showStatusBar();

            // When the screen is turned off and turned back on, the SurfaceTexture is already
            // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
            // a camera and start preview from here (otherwise, we wait until the surface is ready in
            // the SurfaceTextureListener).
            reopenCamera();
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        if(mBackgroundImageRotater != null){
            mBackgroundImageRotater.cancel(true);
        }
        super.onPause();
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            Log.d(TAG, "setUpCameraOutputs: called.");
            if (!isCameraBackFacing() && !isCameraFrontFacing()) {
                Log.d(TAG, "setUpCameraOutputs: finding camera id's.");
                findCameraIds();
            }

            CameraCharacteristics characteristics
                    = manager.getCameraCharacteristics(mCameraId);

            Log.d(TAG, "setUpCameraOutputs: camera id: " + mCameraId);

            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;

            Size largest = null;
            float screenAspectRatio = (float)SCREEN_WIDTH / (float)SCREEN_HEIGHT;
            List<Size> sizes = new ArrayList<>();
            for( Size size : Arrays.asList(map.getOutputSizes(ImageFormat.JPEG))){

                float temp = (float)size.getWidth() / (float)size.getHeight();

                Log.d(TAG, "setUpCameraOutputs: temp: " + temp);
                Log.d(TAG, "setUpCameraOutputs: w: " + size.getWidth() + ", h: " + size.getHeight());

                if(temp > (screenAspectRatio - screenAspectRatio * ASPECT_RATIO_ERROR_RANGE )
                        && temp < (screenAspectRatio + screenAspectRatio * ASPECT_RATIO_ERROR_RANGE)){
                    sizes.add(size);
                    Log.d(TAG, "setUpCameraOutputs: found a valid size: w: " + size.getWidth() + ", h: " + size.getHeight());
                }

            }
            if(sizes.size() > 0){
                largest = Collections.max(
                        sizes,
                        new CameraUtils.CompareSizesByArea());
                Log.d(TAG, "setUpCameraOutputs: largest width: " + largest.getWidth());
                Log.d(TAG, "setUpCameraOutputs: largest height: " + largest.getHeight());
            }

            // Find out if we need to swap dimension to get the preview size relative to sensor
            // coordinate.
            int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
            //noinspection ConstantConditions
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            boolean swappedDimensions = false;
            switch (displayRotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                        swappedDimensions = true;
                    }
                    break;
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                        swappedDimensions = true;
                    }
                    break;
                default:
                    Log.e(TAG, "Display rotation is invalid: " + displayRotation);
            }

            Point displaySize = new Point();
            getWindowManager().getDefaultDisplay().getSize(displaySize);
            int rotatedPreviewWidth = width;
            int rotatedPreviewHeight = height;
            int maxPreviewWidth = displaySize.x;
            int maxPreviewHeight = displaySize.y;

            if (swappedDimensions) {
                rotatedPreviewWidth = height;
                rotatedPreviewHeight = width;
                maxPreviewWidth = displaySize.y;
                maxPreviewHeight = displaySize.x;
            }

            if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                maxPreviewWidth = MAX_PREVIEW_WIDTH;
            }

            if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                maxPreviewHeight = MAX_PREVIEW_HEIGHT;
            }

            Log.d(TAG, "setUpCameraOutputs: max preview width: " + maxPreviewWidth);
            Log.d(TAG, "setUpCameraOutputs: max preview height: " + maxPreviewHeight);


            mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                    ImageFormat.JPEG, /*maxImages*/2);
            mImageReader.setOnImageAvailableListener(
                    mOnImageAvailableListener, mBackgroundHandler);


            mPreviewSize = CameraUtils.chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                    maxPreviewHeight, largest);


            Log.d(TAG, "setUpCameraOutputs: preview width: " + mPreviewSize.getWidth());
            Log.d(TAG, "setUpCameraOutputs: preview height: " + mPreviewSize.getHeight());

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(
                        mPreviewSize.getWidth(), mPreviewSize.getHeight(), SCREEN_WIDTH, SCREEN_HEIGHT);
            } else {
                mTextureView.setAspectRatio(
                        mPreviewSize.getHeight(), mPreviewSize.getWidth(), SCREEN_HEIGHT, SCREEN_WIDTH);
            }


            Log.d(TAG, "setUpCameraOutputs: cameraId: " + mCameraId);

            // Check if the flash is supported.
            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            mFlashSupported = available == null ? false : available;

        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        }

    }

    private void setMaxSizes(){
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        SCREEN_HEIGHT = displaySize.x;
        SCREEN_WIDTH = displaySize.y;

        Log.d(TAG, "setMaxSizes: screen width:" + SCREEN_WIDTH);
        Log.d(TAG, "setMaxSizes: screen height: " + SCREEN_HEIGHT);
    }


    private void findCameraIds(){

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId : manager.getCameraIdList()) {
                Log.d(TAG, "findCameraIds: CAMERA ID: " + cameraId);
                if (cameraId == null) continue;
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_FRONT){
                    setFrontCameraId(cameraId);
                }
                else if (facing == CameraCharacteristics.LENS_FACING_BACK){
                    setBackCameraId(cameraId);
                }
            }
            setCameraBackFacing();
            mCameraId = getBackCameraId();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void toggleCameraDisplayOrientation(){
        if(mCameraId.equals(getBackCameraId())){
            mCameraId = getFrontCameraId();
            setCameraFrontFacing();
            closeCamera();
            reopenCamera();
            Log.d(TAG, "toggleCameraDisplayOrientation: switching to front-facing camera.");
        }
        else if(mCameraId.equals(getFrontCameraId())){
            mCameraId = getBackCameraId();
            setCameraBackFacing();
            closeCamera();
            reopenCamera();
            Log.d(TAG, "toggleCameraDisplayOrientation: switching to back-facing camera.");
        }
        else{
            Log.d(TAG, "toggleCameraDisplayOrientation: error.");
        }
    }
    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        Log.d(TAG, "configureTransform: viewWidth: " + viewWidth + ", viewHeight: " + viewHeight);
        Log.d(TAG, "configureTransform: previewWidth: " + mPreviewSize.getWidth() + ", previewHeight: " + mPreviewSize.getHeight());
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            Log.d(TAG, "configureTransform: rotating from 90 or 270");
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            Log.d(TAG, "configureTransform: rotating 180.");
            matrix.postRotate(180, centerX, centerY);
        }


        float screenAspectRatio = (float)SCREEN_WIDTH / (float)SCREEN_HEIGHT;
        float previewAspectRatio = (float)mPreviewSize.getWidth() / (float)mPreviewSize.getHeight();
        String roundedScreenAspectRatio = String.format("%.2f", screenAspectRatio);
        String roundedPreviewAspectRatio = String.format("%.2f", previewAspectRatio);
        if(!roundedPreviewAspectRatio.equals(roundedScreenAspectRatio) ){

            float scaleFactor = (screenAspectRatio / previewAspectRatio);
            Log.d(TAG, "configureTransform: scale factor: " + scaleFactor);

            float heightCorrection = (((float)SCREEN_HEIGHT * scaleFactor) - (float)SCREEN_HEIGHT) / 2;

            matrix.postScale(scaleFactor, 1);
            matrix.postTranslate(-heightCorrection, 0);
        }

        mTextureView.setTransform(matrix);
    }




    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void saveTempImageToStorage(){

        Log.d(TAG, "saveTempImageToStorage: saving temp image to disk.");
        final ICallback callback = new ICallback() {
            @Override
            public void done(Exception e) {
                if(e == null){
                    Log.d(TAG, "onImageSavedCallback: image saved!");

                    mBackgroundImageRotater = new BackgroundImageRotater(CameraActivity.this);
                    mBackgroundImageRotater.execute();
                    mIsImageAvailable = true;
                    mCapturedImage.close();

                }
                else{
                    Log.d(TAG, "onImageSavedCallback: error saving image: " + e.getMessage());
                    showSnackBar("Error displaying image", Snackbar.LENGTH_SHORT);
                }
            }
        };

        ImageSaver imageSaver = new ImageSaver(
                mCapturedImage,
                getExternalFilesDir(null),
                callback
        );
        mBackgroundHandler.post(imageSaver);
    }

    private void displayCapturedImage(){
        Log.d(TAG, "displayCapturedImage: displaying stillshot image.");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop();

                int bitmapWidth = mCapturedBitmap.getWidth();
                int bitmapHeight = mCapturedBitmap.getHeight();

                Log.d(TAG, "run: captured image width: " + bitmapWidth);
                Log.d(TAG, "run: captured image height: " + bitmapHeight);


                int focusX = (int)(mTextureView.mFocusX);
                int focusY = (int)(mTextureView.mFocusY);
                Log.d(TAG, "run: focusX: " + focusX);
                Log.d(TAG, "run: focusY: " + focusY);


                int maxWidth = mTextureView.getWidth();
                int maxHeight = mTextureView.getHeight();
                Log.d(TAG, "run: initial maxWidth: " + maxWidth);
                Log.d(TAG, "run: initial maxHeight: " + maxHeight);

                float bitmapHeightScaleFactor = (float)bitmapHeight / (float)maxHeight;
                float bitmapWidthScaleFactor = (float)bitmapWidth / (float)maxWidth;
                Log.d(TAG, "run: bitmap width scale factor: " + bitmapWidthScaleFactor);
                Log.d(TAG, "run: bitmap height scale factor: " + bitmapHeightScaleFactor);

                int actualWidth = (int)(maxWidth * (1 / mTextureView.mScaleFactorX));
                int actualHeight = (int)(maxHeight * (1 / mTextureView.mScaleFactorY));
                Log.d(TAG, "run: actual width: " + actualWidth);
                Log.d(TAG, "run: actual height: " + actualHeight);


                int scaledWidth = (int)(actualWidth * bitmapWidthScaleFactor);
                int scaledHeight = (int)(actualHeight * bitmapHeightScaleFactor);
                Log.d(TAG, "run: scaled width: " + scaledWidth);
                Log.d(TAG, "run: scaled height: " + scaledHeight);

                focusX *= bitmapWidthScaleFactor;
                focusY *= bitmapHeightScaleFactor;

                Bitmap background = null;
                background = Bitmap.createBitmap(
                        mCapturedBitmap,
                        focusX,
                        focusY,
                        scaledWidth,
                        scaledHeight
                );

                Context context = CameraActivity.this;

                Glide.with(context)
                        .setDefaultRequestOptions(options)
                        .load(background)
                        .into(mStillshotImageView);

                showStillshotContainer();
            }
        });

    }

    private void showStillshotContainer(){
        mStillshotContainer.setVisibility(View.VISIBLE);
        mFlashContainer.setVisibility(View.INVISIBLE);
        mSwitchOrientationContainer.setVisibility(View.INVISIBLE);
        mCaptureBtnContainer.setVisibility(View.INVISIBLE);

        hideStatusBar();
        closeCamera();
    }



    /**
     *  WARNING!
     *  Can cause memory leaks! To prevent this the object is a global and CANCEL is being called
     *  in "OnPause".
     */
    private class BackgroundImageRotater extends AsyncTask<Void, Integer, Integer>{

        Activity mActivity;

        public BackgroundImageRotater(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: adjusting image for display...");
            File file = new File(mActivity.getExternalFilesDir(null), "temp_image.jpg");
            final Uri tempImageUri = Uri.fromFile(file);

            Bitmap bitmap = null;
            try {
                ExifInterface exif = new ExifInterface(tempImageUri.getPath());
                bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), tempImageUri);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                mCapturedBitmap = rotateBitmap(bitmap, orientation);
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){
                displayCapturedImage();
            }
            else{
                showSnackBar("Error preparing image", Snackbar.LENGTH_SHORT);
            }
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_TRANSPOSE:
                Log.d(TAG, "rotateBitmap: transpose");
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                Log.d(TAG, "rotateBitmap: normal.");
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                Log.d(TAG, "rotateBitmap: flip horizontal");
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                Log.d(TAG, "rotateBitmap: rotate 180");
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                Log.d(TAG, "rotateBitmap: rotate vertical");
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.d(TAG, "rotateBitmap: rotate 90");
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                Log.d(TAG, "rotateBitmap: transverse");
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                Log.d(TAG, "rotateBitmap: rotate 270");
                matrix.setRotate(-90);
                break;
        }
        try {
            if (isCameraFrontFacing()) {
                Log.d(TAG, "rotateBitmap: MIRRORING IMAGE.");
                matrix.postScale(-1.0f, 1.0f);
            }

            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();

            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The file we save the image into.
         */
        private final File mFile;

        /**
         * Original image that was captured
         */
        private Image mImage;

        private ICallback mCallback;

        private Bitmap mBitmap;

        ImageSaver(Bitmap bitmap, File file, ICallback callback) {
            mBitmap = bitmap;
            mFile = file;
            mCallback = callback;
        }

        ImageSaver(Image image, File file, ICallback callback) {
            mImage = image;
            mFile = file;
            mCallback = callback;
        }

        @Override
        public void run() {

            if (mImage != null) {
                ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                FileOutputStream output = null;
                try {
                    File file = new File(mFile, "temp_image.jpg");
                    output = new FileOutputStream(file);
                    output.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    mCallback.done(e);
                } finally {
                    mImage.close();
                    if (null != output) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mCallback.done(null);
                }
            } else if (mBitmap != null) {
                ByteArrayOutputStream stream = null;
                byte[] imageByteArray = null;
                stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                imageByteArray = stream.toByteArray();

                SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
                String format = s.format(new Date());
                File file = new File(mFile, "image_" + format + ".jpg");

                // save the mirrored byte array
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(file);
                    output.write(imageByteArray);
                } catch (IOException e) {
                    mCallback.done(e);
                    e.printStackTrace();
                } finally {
                    if (null != output) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mCallback.done(null);
                    }
                }
            }
        }
    }

    /**
    * Shows an error message dialog.
    */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.camera_request_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

    private void showSnackBar(final String text, final int length) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View view = findViewById(android.R.id.content).getRootView();
                    Snackbar.make(view, text, length).show();
                }
            });
    }

    private void showStatusBar() {

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void hideStatusBar() {

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setCameraFrontFacing() {
        Log.d(TAG, "setCameraFrontFacing: setting camera to front facing.");
        mCameraOrientation = CAMERA_POSITION_FRONT;
    }

    private void setCameraBackFacing() {
        Log.d(TAG, "setCameraBackFacing: setting camera to back facing.");
        mCameraOrientation = CAMERA_POSITION_BACK;
    }

    private void setFrontCameraId(String cameraId){
        CAMERA_POSITION_FRONT = cameraId;
    }


    private void setBackCameraId(String cameraId){
        CAMERA_POSITION_BACK = cameraId;
    }

    private boolean isCameraFrontFacing() {
        if(mCameraOrientation.equals(CAMERA_POSITION_FRONT)){
            return true;
        }
        return false;
    }

    private boolean isCameraBackFacing() {
        if(mCameraOrientation.equals(CAMERA_POSITION_BACK)){
            return true;
        }
        return false;
    }

    public String getBackCameraId(){
        return CAMERA_POSITION_BACK;
    }

    public String getFrontCameraId(){
        return CAMERA_POSITION_FRONT;
    }


}

