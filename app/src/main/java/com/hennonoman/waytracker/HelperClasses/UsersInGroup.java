package com.hennonoman.waytracker.HelperClasses;

import java.util.ArrayList;

public class UsersInGroup {


    ArrayList<String> phone;
    public  UsersInGroup( )
    {


    }

    public  UsersInGroup(ArrayList<String> phone )
    {
        this.phone = phone;

    }

    public void setPhone(ArrayList<String> phone) {
        this.phone = phone;
    }

    public ArrayList<String> getPhone() {
        return phone;
    }
}
