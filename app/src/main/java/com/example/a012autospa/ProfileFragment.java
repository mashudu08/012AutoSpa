package com.example.a012autospa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    Button logout, update;
    FloatingActionButton addImage;
    EditText inputName, inputNumber, inputEmail, inputPassword;
    ImageView profileImage;
    CardView bookings;
    private ProgressDialog progressDialog;
    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseUser user;
    private final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_profile, container, false);

        //setting progress dialog when uploading data to firebase
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Retrieving user data...");
        progressDialog.setTitle("Downloading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // database instance
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        FirebaseDatabase db = com.google.firebase.database.FirebaseDatabase.getInstance();
        DatabaseReference root2  = db.getReference().child(userId).child("profile");
        FirebaseDatabase.getInstance().getReference().keepSynced(true);

        logout = rootView.findViewById(R.id.btnLogout);
        update = rootView.findViewById(R.id.updateProfile);
        addImage = rootView.findViewById(R.id.fabEdit);
        profileImage = rootView.findViewById(R.id.profile_picture);
        inputName = rootView.findViewById(R.id.txtName);
        inputNumber = rootView.findViewById(R.id.txtNumber);
        inputEmail = rootView.findViewById(R.id.txtEmail);
        inputPassword = rootView.findViewById(R.id.txtPassword);
        bookings = rootView.findViewById(R.id.viewCard);

        root2.orderByChild("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //getting the profile information from the database
                String name = snapshot.child("name").getValue().toString();
                String number = snapshot.child("number").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String password = snapshot.child("password").getValue().toString();
                String imageUrl = snapshot.child("profilePictureUrl").getValue().toString();

                if(imageUrl.equals("")){
                    //setting image resource to default image
                    profileImage.setImageResource(R.drawable.ic_baseline_person_24);
                }
                else {
                    Picasso.with(getActivity()).load(imageUrl).fit().centerCrop().into(profileImage);
                }

                //inserting the profile info into the EditText fields
                inputName.setText(name);
                inputNumber.setText(number);
                inputEmail.setText(email);
                inputPassword.setText(password);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new ViewBookingsFragment()).commit();
            }
        });

        return rootView;
    }

    //getting the image file extension - example: .jpg
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(getActivity()).load(mImageUri).fit().centerCrop().into(profileImage);
            Toast.makeText(getActivity(), "Profile picture changed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        // database instance
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        FirebaseDatabase db = com.google.firebase.database.FirebaseDatabase.getInstance();
        DatabaseReference root  = db.getReference().child(userId).child("profile");
        mStorageRef = FirebaseStorage.getInstance().getReference("images");

        String txtName = inputName.getText().toString();
        String txtEmail = inputEmail.getText().toString();
        String txtPass = inputPassword.getText().toString();
        String txtNumber = inputNumber.getText().toString();

        //creating a unique id for image with file extension
        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

        if (mImageUri != null) {
            if(txtName.isEmpty()){
                inputName.setError("Name required!");
            }
            else if (txtNumber.isEmpty()){
                inputNumber.setError("Number required!");
            }
            else if(txtEmail.isEmpty()){
                inputEmail.setError("Email required!");
            }
            else if (txtPass.isEmpty()){
                inputPassword.setError("Password required!");
            }
            else if(txtPass.length() < 6){
                inputPassword.setError("Password is too weak!");
            }
            else {
                //setting progress dialog when uploading data to firebase
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Upload in progress...");
                progressDialog.setTitle("Uploading");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        progressDialog.dismiss();
                                        String photoUrl = uri.toString();
                                        User userInfo = new User(txtName, txtNumber, txtEmail, txtPass, photoUrl);

                                        Toast.makeText(getActivity(), "Details of updated successfully", Toast.LENGTH_SHORT).show();
                                        root.setValue(userInfo);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.setMessage("The Following error occurred: " + e.getMessage());
                                progressDialog.setTitle("Error");
                                progressDialog.show();
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}