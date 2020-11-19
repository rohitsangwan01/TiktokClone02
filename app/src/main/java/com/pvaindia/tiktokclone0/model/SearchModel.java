package com.pvaindia.tiktokclone0.model;

public class SearchModel {
    String link,views;

    public SearchModel(String link, String views) {
        this.link = link;
        this.views = views;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }
}
