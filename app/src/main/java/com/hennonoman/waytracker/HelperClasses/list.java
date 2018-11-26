package com.hennonoman.waytracker.HelperClasses;

/**
 * Created by Seotoolzz on 13/6/17.
 */

public class list {


    private String title;

    private String idGroup;

    private int imageId;

    public list(String title, String idGroup , int imageId) {
        this.title = title;
        this.idGroup = idGroup;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public String getIdGroup() {
        return idGroup;
    }

}
