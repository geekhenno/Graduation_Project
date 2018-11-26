package com.hennonoman.waytracker.fragments_java;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hennonoman.waytracker.HelperClasses.GroupInfo;
import com.hennonoman.waytracker.R;

import java.util.HashMap;
import java.util.Map;

import static com.hennonoman.waytracker.HomeActivity.userphone;

public class JoinGroup extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;

    EditText joidId;
    Button join;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.join_group,container,false);

        joidId = v.findViewById(R.id.joinId);
        join = v.findViewById(R.id.join);


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();




        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                writeNewUser();


            }
        });



        return v;

    }


    private void writeNewUser()
    {
        // Write a message to the database
        String groupid = database.getReference().push().getKey();
        double latit= MapviewFragment.location.latitude;
        double longi= MapviewFragment.location.longitude;
       // GroupInfo group = new GroupInfo(groupid, title,userphone,latit+"",longi+"");

        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("status", "member");
        hopperUpdates.put("latit", latit+"");
        hopperUpdates.put("longi", longi+"");
       // myRef.child("groups").child("-LOoIHAPfy_Y8BSPZozD").child("users_group").push().setValue(userphone);
        myRef.child("groups").child("-LOoIHAPfy_Y8BSPZozD").child("users_group").child("+962796993233").updateChildren(hopperUpdates);
        Toast.makeText(getContext(), "Joind Successfully", Toast.LENGTH_SHORT).show();
   //     getActivity().onBackPressed();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
    }
}
