package com.example.blogpost;


import com.google.firebase.Timestamp;

public class Comments {
    private String message;
    private String userID;
    private Timestamp timestamp;//util Date

    public Comments(String message, Timestamp timestamp,String userID) {
        this.message = message;
        this.userID = userID;
        this.timestamp = timestamp;
    }

    public Comments() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

