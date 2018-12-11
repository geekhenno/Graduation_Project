package com.hennonoman.waytracker;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {




    private EditText mPhoneView;
    CountryCodePicker ccp;
    private EditText mPasswordView;
    FirebaseFirestore firestoer;
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor myEditor;
    public static Activity loginActivity;
    ProgressDialog progressDialog;

    public static String  user;
    public static String password;


    String selectCountry="";

    private String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission
            .ACCESS_FINE_LOCATION, android.Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS ,
    Manifest.permission.READ_CONTACTS};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        loginActivity=this;

        mPhoneView = findViewById(R.id.phonenumber);
        mPasswordView = findViewById(R.id.password);
        ccp = findViewById(R.id.ccp);



        //hidden keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        // ask for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(arePermissionsEnabled())
            {
//                    permissions granted, continue flow normally
            }
            else {

                requestMultiplePermissions();
            }
        }




        //works as sign in button

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL)
                {
                    if(!checkConnection())
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    else
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        Button SignInButton = findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!checkConnection())
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                else
                attemptLogin();

                //Intent in =new Intent(AuthenticationActivity.this,HomeActivity.class);
                //startActivity(in);
            }
        });


        firestoer = FirebaseFirestore.getInstance();
        mySharedPreferences = getSharedPreferences("signinstatus", Context.MODE_PRIVATE);
        myEditor =mySharedPreferences.edit();







        // default phone code selected
        selectCountry ="+"+ccp.getSelectedCountryCode();

        //on select another country
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {

            @Override
            public void onCountrySelected(Country selectedCountry)
            {
                selectCountry ="+"+selectedCountry.getPhoneCode();
            }
        });


    }



    private void attemptLogin()
    {

        mPhoneView.setError(null);
        mPasswordView.setError(null);

        String phone = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;

        View focusView = null;

        if ( !isPasswordValid(password))
        {
            mPasswordView.setError("password should be at least 6 digits");
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone))
        {
            mPhoneView.setError("fill the blank");
            focusView = mPhoneView;
            cancel = true;
        }
        else if (!isPhoneValid(phone))
        {
            mPhoneView.setError("wrong phone number");
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel)
        {
            focusView.requestFocus();
        } else

            {
                progressDialog = ProgressDialog.show(this, "","Signing in...", true);
                ReadSingleContact();



        }
    }

    private boolean isPhoneValid(String phone)
    {
        return  phone.length() >=9;
    }

    private boolean isPasswordValid(String password)
    {
        return password.length() > 5;
    }


    public void go_to_register(View view)
    {
        if (view.getId() == R.id.go_to_register) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

        }



    }

    private void ReadSingleContact() {

        final String user_name= selectCountry+mPhoneView.getText().toString();
        final String password = mPasswordView.getText().toString();
        DocumentSnapshot doc=null;

        DocumentReference user = firestoer.collection("users").document(user_name);

        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {

                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())
                    {

                        if (doc.getString("phone").equalsIgnoreCase(user_name) && doc.getString("password").equalsIgnoreCase(password) )
                        {

                            myEditor.putBoolean("checksignin", true);
                            myEditor.putString("userphone", selectCountry+mPhoneView.getText().toString());
                            myEditor.commit();
                            doc=null;
                            Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(i);


                            finish();
                            progressDialog.dismiss();


                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplication(), "Wrong username or password", Toast.LENGTH_LONG).show();

                        }
                    }
                    else
                    {

                        progressDialog.dismiss();
                        Toast.makeText(getApplication(), "There is no account with this phone number", Toast.LENGTH_LONG).show();

                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

                    }
                });


    }

    public void getHelp(View view)
    {

        startActivity(new Intent(LoginActivity.this,HelpFragment.class));



    }
    public boolean checkConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101
                ) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(permissions[i])) {

                        closeNow();
                    }
                    return;
                }
            }


        }
    }
    private void closeNow()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            finishAffinity();
        }

        else
        {
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled(){
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions(){
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
    }




}
