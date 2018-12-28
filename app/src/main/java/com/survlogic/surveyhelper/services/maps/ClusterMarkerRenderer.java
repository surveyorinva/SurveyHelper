package com.survlogic.surveyhelper.services.maps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.model.AppUserClient;

public class ClusterMarkerRenderer extends DefaultClusterRenderer<ClusterMarkerItem> {
    private static final String TAG = "ClusterMarkerRenderer";
    private final IconGenerator mIconGenerator;
    private final ImageView mImageView;
    private final int markerWidth;
    private final int markerHeight;

    private Activity mActivity;
    private Bitmap mProfileBitmap;

    public ClusterMarkerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarkerItem> clusterManager) {
        super(context, map, clusterManager);

        mActivity = (Activity) context;

        mIconGenerator = new IconGenerator(context.getApplicationContext());
        mImageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);

        mImageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth,markerHeight));

        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        mImageView.setPadding(padding,padding,padding,padding);

        mIconGenerator.setContentView(mImageView);

    }

    @Override
    protected void onBeforeClusterItemRendered(final ClusterMarkerItem item, final MarkerOptions markerOptions) {
        Log.d(TAG, "to_delete: onBeforeClusterItemRendered: Started");
        String profileUrl = item.getUser().getProfile_pic_url();

        Log.d(TAG, "to_delete: onBeforeClusterItemRendered> Bitmap URL: " + profileUrl);

        Bitmap loadedImage = ((AppUserClient) (mActivity.getApplicationContext())).getUserBitmap();
        mImageView.setImageBitmap(loadedImage);
        Bitmap icon = mIconGenerator.makeIcon();

        markerOptions
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .title(item.getTitle());

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarkerItem> cluster) {
        return false;
    }
}
