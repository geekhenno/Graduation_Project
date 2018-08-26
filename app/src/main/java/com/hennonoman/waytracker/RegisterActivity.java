package com.hennonoman.waytracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;


public class RegisterActivity extends AppCompatActivity {


    public void signin(View view) {
        onBackPressed();
    }


    private DatabaseReference mDatabase;
    private EditText username, phone, password_reg, repassword_reg;
    private TextInputLayout password, repassword;

    String _phone, _password, _repassword, fullphone;
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

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ////////////////


        if (!hasReadSmsPermission()) {
            requestReadAndSendSmsPermission();

        }
        if (!hasReadandWriteStorgePermission()) {
            requestReadAndWriteStorage();

        }

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

        if (cancel) {

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

                    progressDialog = ProgressDialog.show(RegisterActivity.this, "", "Please Wait...", true);
                    ReadSingleContact();

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

    private boolean isUsernameValid(String username) {
        return username.length() > 5;
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() >= 9;
    }

    private boolean isMatchPassword(String p1, String p2) {
        return p1.equals(p2);
    }


    private void ReadSingleContact() {

        DocumentSnapshot doc = null;
        DocumentReference user = firestoer.collection("users").document(fullphone);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists()) {
                        System.out.println("doc  --- - --" + doc);
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
        progressDialog.dismiss();
        startActivity(i);
        finish();


    }


    /**
     * Optional informative alert dialog to explain the user why the app needs the Read/Send SMS permission
     */
    private void showRequestPermissionsInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Requesting SMS permission");
        builder.setMessage("The app will now request your permission to send and read SMS related services.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestReadAndSendSmsPermission();
            }
        });
        builder.show();
    }

    /**
     * Runtime permission shenanigans
     */
    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(RegisterActivity.this,
                android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(RegisterActivity.this,
                        android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, android.Manifest.permission.READ_SMS)) {
            Log.d("SplachActivity", "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{android.Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                0);
    }


    private boolean hasReadandWriteStorgePermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadAndWriteStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            } else {
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case 1:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {
                    closeNow();
                }
                break;
        }
    }
    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            finishAffinity();
        }

        else
        {
            finish();
        }
    }


}