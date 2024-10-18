package com.utt.tt21.cc_modulelogin.profile.profileModel;

public class Quang {
    private String name, nickname, uid;

    public Quang() {
    }

    public Quang(String name, String nickname, String uid) {
        this.name = name;
        this.nickname = nickname;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Quang{" +
                "name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
