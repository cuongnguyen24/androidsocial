package com.utt.tt21.cc_modulelogin.profile.profileModel;

import java.util.List;

public class Profile {
    private String uid;
    private String nameProfile;

    private int imgProfile;
    private String emailProfile;
    private String desProfile;
    private List<Follower> followers;
    private List<Following> followings;

    public Profile(String uid, String nameProfile, int imgProfile, String emailProfile, String desProfile, List<Follower> followers, List<Following> followings) {
        this.uid = uid;
        this.nameProfile = nameProfile;
        this.imgProfile = imgProfile;
        this.emailProfile = emailProfile;
        this.desProfile = desProfile;
        this.followers = followers;
        this.followings = followings;
    }

    public Profile() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNameProfile() {
        return nameProfile;
    }

    public void setNameProfile(String nameProfile) {
        this.nameProfile = nameProfile;
    }

    public int getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(int imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getEmailProfile() {
        return emailProfile;
    }

    public void setEmailProfile(String emailProfile) {
        this.emailProfile = emailProfile;
    }

    public String getDesProfile() {
        return desProfile;
    }

    public void setDesProfile(String desProfile) {
        this.desProfile = desProfile;
    }

    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }

    public List<Following> getFollowings() {
        return followings;
    }

    public void setFollowings(List<Following> followings) {
        this.followings = followings;
    }

    public int countFollower()
    {
        return followers.size();
    }

    public int countFollowings()
    {
        return followings.size();
    }

    @Override
    public String toString() {
        return "ProfileModel{" +
                "uid='" + uid + '\'' +
                ", nameProfile='" + nameProfile + '\'' +
                ", imgProfile=" + imgProfile +
                ", emailProfile='" + emailProfile + '\'' +
                ", desProfile='" + desProfile + '\'' +
                ", followers=" + followers +
                ", followings=" + followings +
                '}';
    }
}
