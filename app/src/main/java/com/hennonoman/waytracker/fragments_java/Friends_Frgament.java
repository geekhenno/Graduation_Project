package com.hennonoman.waytracker.fragments_java;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hennonoman.waytracker.HomeActivity;
import com.hennonoman.waytracker.LoginActivity;
import com.hennonoman.waytracker.R;

import static com.hennonoman.waytracker.HomeActivity.cursor;
import static com.hennonoman.waytracker.HomeActivity.frindesAdabter;
import static com.hennonoman.waytracker.HomeActivity.simpleCursorAdapt;

public class Friends_Frgament extends Fragment {

    ListView list;

    public static Activity activity;
    FirebaseFirestore firestoer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.friends_fragment, container, false);


        list = v.findViewById(R.id.frinds_list);

        list.setAdapter(frindesAdabter);







        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        activity = getActivity();

        activity.setTitle("Friends");
    }






}
