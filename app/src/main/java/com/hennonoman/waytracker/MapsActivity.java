package com.hennonoman.waytracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hennonoman.waytracker.LocationUtil.PermissionUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
         {



    TextView username;
    SharedPreferences.Editor myEditor;
    SharedPreferences mySharedPreferences;
    Marker now;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

 SupportMapFragment mapFragment;
    // list of permissions

             double latitude;
             double longitude;
             String address="";
             private GoogleMap mMap;
             // location last updated time
             private String mLastUpdateTime;
             // location updates interval - 3sec
             private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
             // fastest updates interval - 3 sec
             // location updates will be received if another app is requesting the locations
             // than your app can handle
             private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
             private static final int REQUEST_CHECK_SETTINGS = 100;
             // bunch of location related apis
             private FusedLocationProviderClient mFusedLocationClient;
             private SettingsClient mSettingsClient;
             private LocationRequest mLocationRequest;
             private LocationSettingsRequest mLocationSettingsRequest;
             private LocationCallback mLocationCallback;
             private Location mCurrentLocation;

             // boolean flag to toggle the ui
             private Boolean mRequestingLocationUpdates;
             private static final String TAG = MapsActivity.class.getSimpleName();

             @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.create_group:


                break;

        }

        return super.onOptionsItemSelected(item);
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
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);
        // initialize the necessary libraries
        init();

        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        startLocationButtonClick();
        toolbar = findViewById(R.id.mapToolBar);
        setSupportActionBar(toolbar);
        mySharedPreferences = getSharedPreferences("signinstatus", Context.MODE_PRIVATE);
        myEditor = mySharedPreferences.edit();
       // username = findViewById(R.id.user_name);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_viewmap);
        navigationView.setItemIconTintList(null);






        String userphone = mySharedPreferences.getString("userphone","");

        //change name of header
        View headerView = navigationView.getHeaderView(0);
        TextView navUsernumber =  headerView.findViewById(R.id.usernumber);
        TextView navUsername =  headerView.findViewById(R.id.username);
        navUsernumber.setText(userphone);
        navUsername.setText("");

//        username.setText("mohannad");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setCheckedItem(R.id.main_home);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;


                switch (menuItem.getItemId())
                {


                    case R.id.main_home:

                        setTitle("Home");
                        Toast.makeText(MapsActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                     //   fragment = new MapFragments();
                    //    mapFragment.getView().setVisibility(View.VISIBLE);


                        break;

                        case R.id.user_profile:
                        Toast.makeText(MapsActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                     //   fragment = new ProfileFragment();
                      //  mapFragment.getView().setVisibility(View.INVISIBLE);

                       //     startActivity(new Intent(MapsActivity.this,ProfileFragment.class));
                        break;

                    case R.id.user_groups:
                        Toast.makeText(MapsActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.user_favorite:
                        Toast.makeText(MapsActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                        break;



                    case R.id.user_settings:
                        Toast.makeText(MapsActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                        break;


                    case R.id.contactus:
                        Toast.makeText(MapsActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.aboutus:
                        aboutDialog();
                        break;


                    case R.id.signout:

                        signoutDialog();


                        break;




                }

                if (fragment != null)
                {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.layout_map,fragment);
                    ft.commit();
                }



                drawerLayout.closeDrawer(GravityCompat.START);

                return true;

            }
        });






    }



    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap=googleMap;


    }






    public void showToast(String message)
    {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(MapsActivity.this,LoginActivity.class));
                finish();

            }
        });


        new Dialog(getApplicationContext());
        dialog.show();
    }



             private void init() {
                 mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                 mSettingsClient = LocationServices.getSettingsClient(this);

                 mLocationCallback = new LocationCallback() {
                     @Override
                     public void onLocationResult(LocationResult locationResult) {
                         super.onLocationResult(locationResult);
                         // location is received
                         mCurrentLocation = locationResult.getLastLocation();
                         mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                         updateLocationUI();
                     }
                 };

                 mRequestingLocationUpdates = false;

                 mLocationRequest = new LocationRequest();
                 mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
                 mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
                 mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                 LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                 builder.addLocationRequest(mLocationRequest);
                 mLocationSettingsRequest = builder.build();
             }

             /**
              * Restoring values from saved instance state
              */
             private void restoreValuesFromBundle(Bundle savedInstanceState) {
                 if (savedInstanceState != null) {
                     if (savedInstanceState.containsKey("is_requesting_updates")) {
                         mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
                     }

                     if (savedInstanceState.containsKey("last_known_location")) {
                         mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
                     }

                     if (savedInstanceState.containsKey("last_updated_on")) {
                         mLastUpdateTime = savedInstanceState.getString("last_updated_on");
                     }
                 }

                 updateLocationUI();
             }


             /**
              * Update the UI displaying the location data
              * and toggling the buttons
              */
             private void updateLocationUI() {

                 if (mCurrentLocation != null)
                 {


                     mMap.clear();
                     latitude = mCurrentLocation.getLatitude();
                     longitude =mCurrentLocation.getLongitude();
                     getAddress();
                     LatLng location = new LatLng(latitude, longitude);
                     mMap.addMarker(new MarkerOptions().position(location).title(address));
                     mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15));


                 //    Toast.makeText(this, "Last updated on: "+mLastUpdateTime, Toast.LENGTH_SHORT).show();

                 }
             }

             @Override
             public void onSaveInstanceState(Bundle outState) {
                 super.onSaveInstanceState(outState);
                 outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
                 outState.putParcelable("last_known_location", mCurrentLocation);
                 outState.putString("last_updated_on", mLastUpdateTime);

             }



             /**
              * Starting location updates
              * Check whether location settings are satisfied and then
              * location updates will be requested
              */
             private void startLocationUpdates() {
                 mSettingsClient
                         .checkLocationSettings(mLocationSettingsRequest)
                         .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                             @SuppressLint("MissingPermission")
                             @Override
                             public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                 Log.i("", "All location settings are satisfied.");

                                 //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                                 //noinspection MissingPermission
                                 mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                         mLocationCallback, Looper.myLooper());

                                 updateLocationUI();
                             }
                         })
                         .addOnFailureListener(this, new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 int statusCode = ((ApiException) e).getStatusCode();
                                 switch (statusCode) {
                                     case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                         Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                                 "location settings ");
                                         try {
                                             // Show the dialog by calling startResolutionForResult(), and check the
                                             // result in onActivityResult().
                                             ResolvableApiException rae = (ResolvableApiException) e;
                                             rae.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                                         } catch (IntentSender.SendIntentException sie) {
                                             Log.i(TAG, "PendingIntent unable to execute request.");
                                         }
                                         break;
                                     case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                         String errorMessage = "Location settings are inadequate, and cannot be " +
                                                 "fixed here. Fix in Settings.";
                                         Log.e(TAG, errorMessage);

                                         Toast.makeText(MapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                 }

                                 updateLocationUI();
                             }
                         });
             }

            // OnClick(R.id.btn_start_location_updates)
             public void startLocationButtonClick() {
                 // Requesting ACCESS_FINE_LOCATION using Dexter library
                 Dexter.withActivity(this)
                         .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                         .withListener(new PermissionListener() {
                             @Override
                             public void onPermissionGranted(PermissionGrantedResponse response) {
                                 mRequestingLocationUpdates = true;
                                 startLocationUpdates();
                             }

                             @Override
                             public void onPermissionDenied(PermissionDeniedResponse response) {
                                 if (response.isPermanentlyDenied()) {
                                     // open device settings when the permission is
                                     // denied permanently
                                     openSettings();
                                 }
                             }

                             @Override
                             public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                 token.continuePermissionRequest();
                             }
                         }).check();
             }

            // @OnClick(R.id.btn_stop_location_updates)
             public void stopLocationButtonClick() {
                 mRequestingLocationUpdates = false;
                 stopLocationUpdates();
             }

             public void stopLocationUpdates() {
                 // Removing location updates
                 mFusedLocationClient
                         .removeLocationUpdates(mLocationCallback)
                         .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                              //   Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();

                             }
                         });
             }

           //  @OnClick(R.id.btn_get_last_location)
             public void showLastKnownLocation() {
                 if (mCurrentLocation != null) {
                     Toast.makeText(getApplicationContext(), "Latitude: " + mCurrentLocation.getLatitude()
                             + "\nLongitude: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
                 } else {
                     Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                 switch (requestCode) {
                     // Check for the integer request code originally supplied to startResolutionForResult().
                     case REQUEST_CHECK_SETTINGS:
                         switch (resultCode) {
                             case Activity.RESULT_OK:
                                 Log.e(TAG, "User agreed to make required location settings changes.");
                                 // Nothing to do. startLocationupdates() gets called in onResume again.
                                 break;
                             case Activity.RESULT_CANCELED:
                                 Log.e(TAG, "User chose not to make required location settings changes.");
                                 mRequestingLocationUpdates = false;
                                 break;
                         }
                         break;
                 }
             }

             private void openSettings() {
                 Intent intent = new Intent();
                 intent.setAction(
                         Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                 Uri uri = Uri.fromParts("package",
                         BuildConfig.APPLICATION_ID, null);
                 intent.setData(uri);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
             }

             @Override
             public void onResume() {
                 super.onResume();

                 // Resuming location updates depending on button state and
                 // allowed permissions
                 if (mRequestingLocationUpdates && checkPermissions()) {
                     startLocationUpdates();
                 }

                 updateLocationUI();
             }

             private boolean checkPermissions() {
                 int permissionState = ActivityCompat.checkSelfPermission(this,
                         Manifest.permission.ACCESS_FINE_LOCATION);
                 return permissionState == PackageManager.PERMISSION_GRANTED;
             }


             @Override
             protected void onPause() {
                 super.onPause();

                 if (mRequestingLocationUpdates) {
                     // pausing location updates
                     stopLocationUpdates();
                 }
             }

             public Address getAddress(double latitude, double longitude)
             {
                 Geocoder geocoder;
                 List<Address> addresses;
                 geocoder = new Geocoder(this, Locale.getDefault());

                 try {
                     addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                     return addresses.get(0);

                 } catch (IOException e) {
                     e.printStackTrace();
                 }

                 return null;

             }


             public void getAddress()
             {

                 Address locationAddress=getAddress(latitude,longitude);

                 if(locationAddress!=null)
                 {
                      address = locationAddress.getAddressLine(0);
                     String address1 = locationAddress.getAddressLine(1);
                     String city = locationAddress.getLocality();
                     String state = locationAddress.getAdminArea();
                     String country = locationAddress.getCountryName();
                     String postalCode = locationAddress.getPostalCode();

                     String currentLocation;

                     if(!TextUtils.isEmpty(address))
                     {
                         currentLocation=address;

                         if (!TextUtils.isEmpty(address1))
                             currentLocation+="\n"+address1;

                         if (!TextUtils.isEmpty(city))
                         {
                             currentLocation+="\n"+city;

                             if (!TextUtils.isEmpty(postalCode))
                                 currentLocation+=" - "+postalCode;
                         }
                         else
                         {
                             if (!TextUtils.isEmpty(postalCode))
                                 currentLocation+="\n"+postalCode;
                         }

                         if (!TextUtils.isEmpty(state))
                             currentLocation+="\n"+state;

                         if (!TextUtils.isEmpty(country))
                             currentLocation+="\n"+country;

                         System.out.println("current location : "+ currentLocation);

                     }



                 }

             }



         }
