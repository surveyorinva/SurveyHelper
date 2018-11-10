package com.survlogic.surveyhelper.database.Users_Access;

import com.survlogic.surveyhelper.model.FirestoreAppAccessKeys;

import java.util.ArrayList;

public interface FirestoreDatabaseUserAccessListener {

    void returnFirestoreUserAccess(FirestoreAppAccessKeys key);

    void returnAllFirestoreUserAccess(ArrayList<FirestoreAppAccessKeys> mListKeys);

    void returnFirestoreUserAccessGetError(boolean isError);


}
