package com.example.denis.ecar.login;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shinmei on 20.10.2017.
 */

public class SaveProviders {

    public static String PREF = "com.example.denis.ecar.PREF";
    private static DatabaseReference firebaseDB;


    private SaveProviders() {}


    public static DatabaseReference getFirebaseDB() {
        if (firebaseDB == null) {
            firebaseDB = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseDB;
    }


    public static void saveSP(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }


    public static String getSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String token = sp.getString(key, "");
        return token;
    }
}
