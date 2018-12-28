package com.survlogic.surveyhelper.services.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.survlogic.surveyhelper.model.FirestoreUser;

public class ClusterMarkerItem implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private FirestoreUser user;

    public ClusterMarkerItem() {
    }

    public ClusterMarkerItem(LatLng position, String title, String snippet, FirestoreUser user) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.user = user;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public FirestoreUser getUser() {
        return user;
    }

    public void setUser(FirestoreUser user) {
        this.user = user;
    }
}
