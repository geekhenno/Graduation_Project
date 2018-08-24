package com.hennonoman.waytracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hennonoman.waytracker.fragments_java.MapviewFragment;
import com.hennonoman.waytracker.fragments_java.Profile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements Profile.ProfileInteractionListener , MapviewFragment.MapviewInteractionListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
     NavigationView navigationView;
    Fragment fragment;
    FragmentManager fragmentManager;
    FirebaseFirestore firestoer;
    FirebaseStorage storage;
    private StorageReference mStorage;
    public static StorageReference gsReference;
    //////
    SharedPreferences.Editor myEditor;
    SharedPreferences mySharedPreferences;
    TextView username;
    public static ImageView img;
    ///
    FragmentTransaction ft;
    ///
    public static View headerView;
    ///
    public static ImageView navImage;
    public static  String pathProfile,usernameProfile;
    public static String userphone;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.create_group:


                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return true;
    }


    @Override
    public void onBackPressed() {


        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        toolbar = findViewById(R.id.mapToolBar);
        setSupportActionBar(toolbar);

        fragment = new MapviewFragment();

         ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame,fragment);
        ft.commit();

        fragmentManager = getSupportFragmentManager();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mySharedPreferences = getSharedPreferences("signinstatus", Context.MODE_PRIVATE);
        myEditor = mySharedPreferences.edit();
        ButterKnife.bind(this);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_viewmap);
        navigationView.setItemIconTintList(null);

        firestoer = FirebaseFirestore.getInstance();
        userphone = mySharedPreferences.getString("userphone","");

        readUserName();
        readuserImage();




        //change name of header
         headerView = navigationView.getHeaderView(0);
        TextView navUsernumber =  headerView.findViewById(R.id.usernumber);
        TextView navUsername =  headerView.findViewById(R.id.username);
        navUsernumber.setText(userphone);
        navUsername.setText("");

        storage = FirebaseStorage.getInstance();
        mStorage =storage.getReference();






        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.main_home);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                fragment= fragmentManager.findFragmentById(R.id.content_frame);
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                switch (menuItem.getItemId())
                {

                    case R.id.main_home:
                            fragment = new MapviewFragment();
                        if(fragment!=null)
                        {

                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame,fragment);
                            ft.commit();

                        }


                        break;

                    case R.id.user_profile:

                            fragment = new Profile();
                        if(fragment!=null)
                        {

                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame,fragment);
                            ft.commit();

                        }


                        break;

                    case R.id.user_groups:

                        break;

                    case R.id.user_favorite:


                        break;



                    case R.id.user_settings:
                        break;


                    case R.id.contactus:
                        break;

                    case R.id.aboutus:

                        aboutDialog();
                        break;


                    case R.id.signout:


                        signoutDialog();

                        break;




                    //.add(R.id.content_frame,fragment).commit();


                }



                drawerLayout.closeDrawer(GravityCompat.START);

                return true;

            }
        });









    }

    @Override
    public void onProfileFragmentInteraction(Uri uri) {

    }





    void aboutDialog(){

        AlertDialog dialog = new AlertDialog.Builder(this).create();

        dialog.setTitle("Way Tracker");
        dialog.setIcon(R.drawable.logo1);
        dialog.setMessage("ُThanks for Choosing us");

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        new Dialog(getApplicationContext());
        dialog.show();


    }

    void signoutDialog(){

        AlertDialog dialog = new AlertDialog.Builder(this).create();

        dialog.setTitle("Way Tracker");
        dialog.setIcon(R.drawable.logo1);
        dialog.setMessage("Are you sure you want to sign out ?");

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });


        dialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                myEditor.putBoolean("checksignin", false);
                myEditor.commit();
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                finish();

            }
        });


        new Dialog(getApplicationContext());
        dialog.show();
    }




    ///////

    public  void readUserName()
    {


        final View view = new Profile().getView();
        String phone = HomeActivity.userphone;
        final String user_name;
        DocumentSnapshot doc=null;
        DocumentReference user = firestoer.collection("users_name").document(phone);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {

                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())
                    {


                        if (!doc.getString("user_name").isEmpty())
                        {

                            usernameProfile=doc.getString("user_name");
                            View headerView = navigationView.getHeaderView(0);
                            TextView navUsername =  headerView.findViewById(R.id.username);
                            navUsername.setText(usernameProfile);


                        }
                    }

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });


    }

    public  void readuserImage() {

        final String phone = HomeActivity.userphone;
        final String profile_image;
        DocumentSnapshot doc=null;
        DocumentReference user = firestoer.collection("users_images").document(phone);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {

                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())
                    {

                        if (!doc.getString("user_image").isEmpty())
                        {


                            pathProfile=doc.getString("user_image");
                            gsReference = storage.getReferenceFromUrl("gs://way-tracker-c5180.appspot.com/images/"+pathProfile);
                             navImage =  headerView.findViewById(R.id.user_image);

                              img = new ImageView(getApplicationContext());

                              img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            Glide.with(getApplicationContext())
                                    .using(new FirebaseImageLoader())
                                    .load(gsReference)
                                    .into(navImage);

                          //  navImage.setImageDrawable(img.getDrawable());





                        }


                    }

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
