package com.hennonoman.waytracker.fragments_java;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hennonoman.waytracker.HomeActivity;
import com.hennonoman.waytracker.LoginActivity;
import com.hennonoman.waytracker.PwdStrength.PasswordStrength;
import com.hennonoman.waytracker.R;
import com.hennonoman.waytracker.RegisterActivity;

import java.util.HashMap;
import java.util.Map;


public class ChangePasswordFragmant extends Fragment implements View.OnClickListener,TextWatcher {


    Button changepass;
    EditText old,new_pass,re_new_pass;
    FirebaseFirestore firestoer;
    String s_old ,s_new ,s_re_new;
    ProgressDialog progressDialog;
    String pass;
    ProgressBar progressBar;
    TextView strengthView;

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

        View view = inflater.inflate(R.layout.changepassword_fragment, container, false);

        changepass = view.findViewById(R.id.change_password_button);

        old =view.findViewById(R.id.old_password);
        new_pass =view.findViewById(R.id.new_password);
        re_new_pass =view.findViewById(R.id.confirm_new_password);

        firestoer = FirebaseFirestore.getInstance();

        new_pass.addTextChangedListener(this);
        changepass.setOnClickListener(this);

        ReadSingleContact();

         progressBar = view.findViewById(R.id.progressBar);
         strengthView =  view.findViewById(R.id.password_strength);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        getActivity().setTitle("Change Password");
    }





    public void changePassword()

    {
        s_old = old.getText().toString();
        s_new = new_pass.getText().toString();
        s_re_new= re_new_pass.getText().toString();

        boolean cancel = false;

        if (!isPasswordValid(s_new) && !isPasswordValid(s_re_new) && !isPasswordValid(s_old) )
        {

            Snackbar.make(changepass, "password should be at least 6 digits", Snackbar.LENGTH_LONG).show();
            cancel = true;
        }
        else if (!isMatchPassword(s_new, s_re_new))
        {

            Snackbar.make(changepass, "password doesn't match", Snackbar.LENGTH_LONG).show();
            cancel = true;
        }

        else if (!isMatchPassword(pass, s_old))
        {

            Snackbar.make(changepass, "wrong password", Snackbar.LENGTH_LONG).show();
            cancel = true;
        }
        else if (pass.equals(s_re_new))
        {

            Snackbar.make(changepass, "the new password should be different than old password", Snackbar.LENGTH_LONG).show();
            cancel = true;
        }

        if (!cancel)
        {
            if(!checkConnection())
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            else
            {
                progressDialog = ProgressDialog.show(getContext(), "", "Please Wait...", true);
                saveToFirebase(s_re_new);
            }
        }


    }


    private void saveToFirebase(String pass) {

        final String phone=HomeActivity.userphone;
        Map<String, Object> newContact = new HashMap<>();
        newContact.put("phone", phone);
        newContact.put("password", pass);

        firestoer.collection("users").document(phone).set(newContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getContext(), "Changed Successfully", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                        progressDialog.dismiss();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });

    }

    private void ReadSingleContact() {


        DocumentSnapshot doc=null;
        DocumentReference user = firestoer.collection("users").document(HomeActivity.userphone);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {

                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())

                    {
                        pass= doc.getString("password");
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

    private boolean isMatchPassword(String p1, String p2) {
        return p1.equals(p2);
    }
    private boolean isPasswordValid(String password) {

        return password.length() > 5;
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.change_password_button)
            changePassword();

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
        strengthView.setText(str.getText(getContext()));
        strengthView.setTextColor(str.getColor());

        progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getText(getContext()).equals("Weak")) {
            progressBar.setProgress(25);
        } else if (str.getText(getContext()).equals("Medium")) {
            progressBar.setProgress(50);
        } else if (str.getText(getContext()).equals("Strong")) {
            progressBar.setProgress(75);
        } else {
            progressBar.setProgress(100);
        }
    }
    public boolean checkConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


}
