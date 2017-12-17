package com.example.denis.ecar.sharedPref;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shinmei on 20.10.2017.
 */

public class User {

    private String id;
    private String name;
    private String email;
    private String imageUrl;
    private String password;


    public User(){
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setNameInMap(Map<String, Object> map) {
        if(getName() != null){
            map.put( "name", getName() );
        }
    }

    public void setNameIfNull(String name) {
        if(this.name == null){
            this.name = name;
        }
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private void setEmailInMap(Map<String, Object> map) {
        if(getEmail() != null){
            map.put("email", getEmail());
        }
    }

    public void setEmailIfNull(String email) {
        if(this.email == null){
            this.email = email;
        }
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private void setImageUrlInMap(Map<String, Object> map) {
        if(getImageUrl() != null){
            map.put("imageUrl", getImageUrl());
        }
    }

    public void setImageUrlIfNull(String imageUrl) {
        if(this.imageUrl == null){
            this.imageUrl = imageUrl;
        }
    }


    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void saveDB(DatabaseReference.CompletionListener... completionListener){
        DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getId());

        if(completionListener.length == 0){
            firebaseDB.setValue(this);
        }
        else{
            firebaseDB.setValue(this, completionListener[0]);
        }
    }


    public void updateDB(DatabaseReference.CompletionListener... completionListener){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getId());
        Map<String, Object> map = new HashMap<>();
        setNameInMap(map);
        setEmailInMap(map);
        setImageUrlInMap(map);
        if(map.isEmpty()){
            return;
        }
        if(completionListener.length > 0){
            firebase.updateChildren(map, completionListener[0]);
        }
        else{
            firebase.updateChildren(map);
        }
    }


    public void updateImage(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getId());
        Map<String, Object> map = new HashMap<>();
        map.put("imageUrl", imageUrl);
        if(map.isEmpty()){
            return;
        }
        if(completionListener.length > 0){
            firebase.updateChildren(map, completionListener[0]);
        }
        else{
            firebase.updateChildren(map);
        }

    }


    public void removeDB(DatabaseReference.CompletionListener completionListener){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getId());
        firebase.removeValue(completionListener);
    }


    public void contextDataDB(Context context){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getId());
        firebase.addListenerForSingleValueEvent((ValueEventListener) context);
    }
}
