package com.example.denis.ecar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.denis.ecar.login.LoginActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Home";
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(HomeActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // Ausgabe, falls Google-Login fehlschlaegt
                        Log.d(TAG, "onConnectionFailed:" + connectionResult);
                        Toast.makeText(HomeActivity.this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.bttnWeiter).setOnClickListener(this);
        findViewById(R.id.bttnSignOut).setOnClickListener(this);
    }



    private void signOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }


    private void fbSignOut() {
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    private void revokeAccess() {
        // Firebase sign out
        firebaseAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        return;
                    }
                });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bttnWeiter) {
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        } else if (i == R.id.bttnSignOut) {
            signOut();
        } else if (i == R.id.bttnFBSignOut) {
            fbSignOut();
        }else if (i == R.id.bttnGoogleSignOut) {
            revokeAccess();
        }
    }
}
