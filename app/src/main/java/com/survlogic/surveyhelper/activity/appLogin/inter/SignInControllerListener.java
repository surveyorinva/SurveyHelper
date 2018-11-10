package com.survlogic.surveyhelper.activity.appLogin.inter;

public interface SignInControllerListener {

    void refreshUI();

    void closeActivity();

    void setUserEmailErrorNull(boolean isNull);

    void setPasswordErrorNull(boolean isNull);

    void setViewsErrorAuthenticationFail(boolean isFail);


    void onFailureException(String errorCode);

}
