package com.utt.tt21.cc_modulelogin.messenger;

public class MessengerUserModel {
    private String myId, userUid, name, imageUrl;

    public MessengerUserModel() {
    }

    public MessengerUserModel(String myId, String userUid, String name, String imageUrl) {
        this.myId = myId;
        this.userUid = userUid;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
