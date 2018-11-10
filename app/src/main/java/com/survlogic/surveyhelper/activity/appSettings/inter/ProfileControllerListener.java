package com.survlogic.surveyhelper.activity.appSettings.inter;

import com.survlogic.surveyhelper.model.FirestoreAppAccessKeys;
import com.survlogic.surveyhelper.model.FirestoreUser;

public interface ProfileControllerListener {

    void returnCurrentUser(FirestoreUser currentUser);

    void returnCurrentUserAccess(FirestoreAppAccessKeys currentUserAccess);

    void returnErrorNoUserLoggedIn();

    void returnErrorGettingAccessKeys();

}
