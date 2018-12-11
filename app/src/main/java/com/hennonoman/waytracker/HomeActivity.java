package com.hennonoman.waytracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.hennonoman.waytracker.fragments_java.ChangePasswordFragmant;
import com.hennonoman.waytracker.fragments_java.Friends_Frgament;
import com.hennonoman.waytracker.fragments_java.GroupFragment;
import com.hennonoman.waytracker.fragments_java.JoinGroup;
import com.hennonoman.waytracker.fragments_java.MapviewFragment;
import com.hennonoman.waytracker.fragments_java.MyGroups;
import com.hennonoman.waytracker.fragments_java.Profile;


import java.util.ArrayList;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements Profile.ProfileInteractionListener , MapviewFragment.MapviewInteractionListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
     NavigationView navigationView;
    public static View headerView;

    FirebaseFirestore firestoer;
    FirebaseStorage storage;
    private StorageReference mStorage;
    public static StorageReference gsReference;
    //////
    SharedPreferences.Editor myEditor;
    SharedPreferences mySharedPreferences;

    ///
    TextView username;
    ///
    public static Fragment fragment ,fragment2;
    FragmentManager fragmentManager;
    FragmentTransaction ft;
    ///
    FirebaseDatabase database;
    DatabaseReference myRef;

    ///
    public static ImageView navImage;
    public static Bitmap bitmap_profile;
    public static  String pathProfile,usernameProfile;
    public static String userphone;

    ////

    public static ArrayList<String> frindes;
    public static  ArrayAdapter frindesAdabter;

    public static ArrayList<String> frindes_QB_ID;


    //
    public static String[] from;
    public static Cursor cursor;
    public static SimpleCursorAdapter simpleCursorAdapt;

    String currentTag;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.create_group:

//
//                if(!(fragment instanceof ListUsersActivity))
//                {
//                    fragment = new ListUsersActivity();
//                    ft = getSupportFragmentManager().beginTransaction();
//                    ft.addToBackStack(null);
//                    ft.replace(R.id.content_frame,fragment);
//                    ft.commit();
//                }




                break;

            case R.id.join_group:


                if(!(fragment instanceof JoinGroup))
                {
                    fragment = new JoinGroup();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.content_frame,fragment);
                    ft.commit();
                }

                break;


            case R.id.menu_refresh:


                Toast.makeText(this, "Hi refresh", Toast.LENGTH_SHORT).show();

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

        }

        else
            {
            super.onBackPressed();

             }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mySharedPreferences = getSharedPreferences("signinstatus", Context.MODE_PRIVATE);
        myEditor = mySharedPreferences.edit();




        frindes=new ArrayList<>();
        frindes_QB_ID = new ArrayList<>();
        userphone = mySharedPreferences.getString("userphone","");

        toolbar = findViewById(R.id.mapToolBar);
        setSupportActionBar(toolbar);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(userphone);

        fragmentManager = getSupportFragmentManager();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.




        ButterKnife.bind(this);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_viewmap);
        navigationView.setItemIconTintList(null);

        firestoer = FirebaseFirestore.getInstance();





        readUserName();
        readuserImage();
        getFrineds();




        //change name of header
         headerView = navigationView.getHeaderView(0);
        TextView navUsernumber =  headerView.findViewById(R.id.usernumber);
        TextView navUsername =  headerView.findViewById(R.id.username);
        navUsernumber.setText(userphone);
        navUsername.setText("");

        storage = FirebaseStorage.getInstance();
        mStorage =storage.getReference();




        fragment = new MapviewFragment();
        openFragment(fragment,"one");
        currentTag="one";

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.main_home);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                ChangePasswordFragmant.hideKeyboard(HomeActivity.this);
                Profile.hideKeyboard(HomeActivity.this);

                for (int i =0 ; i < fragmentManager.getBackStackEntryCount() ;i++)
                    fragmentManager.popBackStack();






                switch (menuItem.getItemId())
                {

                    case R.id.main_home:

                        if(fragmentManager.findFragmentByTag("one") != null)
                        {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("one")).commit();
                        } else
                            {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.content_frame, new MapviewFragment(), "one").commit();
                        }
                        if(fragmentManager.findFragmentByTag(currentTag) != null && !currentTag.equals("one"))
                        {
                            //if the other fragment is visible, hide it.
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(currentTag)).commit();
                        }


                        if(MapviewFragment.activity!=null)
                        MapviewFragment.activity.setTitle("Home");
                        currentTag="one";

                        break;

                    case R.id.user_profile:

                        if(fragmentManager.findFragmentByTag("two") != null)
                        {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("two")).commit();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.content_frame, new Profile(), "two").commit();
                        }
                        if(fragmentManager.findFragmentByTag(currentTag) != null && !currentTag.equals("two"))
                        {
                            //if the other fragment is visible, hide it.
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(currentTag)).commit();
                        }


                        if(Profile.activity!=null)
                        Profile.activity.setTitle("Profile");
                        currentTag="two";

                        break;

                    case R.id.user_groups:


                        if(fragmentManager.findFragmentByTag("three") != null)
                        {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("three")).commit();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                          //  fragmentManager.beginTransaction().add(R.id.content_frame, new ChatDialogsActivity(), "three").commit();
                        }
                        if(fragmentManager.findFragmentByTag(currentTag) != null && !currentTag.equals("three")){
                            //if the other fragment is visible, hide it.
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(currentTag)).commit();
                        }


//                        if(ChatDialogsActivity.activity!=null)
//                            ChatDialogsActivity.activity.setTitle("Group");
//                        currentTag="three";

                        break;

                    case R.id.user_favorite:


                        if(fragmentManager.findFragmentByTag("four") != null)
                        {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("four")).commit();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.content_frame, new Friends_Frgament(), "four").commit();
                        }
                        if(fragmentManager.findFragmentByTag(currentTag) != null && !currentTag.equals("four")){
                            //if the other fragment is visible, hide it.
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(currentTag)).commit();
                        }


                        if(Friends_Frgament.activity!=null)
                            Friends_Frgament.activity.setTitle("Friends");
                        currentTag="four";
                        break;



                    case R.id.user_settings:

                        break;


                    case R.id.contactus:
                        break;

                    case R.id.aboutus:
                    //    startActivity(new Intent(HomeActivity.this, MainActivity.class));

//                        aboutDialog();
                        break;


                    case R.id.signout:


                        userphone="";
                        usernameProfile="";
                        pathProfile="";
                        signoutDialog();

                        break;







                }



                drawerLayout.closeDrawer(GravityCompat.START);

                return true;

            }
        });









    }

    @Override
    public void onProfileFragmentInteraction(Uri uri) {

    }


    private void openFragment(Fragment fragment, String tag)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
        if (existingFragment != null)
        {
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);
            fragmentTransaction.hide(currentFragment);
            fragmentTransaction.show(existingFragment);
        }
        else
            {
            fragmentTransaction.add(R.id.content_frame, fragment, tag);
        }
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.commit();
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
        String phone = userphone;
        final String user_name;
        DocumentSnapshot doc=null;
        DocumentReference user = firestoer.collection("users").document(phone);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {

                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())
                    {



                            usernameProfile=doc.getString("user_name");
                             headerView = navigationView.getHeaderView(0);
                            TextView navUsername =  headerView.findViewById(R.id.username);
                            navUsername.setText(usernameProfile);



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

        final String phone = userphone;
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

                            Glide.with(getApplicationContext())
                                    .using(new FirebaseImageLoader())
                                    .load(gsReference)    // you can pass url too
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>() {


                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            // you can do something with loaded bitmap here
                                            bitmap_profile=resource;
                                            navImage.setImageBitmap(resource);
                                        }
                                    });


//                            Glide.with(getApplicationContext())
//                                    .using(new FirebaseImageLoader())
//                                    .load(gsReference)
//                                    .into(navImage);



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


    public boolean checkConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
       return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }




    public  void  getFrineds()
    {

         cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (cursor.moveToNext())
        {
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s", "");
            ReadSingleContact(phoneNumber);
            Log.i("phoneNumber: ", phoneNumber);
        }
        cursor.close();
        frindesAdabter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_multiple_choice,frindes);






    }


    private void ReadSingleContact(final String phone)
    {


        if(SplachActivity.allUsers.contains(phone))
       {
           Toast.makeText(getApplicationContext(), "read contact", Toast.LENGTH_SHORT).show();
            Log.i("usersFrindes",phone);
           frindes.add(phone);
           Log.i("hello1",phone);


       }
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
