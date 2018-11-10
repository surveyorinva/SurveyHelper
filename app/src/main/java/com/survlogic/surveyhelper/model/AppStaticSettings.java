package com.survlogic.surveyhelper.model;

public class AppStaticSettings {

    private boolean isPromo;
    private String promoUrl, promoHeader;
    private boolean isPromoActive = true;

    private AppStaticSettings mStaticSettings;

    public AppStaticSettings() {
    }


    public AppStaticSettings(AppStaticSettings staticSettings){
        this.mStaticSettings = staticSettings;

        setPromo(staticSettings.isPromo);
        setPromoActive(staticSettings.isPromoActive);
        setPromoHeader(staticSettings.promoHeader);
        setPromoUrl(staticSettings.promoUrl);

    }

    public boolean isPromo() {
        return isPromo;
    }

    public void setPromo(boolean promo) {
        isPromo = promo;
    }

    public String getPromoUrl() {
        return promoUrl;
    }

    public void setPromoUrl(String promoUrl) {
        this.promoUrl = promoUrl;
    }

    public String getPromoHeader() {
        return promoHeader;
    }

    public void setPromoHeader(String promoHeader) {
        this.promoHeader = promoHeader;
    }

    public boolean isPromoActive() {
        return isPromoActive;
    }

    public void setPromoActive(boolean promoActive) {
        isPromoActive = promoActive;
    }
}
