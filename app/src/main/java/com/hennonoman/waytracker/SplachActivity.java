package com.hennonoman.waytracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.quickblox.auth.session.QBSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class SplachActivity extends AppCompatActivity {

    static final String APP_ID = "74508" ;


    SharedPreferences.Editor myEditor;
    SharedPreferences mySharedPreferences;
     boolean status;
     long time=5000;
    FirebaseFirestore firestoer;
    public static ArrayList<String> allUsers;


    public static HashMap<String,String> users_info;

    public void setLocale()
    {
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


        allUsers= new ArrayList<>();

        users_info = new HashMap();
        firestoer = FirebaseFirestore.getInstance();
        ReadSingleContact();
        // for full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_splach);



        //set language
        setLocale();


        mySharedPreferences = getSharedPreferences("signinstatus", Context.MODE_PRIVATE);
        myEditor =mySharedPreferences.edit();


        nextActivity();







    }

    @Override
    protected void onStart() {
        super.onStart();

        //check sign in or not
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


    private void ReadSingleContact() {


        firestoer.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("IDs", document.getId() + " => " + document.getData());
                                allUsers.add(document.getId());
                            }
                        } else {
                            Log.d("IDsErr", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }



}
