package com.example.denis.ecar.sharedPref;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;

/**
 * Created by Norbert on 01.10.2017.
 * Modified by Shinmei on 17.10.2017.
 */


public class Settings extends AppCompatActivity implements DatabaseReference.CompletionListener{

    public static final String TAG = "Settings";

    private SeekBar seekBar;
    private TextView tv3;
    SharedPreferences sp;
    SharedPreferences.Editor spe;

    private User user;
    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    protected GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private TextView tvUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button bttnSubmit;

    //public static final int REQUEST_GALLERY = 11;
    //private ImageView ivProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sp = getSharedPreferences("bla", Context.MODE_PRIVATE);
        spe = sp.edit();

        init();//Aufruf der Initalisierungsmethode
        //initUser();
    }


    public void init(){
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(60);
        seekBar.setProgress(sp.getInt("interval", 30));
        tv3 = (TextView) findViewById(R.id.textView3);
        tv3.setText(String.valueOf(seekBar.getProgress()));
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv3.setText(String.valueOf(progress));
                spe.putInt("interval", progress);
                spe.commit();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
/*
        //ivProfile = (ImageView)findViewById(R.id.ivProfile);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bttnSubmit = (Button) findViewById(R.id.bttnSubmit);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                facebookAccessData(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {}
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                makeToast(error.getMessage());
            }
        });

        // GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(Settings.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed:" + connectionResult);
                        makeToast(connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
 */
        findViewById(R.id.tvDeleteAcc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, RemoveUserActivity.class));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
    }

/*
    private void initUser() {
        user = new User();
        user.setEmail(etEmail.getText().toString());
        user.setPassword(etPassword.getText().toString());
    }   */


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LinkAccountsFragment linkFragment = (LinkAccountsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentLinkAcc);
        linkFragment.onActivityResult(requestCode, resultCode, data);
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentProfile);
        profileFragment.onActivityResult(requestCode, resultCode, data);
/*       if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY) {
                Uri selectImage = data.getData();
                ivProfile.setImageURI(selectImage);
        }
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();
            if (account == null)
                return;
            googleAccessData(account.getIdToken());
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } */
    }

/*
    private void changeBttnLabels() {
        if (isLinked(EmailAuthProvider.PROVIDER_ID)) {
            bttnSubmit.setText(R.string.PasswortAendern);
            etPassword.setVisibility(View.GONE);
        } else {
            bttnSubmit.setText(R.string.Registrieren);
            etPassword.setVisibility(View.VISIBLE);
        }
        bttnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bttnSubmit.getText().equals("Registrieren")) {
                    initUser();
                    emailAccessData(etEmail.getText().toString(), etPassword.getText().toString());
                } else if (bttnSubmit.getText().equals("Passwort ändern")) {
                    changePassword();
                }
            }
        });

        setButtonLabel(R.id.bttnFB, FacebookAuthProvider.PROVIDER_ID, R.string.linkFB,
                R.string.unlinkFB);

        setButtonLabel(R.id.bttnGoogle, GoogleAuthProvider.PROVIDER_ID, R.string.linkGoogle,
                R.string.unlinkGoogle);
    }


    private void setButtonLabel(int bttnId, String providerId, int linkId, int unlinkId) {
        if (isLinked(providerId)) {
            ((Button) findViewById(bttnId)).setText(unlinkId);
        } else {
            ((Button) findViewById(bttnId)).setText(linkId);
        }
    }       */


    /*
    public void selectImage() {
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_GALLERY);
    }   */


    /*
     * Ordnet den EmailAuth von firebase einen String zu fuer die spaetere Verwendung.
     * @param email enthaelt die E-Mail-Adresse des Users
     * @param password enthaelt das Passwort des Users
     */     /*
    private void emailAccessData(String email, String password) {
        linkProvider("email", email, password);
    }


    /**
     * Ordnet dem Facebook accessToken einen String zu fuer die spaetere Verwendung.
     * @param accessToken enthaelt den Facebook AcessToken
     */ /*
    private void facebookAccessData(AccessToken accessToken) {
        linkProvider("facebook", (accessToken != null ? accessToken.getToken() : null));
    }


    /*
     * Ordnet dem Google accessToken einen String zu fuer die spaetere Verwendung.
     * @param accessToken enthaelt den Google AccessToken
     */ /*
    private void googleAccessData(String accessToken) {
        linkProvider("google", accessToken);
    }


    /**
     * Prueft, ob ein token vorhanden ist und um welchen es sich handelt mithilfe des im String
     * angegebenen providers. Daraufhin wird der provider mit dem Acc verbunden.
     * @param provider uebergibt den aktuellen Provider an
     * @param token uebergibt den/die benoetigten token
     */ /*
    private void linkProvider(final String provider, String... token) {
        if(token != null && token.length > 0 && token[0] != null) {
            AuthCredential credential = FacebookAuthProvider.getCredential(token[0]);
            credential = provider.equalsIgnoreCase("google") ?
                    GoogleAuthProvider.getCredential(token[0], null) : credential;
            credential = provider.equalsIgnoreCase("email") ?
                    EmailAuthProvider.getCredential(token[0], token[1]) : credential;

            firebaseAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            changeBttnLabels();
                            makeToast("Konto wurde erfolgreich mit " + provider + "verbunden.");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    makeToast("Error: " + e.getMessage());
                }
            });
        } else {
            firebaseAuth.signOut();
        }
    }


    public void sendFBLoginData(View view) {
        if (isLinked(FacebookAuthProvider.PROVIDER_ID)) {
            unlinkProvider(FacebookAuthProvider.PROVIDER_ID);
            return;
        }
        LoginManager.getInstance().logInWithReadPermissions(Settings.this,
                Arrays.asList("public_profile", "email", "user_friends"));
    }


    public void sendGoogleLoginData(View view) {
        if (isLinked(GoogleAuthProvider.PROVIDER_ID)) {
            unlinkProvider(GoogleAuthProvider.PROVIDER_ID);
            return;
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }


    /**
     * Prueft, ob der im Paramenter angegebene Provider mit dem Acc verbunden ist.
     * @param providerId enthaelt den zu pruefenden Provider.
     * @return false, wenn dies nicht der Fall ist und true, wenn es zutrifft
     */     /*
    private boolean isLinked(String providerId) {
        if(providerId != null) // Um Crash bei nicht vorhandener Verlinkung zu unterbinden
        {
            for (UserInfo userInfo : firebaseAuth.getCurrentUser().getProviderData()) {
                if (userInfo.getProviderId().equals(providerId))
                    return true;
            }
        }
        return false;
    }


    /**
     * Trennt die Verbindung zum angegebenen Provider zum Acc.
     * @param providerId enthaelt die ProviderID, von dem getrennt werden soll
     */     /*
    private void unlinkProvider(final String providerId) {
        firebaseAuth.getCurrentUser().unlink(providerId)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        changeBttnLabels();
                        makeToast("Verbindung getrennt.");
                        if (isLastProvider(providerId)) {
                            user.setId(firebaseAuth.getCurrentUser().getUid());
                            user.removeDB(Settings.this);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("Error: " + e.toString());
                    }
        });
    }


    /**
     * Ueberprueft, ob der letzte Provider EmailAuth ist
     * @param providerId beinhaltet die Provider-ID
     * @return true, wenn der Fall eintrifft
     */     /*
    private boolean isLastProvider(String providerId) {
        int size = firebaseAuth.getCurrentUser().getProviders().size();
        return (size == 0 || (size == 1 && providerId.equals(EmailAuthProvider.PROVIDER_ID)));
    }


    private void changePassword() {
        View view = (LayoutInflater.from(Settings.this)).inflate(R.layout.input_change_password, null);
        final EditText etNewPassword = (EditText) view.findViewById(R.id.etNewPassword);
        final String password = etNewPassword.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

        builder.setTitle("Passwort ändern").setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
           public void onClick(DialogInterface dialog, int id) {
               if (!TextUtils.isEmpty(password) &&
                       password.length() > 5) {
                   firebaseAuth.getCurrentUser().updatePassword(password);
               } else {
                   etNewPassword.setError("Ungültiges Passwort.");
               }
           }
        });
        builder.setNegativeButton("abbrechen",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }   */


    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null){
            Log.d(TAG, "fireBase databaseError: " + databaseError.toString());
            makeToast("Error: " + databaseError.toString());
        }
        firebaseAuth.getCurrentUser().delete();
        firebaseAuth.signOut();
        finish();
    }


    public void makeToast(String text) {
        Toast.makeText(Settings.this, text, Toast.LENGTH_LONG).show();
    }



}