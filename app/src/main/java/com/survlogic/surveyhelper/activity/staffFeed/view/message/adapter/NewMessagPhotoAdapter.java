package com.survlogic.surveyhelper.activity.staffFeed.view.message.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.utils.GraphicDrawingUtils;
import com.survlogic.surveyhelper.views.SquareImageView;

import java.util.ArrayList;

public class NewMessagPhotoAdapter extends ArrayAdapter {

    private static final String TAG = "NewMessagPhotoAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;

    private ArrayList<String> mLocalPhotos;

    private int currentCount = 0, imageCap = 0, arrayListCount = 0;
    private static final int limitCount = 4, limitArrayCount = 3;
    private boolean overLimit = false;

    public NewMessagPhotoAdapter(Context context, int layoutResource, ArrayList<String> images) {
        super(context, layoutResource, images);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;

        this.layoutResource = layoutResource;
        this.mLocalPhotos = images;
    }

    private static class ViewHolder {
        SquareImageView image;
        ProgressBar mProgressBar;
    }


    @Override
    public int getCount() {

        arrayListCount = mLocalPhotos.size(); //counts the total number of elements from the arrayList.

        if (arrayListCount <= 4) {
            imageCap = arrayListCount;

        } else {
            imageCap = limitCount;
            overLimit = true;
        }
        return imageCap;//returns the total count to adapter

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        View row = convertView;

        currentCount = position;

        if (row == null) {
            row = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.image = row.findViewById(R.id.gridImageView);
            holder.mProgressBar = row.findViewById(R.id.gridImageProgressBar);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader imageLoader = ImageLoader.getInstance();

        String url = mLocalPhotos.get(position);
        Log.d(TAG, "Staring Switch: Limit: " + limitArrayCount + " at position " + position);

        switch (position) {
            case limitArrayCount:
                imageLoader.loadImage(url, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        String description = "+ " + String.valueOf(arrayListCount - limitCount + 1);

                        GraphicDrawingUtils graphicDrawingUtils = new GraphicDrawingUtils(mContext);
                        Bitmap mImageWithWatermark = graphicDrawingUtils.setFullScreenWatermark(loadedImage, description, true);
                        holder.image.setImageBitmap(mImageWithWatermark);
                        holder.mProgressBar.setVisibility(View.GONE);
                    }
                });

                break;

            default:

                imageLoader.displayImage(url, holder.image, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        if (holder.mProgressBar != null) {
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (holder.mProgressBar != null) {
                            holder.mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (holder.mProgressBar != null) {
                            holder.mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        if (holder.mProgressBar != null) {
                            holder.mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
                break;
        }


        return row;
    }

    public void swapItems(ArrayList<String> items) {
        this.mLocalPhotos = items;
        notifyDataSetChanged();
    }
}
