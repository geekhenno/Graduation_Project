package com.hennonoman.waytracker.fragments_java;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hennonoman.waytracker.HelperClasses.GroupInfo;
import com.hennonoman.waytracker.HelperClasses.UsersInGroup;
import com.hennonoman.waytracker.HomeActivity;
import com.hennonoman.waytracker.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.hennonoman.waytracker.HomeActivity.userphone;

public class GroupFragment extends Fragment implements View.OnClickListener {

    CircleImageView image_group;
    EditText titlegroup;
    Button creategroupfragment;
    TextView selectimagegroup;

    ProgressDialog progressDialog;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      View v = inflater.inflate(R.layout.fragment_group, container, false);


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        image_group =v.findViewById(R.id.image_group);
        titlegroup =v.findViewById(R.id.titlegroup);
        creategroupfragment =v.findViewById(R.id.creategroupfragment);
        selectimagegroup =v.findViewById(R.id.selectimagegroup);
        creategroupfragment.setOnClickListener(this);

        HomeActivity.fragment=null;


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Groups");
    }


    @Override
    public void onClick(View view) {


        switch (view.getId())
        {

            case R.id.creategroupfragment:

                String title= titlegroup.getText().toString();
                if(title.isEmpty())
                    titlegroup.setError("Can not be empty");
                else {
                    progressDialog = ProgressDialog.show(getContext(), "", "Create Group...", true);
                    writeNewUser(title);
                }
                break;

        }

    }

    private void writeNewUser( String title)
    {
        // Write a message to the database
        String groupid = database.getReference().push().getKey();
        String users_group = database.getReference().push().getKey();
        double latit= MapviewFragment.location.latitude;
        double longi= MapviewFragment.location.longitude;
        GroupInfo group = new GroupInfo(groupid, title,userphone,latit+"",longi+"");
        myRef.child("groups").child(groupid).setValue(group);

        ArrayList<String> u =  new ArrayList<>();
        u.add(userphone);
        u.add("07969932323");
        UsersInGroup usersInGroup = new UsersInGroup(u);


        myRef.child("groups").child(groupid).child("users_group").setValue(usersInGroup);


        progressDialog.dismiss();
        Toast.makeText(getContext(), "Creation Successfully", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();

    }

}
