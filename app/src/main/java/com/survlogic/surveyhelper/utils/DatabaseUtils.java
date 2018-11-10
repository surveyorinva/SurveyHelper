package com.survlogic.surveyhelper.utils;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DatabaseUtils {

    private static final String TAG = "DatabaseUtils";

    /**
     * Checks if the DB with the given name is present on the device.
     *
     * @param packageName
     * @param dbName
     * @return
     */
    public static boolean isDatabasePresent(String packageName, String dbName) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/" + packageName + "/databases/" + dbName, null, SQLiteDatabase.OPEN_READONLY);
            sqLiteDatabase.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            e.printStackTrace();
            Log.e(TAG, "The database does not exist." + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception " + e.getMessage());
        }

        return (sqLiteDatabase != null);
    }



}
