package com.survlogic.surveyhelper.model;

import android.app.Application;
import android.graphics.Bitmap;

public class AppUserClient extends Application {

    private FirestoreUser user = null;
    private Bitmap user_bitmap;

    public FirestoreUser getUser() { return user; }

    public void setUser(FirestoreUser user) {this.user = user;}

    public Bitmap getUserBitmap() {return user_bitmap;}

    public void setUserBitmap(Bitmap bitmap) {this.user_bitmap = bitmap;}

}
