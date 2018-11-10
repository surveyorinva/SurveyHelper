package com.survlogic.surveyhelper.database.Users;

import com.survlogic.surveyhelper.model.FirestoreUser;

public interface FirestoreDatabaseUserListener {

    void returnFirestoreUser(FirestoreUser user);

    void returnFirestoreUserGetError(boolean isError);

    void updateUserProfileSuccess(FirestoreUser user);

    void updateUserProfileGetError(boolean isError);


}
