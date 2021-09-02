package com.example.firebasetesting;

import com.bumptech.glide.Glide;

public class UserInfo {
    public String ID, name, sex, profileImageUrl, description, jobTitle;
    public UserInfo() {}
    public UserInfo(String ID, String name, String sex, String profileImageUrl){
        this.ID = ID;
        this.name = name;
        this.sex = sex;
        this.profileImageUrl = profileImageUrl;
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
