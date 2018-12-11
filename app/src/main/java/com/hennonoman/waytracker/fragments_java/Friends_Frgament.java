package com.hennonoman.waytracker.fragments_java;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

import static com.hennonoman.waytracker.HomeActivity.cursor;
import static com.hennonoman.waytracker.HomeActivity.frindesAdabter;
import static com.hennonoman.waytracker.HomeActivity.simpleCursorAdapt;

public class Friends_Frgament extends Fragment   {

    ListView lstUsers;


    public static Activity activity;
    FirebaseFirestore firestoer;
    FloatingActionButton floatingActionButton ;

    public ListView lstChatDialogs ;
   // ChatDialogsAdapters adapters,adapter ;////////////////////
    ArrayList<QBUser> qbUsersWithoutCurrent ;
    ProgressDialog mDialog ;

    QBUser qbUser ;

    // ArrayList<QBUser> listUsers ;/////////////////////for get QBUser inside check mode

    String mode="" ;
    QBChatDialog qbChatDialog ;
    List<QBUser> userAdd=new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.friends_fragment, container, false);



        lstUsers=v.findViewById(R.id.frinds_list);
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);



        lstUsers.setAdapter(frindesAdabter);





        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        activity.setTitle("Friends");

    }











}
