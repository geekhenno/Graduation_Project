package com.hennonoman.waytracker.fragments_java;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hennonoman.waytracker.AuthenticationActivity;
import com.hennonoman.waytracker.HomeActivity;
import com.hennonoman.waytracker.LoginActivity;
import com.hennonoman.waytracker.R;
import com.hennonoman.waytracker.RegisterActivity;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profile.ProfileInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_WRITE_PERMISSION = 786;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final static int GALLERY_CODE = 1;
    private Uri resultUri = null;

    CircleImageView image_profilePic;
    ImageView icon_user_name;
    EditText edit_user_name;
    Button save_edit_profile;
    TextView edit_profile_pic , change_password , show_user_name, show_phone,show_phone_under;
    private ProgressDialog mProgress;
    FirebaseFirestore firestoer;
    PopupMenu popupMenu;
    Fragment fragment;
     FragmentTransaction ft;
    FragmentManager fragmentManager;
    String imagePath;
    private StorageReference mStorageReference;
    String setPath,  setUser;

    public static Activity activity;

    public static Uri mImageUri=null;

    private ProfileInteractionListener mListener;

    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    private boolean hasReadandWriteStorgePermission() {
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestReadAndWriteStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE ) ) {
            Log.d("SplachActivity", "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                0);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity=getActivity();
       activity.setTitle("Profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        image_profilePic= view.findViewById(R.id.image_profilePic);
        edit_user_name=view.findViewById(R.id.edit_user_name);
        show_user_name=view.findViewById(R.id.show_user_name);
        show_phone=view.findViewById(R.id.show_phone);
        show_phone_under=view.findViewById(R.id.show_phone_under);
        save_edit_profile= view.findViewById(R.id.save_edit_profile);
        edit_profile_pic = view.findViewById(R.id.edit_profile_pic);
     //   edit_user_name.setOnClickListener(this);
//        change_password=view.findViewById(R.id.change_password);
        icon_user_name=view.findViewById(R.id.icon_user_name);
//        mProgress = new ProgressDialog(getContext());
        save_edit_profile.setOnClickListener(this);
        icon_user_name.setOnClickListener(this);
//        change_password.setOnClickListener(this);
        edit_profile_pic.setOnClickListener(this);
//
         firestoer = FirebaseFirestore.getInstance();
         mStorageReference = FirebaseStorage.getInstance().getReference();
        setProfile();
       edit_user_name.setText(setUser);
       show_user_name.setText(setUser);
       show_phone.setText(HomeActivity.userphone);
        show_phone_under.setText(HomeActivity.userphone);
      image_profilePic.setImageDrawable(HomeActivity.navImage.getDrawable());
      image_profilePic.setImageBitmap(HomeActivity.bitmap_profile);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);







         return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onProfileFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ProfileInteractionListener) {
            mListener = (ProfileInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }




    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void onClick(View view) {


        switch (view.getId())
        {
                case R.id.icon_user_name:

                    edit_user_name.setEnabled(true);
                showKeyboard();
                break;

            case R.id.save_edit_profile:
                edit_user_name.setEnabled(false);
                saveUserNametoFirebase();

                break;

            case R.id.edit_profile_pic:

                if (!hasReadandWriteStorgePermission())
                {
                    requestReadAndWriteStorage();
                }
                showPop(R.menu.profile_image_menu,edit_profile_pic);


                break;
//
//            case R.id.change_password:
//
//                    fragment = new ChangePasswordFragmant();
//                    fragmentManager = getActivity().getSupportFragmentManager();
//                    ft = fragmentManager.beginTransaction();
//                    ft.addToBackStack(null);
//                    ft.replace(R.id.content_frame,fragment);
//                    ft.commit();
//
//
//                break;
//


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK)
        {

             mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                     .start(getContext(), this);


        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);



            if (resultCode == RESULT_OK)
            {


                resultUri = result.getUri();
                image_profilePic.setImageURI(resultUri);
                uploadImage();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();


            }
        }
    }




    public void changeProfilePic()
    {


        /////
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
       startActivityForResult(galleryIntent, GALLERY_CODE);


    }




    public void uploadImage()
    {


        if( resultUri != null)
        {

            mProgress = ProgressDialog.show(getContext(), "","Saving...", true);
             mProgress.show();

                final StorageReference filepath = mStorageReference.child("images").
                        child(resultUri.getLastPathSegment());
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadurl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        saveImagetoFirebase(downloadurl);
                    }
                });

        }
    }
    public void saveUserNametoFirebase()
    {


        final String user_name= edit_user_name.getText().toString();
        String phone = HomeActivity.userphone;
        Map<String, Object> newContact = new HashMap<>();
        newContact.put("user_name",user_name );


        firestoer.collection("users_name").document(phone).set(newContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        TextView navUsername =  HomeActivity.headerView.findViewById(R.id.username);
                        navUsername.setText(user_name);
                        HomeActivity.usernameProfile=user_name;
                        show_user_name.setText(user_name);




                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    private void saveImagetoFirebase(final String imageUri)
    {


        String phone = HomeActivity.userphone;
        Map<String, Object> newContact = new HashMap<>();
        newContact.put("user_image", resultUri.getLastPathSegment());
        firestoer.collection("users_images").document(phone).set(newContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {



                        ImageView navImage =  HomeActivity.headerView.findViewById(R.id.user_image);
                         navImage.setImageURI(resultUri);
                         image_profilePic.setImageURI(resultUri);
                         image_profilePic.setImageAlpha(0);
                         resultUri=null;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



        mProgress.dismiss();






    }



    public void setProfile()
    {
        setPath=HomeActivity.pathProfile;
        setUser=HomeActivity.usernameProfile;
    }


public void showKeyboard()
{
    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

}



    public void showPop(int id, View v)
    {

        ContextThemeWrapper ctw = new ContextThemeWrapper(getContext(), R.style.CustomPopupTheme);
        popupMenu = new PopupMenu(ctw,v);
        popupMenu.inflate(id);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId())
                {

                    case R.id.change_pic:

                        changeProfilePic();

                        break;


                    case R.id.remove_pic:


                        ImageView navImage =  HomeActivity.headerView.findViewById(R.id.user_image);
                        navImage.setImageResource(R.drawable.def_profile);
                        image_profilePic.setImageResource(R.drawable.def_profile);
                        image_profilePic.setImageAlpha(0);

                        String phone = HomeActivity.userphone;
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
                        break;

                }


                return false;
            }
        });



    }

    


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface ProfileInteractionListener {
        // TODO: Update argument type and name
        void onProfileFragmentInteraction(Uri uri);
    }
}
