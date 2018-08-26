package com.hennonoman.waytracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.hennonoman.waytracker.fragments_java.MapviewFragment;
import com.hennonoman.waytracker.fragments_java.ResetpassFragment;

public class HelpFragment extends AppCompatActivity {
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction ft;


    /////




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_fragment);




        fragment = new ResetpassFragment();
        fragmentManager = getSupportFragmentManager();
        ft=fragmentManager.beginTransaction();
        ft.replace(R.id.contentframe,fragment);
        ft.commit();










    }






    }
