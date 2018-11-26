package com.hennonoman.waytracker.fragments_java;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hennonoman.waytracker.HelperClasses.CustomListView;
import com.hennonoman.waytracker.HelperClasses.CustomerGroupList;
import com.hennonoman.waytracker.HelperClasses.GroupInfo;
import com.hennonoman.waytracker.HelperClasses.UsersInGroup;
import com.hennonoman.waytracker.HelperClasses.list;
import com.hennonoman.waytracker.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.hennonoman.waytracker.HomeActivity.userphone;

public class MyGroups extends Fragment {
    private static final String TAG = "ViewDatabase";
    FirebaseDatabase database;
    DatabaseReference myRef;
    SwipeMenuListView mListView;
    ArrayList<String> array;
    ArrayList<GroupInfo>ids;
    private ArrayList<list>  dataArrayList = new ArrayList<>();
    private list data;
    ArrayAdapter adapter;

    public static Activity activity;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.groups_list,container,false);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        mListView=v.findViewById(R.id.listview);


        array= new ArrayList<>();
        ids= new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //

        mListView.setMenuCreator(creator);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Toast.makeText(getContext(), dataArrayList.get(i).getIdGroup(), Toast.LENGTH_SHORT).show();
            }
        });



        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
            {
                switch (index)
                {
                    case 0:

//                        dataArrayList.remove(position);
//                        adapter.notifyDataSetChanged();
                        deleteGroup(position);


                        break;

                    case 2:

                        break;
                }
                return false;
            }
        });


        return v;
    }


    public void deleteGroup(int index)
    {
        Query applesQuery =  myRef.child("groups").child(dataArrayList.get(index).getIdGroup());

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
               dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }

        }

        );
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity=getActivity();
        getActivity().setTitle("Groups");
    }
    SwipeMenuCreator creator = new SwipeMenuCreator()
    {

        @Override
        public void create(SwipeMenu menu)
        {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.parseColor("#F45557")));
            // set item width
            deleteItem.setWidth(200);
            deleteItem.setTitle("x");
            deleteItem.setTitleColor(Color.WHITE);
            deleteItem.setTitleSize(20);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };



    private void showData(DataSnapshot dataSnapshot) {

        dataArrayList.clear();
        DataSnapshot groupsSnapshot = dataSnapshot.child("groups");


        Iterable<DataSnapshot> contactChildren = groupsSnapshot.getChildren();

        for (DataSnapshot group : contactChildren)
        {


            GroupInfo gInfo = new GroupInfo();
            gInfo.setTitle(group.child("title").getValue().toString());
            gInfo.setGroupId(group.child("groupId").getValue().toString());
            gInfo.setLatit(group.child("latit").getValue().toString());
            gInfo.setLongi(group.child("longi").getValue().toString());
           String adminStatus = group.child("admin").getValue().toString();


           ArrayList<String> uu = (ArrayList<String>) group.child("users_group").child("phone").getValue();



           for (int i = 0 ; i<uu.size();i++)
           {
               Log.i("user"+i, uu.get(i));
           }


            dataArrayList.add(data = new list(gInfo.getTitle(), gInfo.groupId,R.drawable.favorite));
            array.add(gInfo.getTitle());
           ids.add(gInfo);


        }





        if (getActivity()!=null)
        {
             adapter = new CustomerGroupList(getActivity(),  dataArrayList);
            mListView.setAdapter(adapter);
        }

    }
}
