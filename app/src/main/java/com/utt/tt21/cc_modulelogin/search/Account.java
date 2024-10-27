package com.utt.tt21.cc_modulelogin.search;

public class Account {
    private String userId;
    private String nameProfile;
    private String imgProfile;
    private String desProfile;
    private boolean Dantheodoi;
    public Account() {
        // Để Firebase sử dụng phản xạ
    }


    public Account(String userId, String nameProfile, String imgProfile, String desProfile, boolean dangtheodoi) {
        this.userId = userId;
        this.nameProfile = nameProfile;
        this.imgProfile = imgProfile;
        this.desProfile = desProfile;
        this.Dantheodoi = dangtheodoi;
    }

    public boolean isDantheodoi() {
        return Dantheodoi;
    }

    public void setDantheodoi(boolean dantheodoi) {
        Dantheodoi = dantheodoi;
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

}

