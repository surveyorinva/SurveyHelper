package com.survlogic.surveyhelper.activity.staffFeed.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

public class FeedDialogUtils {

    private static final String TAG = "FeedDialogUtils";

    public interface DialogListener{
        void isPopupOpen(boolean isOpen);
    }

    private Context mContext;
    private Activity mActivity;
    private DialogListener listener;

    public FeedDialogUtils(Context context, DialogListener listener) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.listener = listener;
    }


    /**
     * Creates a dialog Banner window that slides down from the top of the screen
     * @param backgroundImageUrl
     * @return
     */

    public PopupWindow createBannerFeedActionAnnouncementGame(String backgroundImageUrl){

        listener.isPopupOpen(true);

        final PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);

        View popupView = mActivity.getLayoutInflater().inflate(R.layout.staff_feed_banner_feed_announcement, null);
        ImageView ivImage = popupView.findViewById(R.id.announcement_image_background);
        final ProgressBar progress = popupView.findViewById(R.id.announcement_image_background_progress);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(backgroundImageUrl, ivImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(progress !=null){
                    progress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(progress !=null){
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(progress !=null){
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(progress !=null){
                    progress.setVisibility(View.GONE);
                }
            }
        });

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height,true);

        popupWindow.setAnimationStyle(R.style.popup_window_animation_slide_down);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setTouchable(true);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);

        Button btnDismissForSession = popupView.findViewById(R.id.announcement_button_action_dont_show);
        btnDismissForSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceLoader.setAnnouncementShowReward(false,true);
                popupWindow.dismiss();
                listener.isPopupOpen(false);

            }
        });


        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    listener.isPopupOpen(false);
                    return true;
                }

                return false;
            }

        });

        popupWindow.setElevation(10);

        return popupWindow;
    }


    public PopupWindow createPopUpFeedActionItems(){

        listener.isPopupOpen(true);

        View popupView = mActivity.getLayoutInflater().inflate(R.layout.staff_feed_popup_feed_add, null);

        float density = mActivity.getResources().getDisplayMetrics().density;

        int width = (int) density * 240;
        int height = (int) density * 285;

        int marginBottom = (int) density * 50;
        int marginEnd = (int) density * 50;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height,true);

        popupWindow.setAnimationStyle(R.style.popup_window_animation_explode);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setTouchable(true);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    listener.isPopupOpen(false);
                    return true;
                }

                return false;
            }

        });

        popupWindow.setElevation(10);

        return popupWindow;
    }
}
