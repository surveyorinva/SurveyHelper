package com.survlogic.surveyhelper.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

public class FileUtils {

    private static final String TAG = "FileUtils";
    private Context mContext;

    public FileUtils(Context context) {
        this.mContext = context;
    }

    private boolean checkSDCardStatus() {
        String SDCardStatus = Environment.getExternalStorageState();

        // MEDIA_UNKNOWN: unrecognized SD card
        // MEDIA_REMOVED: no SD card at all
        // MEDIA_UNMOUNTED: SD card exist but not mounted, not available in Android 4.0+
        // MEDIA_CHECKING: preparing SD card, e.g. powered on and booting
        // MEDIA_MOUNTED: mounted and ready to use
        // MEDIA_MOUNTED_READ_ONLY
        switch (SDCardStatus) {
            case Environment.MEDIA_MOUNTED:
                return true;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                return false;
            default:
                return false;
        }
    }

    private boolean writeDataToPath(File path, String fileName, String data) {
        boolean results = false;

        File targetFilePath = new File(path, fileName);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(targetFilePath);
            fos.write(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    results =  true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }

        return results;
    }

    /**
     * Returns the URL without the query string
     **/
    public static URL getPathFromUrl(URL url) {

        if (url != null) {
            String urlStr = url.toString();
            String urlWithoutQueryString = urlStr.split("\\?")[0];
            try {
                return new URL(urlWithoutQueryString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Checks if the url is valid
     */
    public static boolean isValidURL(String url) {
        URL urlObj;

        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            urlObj.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }


    /**
     * Get the file path from the Media Content Uri for video, audio or images.
     *
     * @param mediaContentUri Media content Uri.
     **/
    public static String getPathForMediaUri(Context context, Uri mediaContentUri) {

        Cursor cur = null;
        String path = null;

        try {
            String[] projection = {MediaStore.MediaColumns.DATA};
            cur = context.getContentResolver().query(mediaContentUri, projection, null, null, null);

            if (cur != null && cur.getCount() != 0) {
                cur.moveToFirst();
                path = cur.getString(cur.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }

            // Log.v( TAG, "#getRealPathFromURI Path: " + path );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed())
                cur.close();
        }

        return path;
    }

    /**
     * Gets the data storage directory(pictures dir) for the device. If the external storage is not
     * available, this returns the reserved application data storage directory. SD Card storage will
     * be preferred over internal storage.
     *
     * @param dirName if the directory name is specified, it is created inside the DIRECTORY_PICTURES
     *                directory.
     * @return Data storage directory on the device. Maybe be a directory on SD Card or internal
     * storage of the device.
     **/
    public static File getStorageDirectory(Context ctx, String dirName) {

        if (TextUtils.isEmpty(dirName)) {
            dirName = "atemp";
        }

        File f = null;

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + dirName);
        } else {
            // media is removed, unmounted etc
            // Store image in
            // /data/data/<package-name>/cache/atemp/photograph.jpeg
            f = new File(ctx.getCacheDir() + "/" + dirName);
        }

        if (!f.exists()) {
            f.mkdirs();
        }

        return f;
    }

    /**
     * Given a file name, this method creates a {@link java.io.File} on best chosen device storage and
     * returns the file object. You can get the file path using {@link java.io.File#getAbsolutePath()}
     **/
    public static File getFile(Context ctx, String fileName) {
        File dir = getStorageDirectory(ctx, null);
        File f = new File(dir, fileName);
        return f;
    }

    /**
     * Inserts an image into {@link android.provider.MediaStore.Images.Media} content provider of the device.
     *
     * @return The media content Uri to the newly created image, or null if the image failed to be
     * stored for any reason.
     **/
    public static String writeImageToMedia(Context ctx, Bitmap image, String title, String description) {
        // TODO: move to MediaUtils
        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        return MediaStore.Images.Media.insertImage(ctx.getContentResolver(), image, title, description);
    }

    /**
     * Convert {@linkplain java.io.InputStream} to byte array.
     *
     * @throws NullPointerException If input parameter {@link java.io.InputStream} is null
     **/
    public static byte[] readStreamToBytes(InputStream inputStream) {

        if (inputStream == null) {
            throw new NullPointerException("InputStream is null");
        }

        byte[] bytesData = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            bytesData = buffer.toByteArray();

            // Log.d( TAG, "#readStream data: " + data );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (reader != null) {
                try {
                    reader.close();

                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }    // finally

        return bytesData;
    }

    /**
     * Creates the uri to a file located on external storage or application internal storage.
     */
    public static Uri createUri(Context ctx) {
        File root = getStorageDirectory(ctx, null);
        root.mkdirs();
        File file = new File(root, Long.toString(new Date().getTime()));
        Uri uri = Uri.fromFile(file);

        return uri;
    }

    /**
     * Checks if given url is a relative path.
     *
     * @param url
     * @return false if parameter url is null or false
     */
    public static final boolean isRelativeUrl(String url) {

        if (TextUtils.isEmpty(url)) {
            return false;
        }

        Uri uri = Uri.parse(url);

        return uri.getScheme() == null;
    }

    /**
     * Checks if the parameter {@link android.net.Uri} is a content uri.
     **/
    public static boolean isContentUri(Uri uri) {
        if (!uri.toString().contains("content://")) {
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    /**
     * Gets the extension of a file.
     */
    public static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        return ext;
    }


}
