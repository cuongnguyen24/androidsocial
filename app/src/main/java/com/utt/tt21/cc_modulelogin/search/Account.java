package com.utt.tt21.cc_modulelogin.search;

public class Account {
    private String userId; // Thêm thuộc tính userId
    private String name;
    private int followers;
    private boolean following;
    private String nickname;
    public Account() {
        // Để Firebase sử dụng phản xạ
    }

    public Account(String userId, String name, int followers, boolean following, String nickname) {
        this.userId = userId;
        this.name = name;
        this.followers = followers;
        this.following = following;
        this.nickname = nickname;
    }

    public String getUserId() { // Thêm phương thức getter cho userId
        return userId;
    }

    public void setUserId(String userId) { // Thêm phương thức setter cho userId
        this.userId = userId;
    }
    public String getNickname() { // Thêm phương thức getter cho userId
        return nickname;
    }

    public void setNickname(String nickname) { // Thêm phương thức setter cho userId
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public String getFollowers() {
        return followers +" followers";
    }

    public boolean isFollowing() {
        return following;
    }
}
