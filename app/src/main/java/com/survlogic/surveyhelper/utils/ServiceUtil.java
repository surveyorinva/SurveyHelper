package com.survlogic.surveyhelper.utils;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.support.annotation.NonNull;

public class ServiceUtil {

    private ServiceUtil() {}

    public static boolean isRunning(@NonNull Context context, @NonNull Class<?> cls) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
