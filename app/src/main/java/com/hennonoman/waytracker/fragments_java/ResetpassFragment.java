package com.hennonoman.waytracker.fragments_java;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hennonoman.waytracker.AuthenticationActivity;
import com.hennonoman.waytracker.HomeActivity;
import com.hennonoman.waytracker.LoginActivity;
import com.hennonoman.waytracker.R;
import com.hennonoman.waytracker.RegisterActivity;
import com.rilixtech.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ResetpassFragment extends Fragment implements View.OnClickListener{
    FirebaseFirestore firestoer;
    PhoneAuthCredential credential;
    private FirebaseAuth mAuth;
    Button continue_reset ;
    EditText pnumber;
    CountryCodePicker ccp;
    public static String number;
    String codeSent;
    public static EditText editText;
    ProgressDialog progressDialog;
    public static String pass;


    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction ft;
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null)
        {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resetpass, container, false);


        firestoer = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


         pass="";
        continue_reset = view.findViewById(R.id.continue_reset);
        pnumber =view.findViewById(R.id.pnumber);
        ccp = view.findViewById(R.id.ccp);

        continue_reset.setOnClickListener(this);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        getActivity().setTitle("Reset Password");
    }





    public void showDialog()
    {

        editText = new EditText(getContext());
        editText.setHint("Confirmation code");
        editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(6);
        editText.setFilters(FilterArray);
        editText.setMaxLines(1);


        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Confirmation Code")
                .setIcon(R.drawable.logo1)
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .setNegativeButton("cancel", null)
                .setView(editText)
                .create();


        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String c =editText.getText().toString();
                        if(c.isEmpty())
                            editText.setError("fill blank");
                        else
                        {
                            verifySignInCode();
                            alertDialog.dismiss();
                        }

                    }
                });
            }
        });
        alertDialog.show();








    }





    @Override
    public void onClick(View view) {







        if(view.getId()==R.id.continue_reset)

        {


            number = pnumber.getText().toString();

            if(number.length()<9)
                pnumber.setError("wrong phone number");

            else
                {
                    number="+"+ccp.getSelectedCountryCode()+pnumber.getText().toString();
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
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
                    TextView ph = new TextView(getContext());
                    ph.setText(number);
                    ph.setTextSize(22);
                    ph.setTextColor(getResources().getColor(R.color.colorText));
                    ph.setGravity(Gravity.CENTER);

                    builder.setView(ph);
                    builder.setView(ph);


                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            progressDialog = ProgressDialog.show(getContext(), "","Please wait...", true);
                            ReadSingleContact();

                        }
                    });
                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                            pnumber.requestFocus();
                        }
                    });
                    builder.show();





            }

        }

    }

    private void saveToFirebase(String pass) {

        final String phone= HomeActivity.userphone;
        Map<String, Object> newContact = new HashMap<>();
        newContact.put("phone", phone);
        newContact.put("password", pass);

        firestoer.collection("users").document(phone).set(newContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getContext(), "Changed Successfully", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void ReadSingleContact() {

        DocumentSnapshot doc=null;
        DocumentReference user = firestoer.collection("users").document(number);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {

                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists())

                    {

                        pass= doc.getString("password");
                        progressDialog.dismiss();
                        sendVerivicationCode();
                        showDialog();

                    }
                    else {
                        Toast.makeText(getContext(), "There is no account with this phone number", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
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


    private void sendVerivicationCode() {



        PhoneAuthProvider.getInstance().verifyPhoneNumber(

                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
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
            progressDialog.dismiss();



        }
    };



    private void verifySignInCode()
    {

        String c =editText.getText().toString();


        if(codeSent==null)
        {
            Toast.makeText(getContext(),"Invalid code",Toast.LENGTH_SHORT).show();

        }


        else
        {
            progressDialog = ProgressDialog.show(getContext(), "","Please Wait...", true);
            credential = PhoneAuthProvider.getCredential(codeSent, c);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            fragment = new ConfirmRespass();
                            fragmentManager = getActivity().getSupportFragmentManager();
                            ft=fragmentManager.beginTransaction();
                            ft.replace(R.id.contentframe,fragment);
                            ft.commit();


                            progressDialog.dismiss();

                        }
                        else
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getContext(),"Invalid code",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }




                    }
                });
    }







}
