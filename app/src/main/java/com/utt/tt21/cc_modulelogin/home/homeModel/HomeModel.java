package com.utt.tt21.cc_modulelogin.home.homeModel;

import java.util.List;

public class HomeModel {
    private String userName;
    private String timestamp;
    private String profileImage;
    private List<String> postImage;
    private String uid;

    private String content;

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

    public List<String> getPostImage() {
        return postImage;
    }

    public void setPostImage(List<String> postImage) {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HomeModel(String userName, String timestamp, String profileImage, List<String> postImage, int likeCount, int cmtCount, int reupCount, int postCount, String content, String uid) {
        this.userName = userName;
        this.timestamp = timestamp;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.likeCount = likeCount;
        this.cmtCount = cmtCount;
        this.reupCount = reupCount;
        this.postCount = postCount;
        this.content = content;
        this.uid = uid;
    }
}
