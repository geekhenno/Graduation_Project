package com.hennonoman.waytracker.fragments_java;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    TextView edit_profile_pic , change_password;
    private ProgressDialog mProgress;
    FirebaseFirestore firestoer;

    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;

    String imagePath;
    String comparePath="",compareUserName="";
    private StorageReference mStorageReference;
    String setPath,  setUser;

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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);


    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);





    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        image_profilePic= view.findViewById(R.id.image_profilePic);
        edit_user_name=view.findViewById(R.id.edit_user_name);
        save_edit_profile= view.findViewById(R.id.save_edit_profile);
        edit_profile_pic = view.findViewById(R.id.edit_profile_pic);
        change_password=view.findViewById(R.id.change_password);
        icon_user_name=view.findViewById(R.id.icon_user_name);
        mProgress = new ProgressDialog(getContext());
        save_edit_profile.setOnClickListener(this);
        icon_user_name.setOnClickListener(this);
        change_password.setOnClickListener(this);
        edit_profile_pic.setOnClickListener(this);

         firestoer = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
         edit_user_name.setEnabled(false);
        setProfile();
       edit_user_name.setText(setUser);
      image_profilePic.setImageDrawable(HomeActivity.navImage.getDrawable());


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

                break;

            case R.id.save_edit_profile:
                edit_user_name.setEnabled(false);

                uploadImage();
                saveUserNametoFirebase();

                break;

            case R.id.edit_profile_pic:

                if (!hasReadandWriteStorgePermission())
                {
                    requestReadAndWriteStorage();

                }
                changeProfilePic();


                break;

            case R.id.change_password:
               // Toast.makeText(getContext(), "change_password", Toast.LENGTH_SHORT).show();
               readuserImage();
               readUserName();
                break;



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



            if (resultCode == RESULT_OK) {


                resultUri = result.getUri();
                image_profilePic.setImageURI(resultUri);

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

            mProgress.setMessage("Saving...");
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

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



        mProgress.dismiss();






    }


    private void readUserName() {



        String phone = HomeActivity.userphone;
        final String user_name;
        DocumentSnapshot doc=null;
        DocumentReference user = firestoer.collection("users_name").document(phone);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {

                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())
                    {


                        if (!doc.getString("user_name").isEmpty())
                        {
                            edit_user_name.setText(doc.getString("user_name"));


                        }
                    }

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });


    }

    private void readuserImage() {

        String phone = HomeActivity.userphone;
        final String profile_image;
        DocumentSnapshot doc=null;
        DocumentReference user = firestoer.collection("users_images").document(phone);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {

                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())
                    {

                        if (!doc.getString("user_image").isEmpty())
                        {

                            imagePath= doc.getString("user_image");
                           image_profilePic.setImageURI(Uri.parse(imagePath));
                           image_profilePic.setImageAlpha(0);

                        }


                    }

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });


    }

    public void setProfile()
    {
        setPath=HomeActivity.pathProfile;
        setUser=HomeActivity.usernameProfile;
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
