package com.f.closedeal.Models;

public class Notifications {

    public String userid;
    public String postid;
    public String text;
    private boolean ispost;

    public Notifications(String userid, String postid, String text, boolean ispost) {
        this.userid = userid;
        this.postid = postid;
        this.text = text;
        this.ispost = ispost;
    }

    public Notifications() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}
