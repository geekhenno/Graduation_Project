package com.hennonoman.waytracker;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AuthenticationActivity extends AppCompatActivity {




    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor myEditor;
    String codeSent , phone,password;
    PhoneAuthCredential credential;
    FirebaseFirestore firestoer;
    public static  EditText codenumber;
    ProgressDialog progressDialog;
    Button reg;
    public static String codeMessage="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);





        mAuth = FirebaseAuth.getInstance();
        codenumber= findViewById(R.id.codeNumber);
        reg= findViewById(R.id.regCode);
        firestoer = FirebaseFirestore.getInstance();

         phone = getIntent().getExtras().getString("phone");
         password = getIntent().getExtras().getString("pass");

        mySharedPreferences = getSharedPreferences("signinstatus", Context.MODE_PRIVATE);
        myEditor =mySharedPreferences.edit();

        sendVerivicationCode();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                verifySignInCode();



            }
        });





        findViewById(R.id.reSendCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),"resent succsessfully",Toast.LENGTH_SHORT).show();
                sendVerivicationCode();

            }
        });





        mDatabase = FirebaseDatabase.getInstance().getReference();


    }



    private void verifySignInCode()
    {

        String c =codenumber.getText().toString();

        if(c.isEmpty())
        {
            codenumber.setError("fill the blank");
            codenumber.requestFocus();
            return;
        }
        if(codeSent==null)
        {
            Toast.makeText(AuthenticationActivity.this,"Invalid code",Toast.LENGTH_SHORT).show();
        }


        else
        {
            progressDialog = ProgressDialog.show(AuthenticationActivity.this, "","Please Wait...", true);
            credential = PhoneAuthProvider.getCredential(codeSent, c);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            writeNewUser();

                        }
                        else
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(AuthenticationActivity.this,"Invalid code",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }




                    }
                });
    }

    private void sendVerivicationCode() {



        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks




    }



    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent =s;



        }
    };


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //  updateUI(currentUser);
    }

    private void writeNewUser() {

        Map<String, Object> newContact = new HashMap<>();
        newContact.put("phone", phone);
        newContact.put("password", password);

        firestoer.collection("users").document(phone).set(newContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        saveUserNametoFirebase();
                        saveImagetoFirebase();

                        startActivity(new Intent(AuthenticationActivity.this,HomeActivity.class));
                        myEditor.putBoolean("checksignin", true);
                        myEditor.putString("userphone", phone);
                        myEditor.commit();
                        Toast.makeText(AuthenticationActivity.this,"Register successfully",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        LoginActivity.loginActivity.finish();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AuthenticationActivity.this, "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                        progressDialog.dismiss();
                    }
                });



    }



    public void saveUserNametoFirebase()
    {




        Map<String, Object> newContact = new HashMap<>();
        newContact.put("user_name","" );


        firestoer.collection("users_name").document(phone).set(newContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    private void saveImagetoFirebase()
    {


        Map<String, Object> newContact = new HashMap<>();
        newContact.put("user_image", "def_profile.png");
        firestoer.collection("users_images").document(phone).set(newContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });










    }









}
