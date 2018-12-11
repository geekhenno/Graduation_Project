package com.hennonoman.waytracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hennonoman.waytracker.PwdStrength.PasswordStrength;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends AppCompatActivity implements TextWatcher {


    public void signin(View view) {
        onBackPressed();
    }


    private DatabaseReference mDatabase;
    private EditText username, phone, password_reg, repassword_reg,username_reg;
    private TextInputLayout password, repassword;

    String _phone, _password, _repassword, fullphone ,username_reg_string;
    boolean checkReg = false;
    boolean regStatus = true;
    int count = 0;
    FirebaseFirestore firestoer;
    CountryCodePicker ccp;
    String selectCountry = "";
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        phone = findViewById(R.id.phone_reg);
        password_reg = findViewById(R.id.password_reg);
        repassword_reg = findViewById(R.id.repassword_reg);
        username_reg = findViewById(R.id.username_reg);

        password_reg.addTextChangedListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ////////////////







        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        firestoer = FirebaseFirestore.getInstance();

        ccp = findViewById(R.id.ccp);
        selectCountry = ccp.getSelectedCountryCode();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {

            @Override
            public void onCountrySelected(Country selectedCountry) {
                selectCountry = selectedCountry.getPhoneCode();
            }
        });


    }

    public void register(View view) {


        _phone = phone.getText().toString();
        fullphone = "+" + selectCountry + phone.getText().toString();
        _password = password_reg.getText().toString();
        _repassword = repassword_reg.getText().toString();
        username_reg_string = username_reg.getText().toString();

        boolean cancel = false;

        if (!isPasswordValid(_password))
        {

            Snackbar.make(view, "password should be at least 6 digits", Snackbar.LENGTH_LONG).show();
            cancel = true;
        } else if (!isMatchPassword(_password, _repassword)) {

            Snackbar.make(view, "password doesn't match", Snackbar.LENGTH_LONG).show();
            cancel = true;
        } else if (!isPhoneValid(_phone)) {

            Snackbar.make(view, "wrong phone number", Snackbar.LENGTH_LONG).show();
            cancel = true;
        }

        else if (username_reg_string.isEmpty())
        {

            Snackbar.make(view, "wrong user name", Snackbar.LENGTH_LONG).show();
            cancel = true;
        }

        if (cancel)
        {


        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Specify the alert dialog title
            String titleText = "Are you sure this is your number ?";
            // Initialize a new foreground color span instance
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorTextDark));
            // Initialize a new spannable string builder instance
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
            // Apply the text color span
            ssBuilder.setSpan(
                    foregroundColorSpan,
                    0,
                    titleText.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            // Set the alert dialog title using spannable string builder
            builder.setTitle(ssBuilder);
            //  builder.setMessage("Are you sure this is your number ?");
            //builder.set
            TextView ph = new TextView(this);
            ph.setText(fullphone);
            ph.setTextSize(22);
            ph.setTextColor(getResources().getColor(R.color.colorText));
            ph.setGravity(Gravity.CENTER);

            builder.setView(ph);
            builder.setView(ph);


            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(!checkConnection())
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

                    else {
                        progressDialog = ProgressDialog.show(RegisterActivity.this, "", "Please Wait...", true);
                        ReadSingleContact();
                    }

                }
            });
            builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                    phone.requestFocus();
                }
            });
            builder.show();


        }
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 5;
    }



    private boolean isPhoneValid(String phone) {
        return phone.length() >= 9;
    }

    private boolean isMatchPassword(String p1, String p2) {
        return p1.equals(p2);
    }


    private void ReadSingleContact()
    {

        DocumentSnapshot doc = null;
        DocumentReference user = firestoer.collection("users").document(fullphone);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists()) {

                        if (doc.getString("phone").equalsIgnoreCase(fullphone)) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "phone number already exist", Toast.LENGTH_LONG).show();
                            doc = null;
                        }
                    } else {
                        confirmPhoneNumber();

                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();


                    }
                });


    }

    public void confirmPhoneNumber() {
        Intent i = new Intent(RegisterActivity.this, AuthenticationActivity.class);
        i.putExtra("phone", fullphone);
        i.putExtra("pass", _password);
        i.putExtra("username", username_reg_string);
        progressDialog.dismiss();
        startActivity(i);
        finish();


    }







    @Override
    public void afterTextChanged(Editable s) {
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        updatePasswordStrengthView(s.toString());
    }

    private void updatePasswordStrengthView(String password)
    {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView strengthView = (TextView) findViewById(R.id.password_strength);
        if (TextView.VISIBLE != strengthView.getVisibility())
            return;

        if (password.isEmpty())
        {
            strengthView.setText("");
            progressBar.setProgress(0);
            progressBar.getProgressDrawable().setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.SRC_IN);

            return;
        }

        PasswordStrength str = PasswordStrength.calculateStrength(password);
        strengthView.setText(str.getText(this));
        strengthView.setTextColor(str.getColor());

        progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getText(this).equals("Weak")) {
            progressBar.setProgress(25);
        } else if (str.getText(this).equals("Medium")) {
            progressBar.setProgress(50);
        } else if (str.getText(this).equals("Strong")) {
            progressBar.setProgress(75);
        } else {
            progressBar.setProgress(100);
        }
    }
    public boolean checkConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }





}