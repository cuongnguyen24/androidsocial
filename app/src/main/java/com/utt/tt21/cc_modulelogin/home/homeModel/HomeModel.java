package com.utt.tt21.cc_modulelogin.home.homeModel;

public class HomeModel {
    private String userName;
    private String timestamp;
    private String profileImage;
    private String postImage;
    private String uid;

    private String des;

    private int likeCount, cmtCount, reupCount, postCount;

    public HomeModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCmtCount() {
        return cmtCount;
    }

    public void setCmtCount(int cmtCount) {
        this.cmtCount = cmtCount;
    }

    public int getReupCount() {
        return reupCount;
    }

    public void setReupCount(int reupCount) {
        this.reupCount = reupCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public HomeModel(String userName, String timestamp, String profileImage, String postImage, int likeCount, int cmtCount, int reupCount, int postCount, String des, String uid) {
        this.userName = userName;
        this.timestamp = timestamp;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.likeCount = likeCount;
        this.cmtCount = cmtCount;
        this.reupCount = reupCount;
        this.postCount = postCount;
        this.des = des;
        this.uid = uid;
    }
}
