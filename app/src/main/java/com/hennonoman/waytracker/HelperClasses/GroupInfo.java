package com.hennonoman.waytracker.HelperClasses;

public class GroupInfo {



    public String groupId;
    public String title;
    public String admin;
    public String latit;
    public String longi;



    public GroupInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public GroupInfo(String groupId, String title,String admin, String latit,String longi) {
        this.groupId = groupId;
        this.title = title;
        this.admin = admin;
        this.latit = latit;
        this.longi = longi;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setLatit(String latit) {
        this.latit = latit;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTitle() {
        return title;
    }

    public String isAdmin() {
        return admin;
    }

    public String getLatit() {
        return latit;
    }

    public String getLongi() {
        return longi;
    }
}
