package com.example.denis.ecar.login;

import android.content.Context;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shinmei on 29.09.2017.
 */

public class User {
    public static String PROVIDER = "com.example.denis.ecar.PROVIDER";
    private String id;
    private String username;
    private String email;


    public User() {}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void setUsernameInMap(Map<String, Object> map) {
        if (getUsername() != null)
            map.put("Username", getUsername());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private void setEmailInMap( Map<String, Object> map ) {
        if( getEmail() != null )
            map.put( "email", getEmail() );
    }


    public void saveProviderSP(Context context, String token) {
        SaveProviders.saveSP(context, PROVIDER, token);
    }

    public String getProviderSP(Context context) {
        return (SaveProviders.getSP(context, PROVIDER));
    }


    public void saveDB( DatabaseReference.CompletionListener... completionListener ){
        DatabaseReference firebase = SaveProviders.getFirebaseDB().child("users").child( getId() );

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }
    }

    public void updateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = SaveProviders.getFirebaseDB().child("users").child( getId() );

        Map<String, Object> map = new HashMap<>();
        setUsernameInMap(map);
        setEmailInMap(map);

        if( map.isEmpty() ){
            return;
        }

        if( completionListener.length > 0 ){
            firebase.updateChildren(map, completionListener[0]);
        }
        else{
            firebase.updateChildren(map);
        }
    }

    public void removeDB( DatabaseReference.CompletionListener completionListener ){

        DatabaseReference firebase = SaveProviders.getFirebaseDB().child("users").child( getId() );
        firebase.setValue(null, completionListener);
    }

    public void contextDataDB( Context context ){
        DatabaseReference firebase = SaveProviders.getFirebaseDB().child("users").child( getId() );

        firebase.addListenerForSingleValueEvent( (ValueEventListener) context );
    }


    public boolean isSocialNetworkLogged(Context context) {
        String token = getProviderSP(context);
        return (token.contains("facebook") || token.contains("google"));
    }
}
