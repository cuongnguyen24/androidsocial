package com.utt.tt21.cc_modulelogin.search;

public class Account {
    private String userId;// Thêm thuộc tính userId
    private String nameProfile;
    private String imgProfile;
//    private int followers;
 //   private boolean following;
    private String desProfile;
    public Account() {
        // Để Firebase sử dụng phản xạ
    }

    public Account(String userId, String nameProfile, String imgProfile,String desProfile) {
        this.userId = userId;
        this.nameProfile = nameProfile;
        this.imgProfile = imgProfile;
//        this.followers = followers;
   //     this.following = following;
        this.desProfile = desProfile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getNameProfile() {
        return nameProfile;
    }

    public void setNameProfile(String nameProfile) {
        this.nameProfile = nameProfile;
    }

//    public void setFollowing(boolean following) {
//        this.following = following;
//    }


    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getDesProfile() {
        return desProfile;
    }

    public void setDesProfile(String desProfile) {
        this.desProfile = desProfile;
    }
//    public void setFollowers(int followers) {
//        this.followers = followers;
//    }


//    public String getFollowers() {
//        return followers +" followers";
//    }

//    public boolean isFollowing() {
//        return following;
//    }
}

