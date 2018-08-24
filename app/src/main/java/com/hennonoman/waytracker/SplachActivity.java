package com.hennonoman.waytracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Locale;

public class SplachActivity extends AppCompatActivity {
    SharedPreferences.Editor myEditor;
    SharedPreferences mySharedPreferences;
     boolean status;
     long time=2500;

    public void setLocale() {
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splach);

        setLocale();
        mySharedPreferences = getSharedPreferences("signinstatus", Context.MODE_PRIVATE);
        myEditor =mySharedPreferences.edit();


        nextActivity();







    }

    @Override
    protected void onStart() {
        super.onStart();

       status=mySharedPreferences.getBoolean("checksignin", false);

    }


    public void nextActivity()
    {
        CountDownTimer count= new CountDownTimer(time,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent i;

                if(status)
                {
                    i = new Intent(SplachActivity.this, HomeActivity.class);
                }
                else
                    i = new Intent(SplachActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        }.start();

    }



}
