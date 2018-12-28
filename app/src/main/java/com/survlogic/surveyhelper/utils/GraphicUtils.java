package com.survlogic.surveyhelper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GraphicUtils {

    private static final String TAG = "GraphicUtils";

    /**
     * Converts the passed in drawable to Bitmap representation
     *
     * @throws NullPointerException If the parameter drawable is null.
     **/
    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable == null) {
            throw new NullPointerException("Drawable to convert should NOT be null");
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        if (drawable.getIntrinsicWidth() <= 0 && drawable.getIntrinsicHeight() <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Converts the given bitmap to {@linkplain java.io.InputStream}.
     *
     * @throws NullPointerException If the parameter bitmap is null.
     **/
    public static InputStream bitmapToInputStream(Bitmap bitmap) throws NullPointerException {

        if (bitmap == null) {
            throw new NullPointerException("Bitmap cannot be null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream inputstream = new ByteArrayInputStream(baos.toByteArray());

        return inputstream;
    }

    /**
     * Gives the device independent constant which can be used for scaling images, manipulating view
     * sizes and changing dimension and display pixels etc.
     **/
    public static float getDensityMultiplier(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A int value to represent dp equivalent to px value
     */
    public static int getDip(int px, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    /**
     * Converts a given bitmap to byte array
     */
    public static byte[] toBytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Resizes an image to the given width and height parameters Prefer using
     *
     * @param sourceBitmap Bitmap to be resized
     * @param newWidth     Width of resized bitmap
     * @param newHeight    Height of the resized bitmap
     */
    public static Bitmap resizeImage(Bitmap sourceBitmap, int newWidth, int newHeight, boolean filter) {
        // TODO: move this method to ImageUtils
        if (sourceBitmap == null) {
            throw new NullPointerException("Bitmap to be resized cannot be null");
        }

        Bitmap resized = null;

        if (sourceBitmap.getWidth() < sourceBitmap.getHeight()) {
            // image is portrait
            resized = Bitmap.createScaledBitmap(sourceBitmap, newHeight, newWidth, true);
        } else {
            // image is landscape
            resized = Bitmap.createScaledBitmap(sourceBitmap, newWidth, newHeight, true);
        }

        resized = Bitmap.createScaledBitmap(sourceBitmap, newWidth, newHeight, true);

        return resized;
    }

    /**
     * <br/>
     * <br/>
     *
     * @param compressionFactor Powers of 2 are often faster/easier for the decoder to honor
     */
    public static Bitmap compressImage(Bitmap sourceBitmap, int compressionFactor) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inSampleSize = compressionFactor;

        if (Build.VERSION.SDK_INT >= 10) {
            opts.inPreferQualityOverSpeed = true;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);

        return image;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * Provide the height to which the sourceImage is to be resized. This method will calculate the
     * resultant height. Use scaleDownBitmap from {@link DialogUtils} wherever possible
     */
    public static Bitmap resizeImageByHeight(int height, Bitmap sourceImage) {
        // TODO: move this method to ImageUtils
        int widthO = 0; // original width
        int heightO = 0; // original height
        int widthNew = 0;
        int heightNew = 0;

        widthO = sourceImage.getWidth();
        heightO = sourceImage.getHeight();
        heightNew = height;

        // Maintain the aspect ratio
        // of the original banner image.
        widthNew = (heightNew * widthO) / heightO;

        return Bitmap.createScaledBitmap(sourceImage, widthNew, heightNew, true);
    }


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
