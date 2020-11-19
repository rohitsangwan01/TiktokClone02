package com.pvaindia.tiktokclone0.model;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class HomeModel {
    String uname,tags,views,likes,liked,shares,link,uid,id,comments,description;
    public HomeModel(String uname, String tags, String views, String likes, String liked, String shares, String link, String uid, String id,String comments,String description) {
        this.uname = uname;
        this.tags = tags;
        this.views = views;
        this.likes = likes;
        this.liked = liked;
        this.shares = shares;
        this.link = link;
        this.uid = uid;
        this.id = id;
        this.comments=comments;
        this.description=description;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
