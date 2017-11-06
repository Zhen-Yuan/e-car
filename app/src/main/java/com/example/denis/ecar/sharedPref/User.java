package com.example.denis.ecar.sharedPref;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shinmei on 20.10.2017.
 */

public class User {

    public static String PROVIDER = "com.example.denis.ecar.sharedPref.PROVIDER";

    private String id;
    private String name;
    private String email;


    public User(){

    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public boolean isSocialNetworkLogged(Context context){
        String token = getProviderSP(context);
        return(token.contains("facebook") || token.contains("google"));
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
            map.put( "email", getEmail() );
        }
    }


    public void setEmailIfNull(String email) {
        if(this.email == null){
            this.email = email;
        }

    }


    public void saveProviderSP(Context context, String token){
        UserPref.saveSP( context, PROVIDER, token );
    }
    public String getProviderSP(Context context){
        return(UserPref.getSP(context, PROVIDER));
    }



    public void saveDB(DatabaseReference.CompletionListener... completionListener){
        DatabaseReference firebaseDB = UserPref.getFirebaseDB().child("users").child(getId());

        if(completionListener.length == 0){
            firebaseDB.setValue(this);
        }
        else{
            firebaseDB.setValue(this, completionListener[0]);
        }
    }

    public void updateDB(DatabaseReference.CompletionListener... completionListener){
        DatabaseReference firebase = UserPref.getFirebaseDB().child("users").child(getId());

        Map<String, Object> map = new HashMap<>();
        setNameInMap(map);
        setEmailInMap(map);

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
        DatabaseReference firebase = UserPref.getFirebaseDB().child("users").child(getId());
        firebase.setValue(null, completionListener);
    }


    public void contextDataDB(Context context){
        DatabaseReference firebase = UserPref.getFirebaseDB().child("users").child(getId());
        firebase.addListenerForSingleValueEvent((ValueEventListener) context);
    }
}
