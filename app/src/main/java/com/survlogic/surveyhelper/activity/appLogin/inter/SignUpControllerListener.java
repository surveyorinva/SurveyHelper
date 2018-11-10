package com.survlogic.surveyhelper.activity.appLogin.inter;

public interface SignUpControllerListener {

    void refreshUI();

    void closeActivity();

    void setUserNameFirstErrorNull(boolean isNull);

    void setUserNameLastErrorNull(boolean isNull);

    void setUserEmailErrorNull(boolean isNull);

    void setPasswordErrorNull(boolean isNull);

    void setAccessKeyErrorNull(boolean isNull);

    void setAccessKeyErrorWrongKey(boolean isNull);

    void onFailureException(String errorCode);

    void setViewsErrorAuthenticationFail(boolean isFail);

}
