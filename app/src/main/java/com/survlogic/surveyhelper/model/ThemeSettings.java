package com.survlogic.surveyhelper.model;

import android.util.Log;

public class ThemeSettings {
    private static final String TAG = "ThemeSettings";

    private String staffCompanyPage0Top, staffCompanyPage0Bottom;
    private String staffCompanyPage1Top, staffCompanyPage1Bottom;
    private String staffCompanyPage2Top, staffCompanyPage2Bottom;

    private ThemeSettings mTheme;
    
    public ThemeSettings() {}

    
    public ThemeSettings(ThemeSettings theme){
        this.mTheme = theme;

        setStaffCompanyPage0Bottom(theme.staffCompanyPage0Bottom);
        setStaffCompanyPage0Top(theme.staffCompanyPage0Top);
        
        setStaffCompanyPage1Bottom(theme.staffCompanyPage1Bottom);
        setStaffCompanyPage1Top(theme.staffCompanyPage1Top);

        setStaffCompanyPage2Bottom(theme.staffCompanyPage2Bottom);
        setStaffCompanyPage2Top(theme.staffCompanyPage2Top);

    }
    
    public String getStaffCompanyPage0Top() {
        return staffCompanyPage0Top;
    }

    public void setStaffCompanyPage0Top(String staffCompanyPage0Top) {
        this.staffCompanyPage0Top = staffCompanyPage0Top;
    }

    public String getStaffCompanyPage0Bottom() {
        return staffCompanyPage0Bottom;
    }

    public void setStaffCompanyPage0Bottom(String staffCompanyPage0Bottom) {
        this.staffCompanyPage0Bottom = staffCompanyPage0Bottom;
    }

    public String getStaffCompanyPage1Top() {
        return staffCompanyPage1Top;
    }

    public void setStaffCompanyPage1Top(String staffCompanyPage1Top) {
        this.staffCompanyPage1Top = staffCompanyPage1Top;
    }

    public String getStaffCompanyPage1Bottom() {
        return staffCompanyPage1Bottom;
    }

    public void setStaffCompanyPage1Bottom(String staffCompanyPage1Bottom) {
        this.staffCompanyPage1Bottom = staffCompanyPage1Bottom;
    }

    public String getStaffCompanyPage2Top() {
        return staffCompanyPage2Top;
    }

    public void setStaffCompanyPage2Top(String staffCompanyPage2Top) {
        this.staffCompanyPage2Top = staffCompanyPage2Top;
    }

    public String getStaffCompanyPage2Bottom() {
        return staffCompanyPage2Bottom;
    }

    public void setStaffCompanyPage2Bottom(String staffCompanyPage2Bottom) {
        this.staffCompanyPage2Bottom = staffCompanyPage2Bottom;
    }
}
