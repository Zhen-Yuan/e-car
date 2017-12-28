package com.example.denis.ecar.sharedPref;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.denis.ecar.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getCacheDir;


public class ProfileFragment extends Fragment
        implements ValueEventListener, DatabaseReference.CompletionListener{

    private View view;
    private CircleImageView profileImage;
    private Uri imageUrl;

    private User user;
    private FirebaseAuth firebaseAuth;
    private StorageReference firebaseStorage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
         user = new User();
         user.setId(firebaseAuth.getCurrentUser().getUid());
        DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getId());
        firebaseDB.addListenerForSingleValueEvent(this);

        firebaseStorage = FirebaseStorage.getInstance().getReference().child("images/").child(user.getId());

        profileImage = (CircleImageView)view.findViewById(R.id.civProfile);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }


    public void selectImage() {
        Crop.pickImage(getActivity());
    }


    private void uploadImage(Uri uri) {
        if (uri != null) {
            firebaseStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(getContext(), imageUrl.toString(), Toast.LENGTH_SHORT).show();
                    user.setImageUrl(imageUrl.toString());
                    Picasso.with(getContext()).load(imageUrl).into(profileImage);
                    user.updateDB(ProfileFragment.this);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity());
    }


    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            imageUrl = Crop.getOutput(result);
            uploadImage(imageUrl);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getContext(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue(User.class);
        Picasso.with(getContext()).load(u.getImageUrl()).into(profileImage);
    }


    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
            Toast.makeText(getContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getContext(), "Profilbild aktualisiert", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}