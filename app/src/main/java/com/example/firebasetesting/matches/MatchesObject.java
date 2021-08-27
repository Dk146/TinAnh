package com.example.firebasetesting.matches;

public class MatchesObject {
    public String ID, name, profileImageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public MatchesObject() {}

    public MatchesObject(String ID, String name, String profileImageUrl) {
        this.ID = ID;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
}
