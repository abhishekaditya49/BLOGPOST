package com.example.blogpost;

import android.util.Log;

import java.sql.Timestamp;

public class BlogPost extends BlogPostId {
    private String uid,image_url,desc;
    private com.google.firebase.Timestamp timestamp;

    public BlogPost() {
    }

    public BlogPost(String uid, String desc,String image_url, com.google.firebase.Timestamp timestamp) {
        this.uid = uid;
        this.desc = desc;
        this.image_url = image_url;
        this.timestamp = timestamp;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
