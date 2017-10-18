package com.example.denis.ecar;

/**
 * Created by Norbert on 01.10.2017.
 * Modified by Shinmei on 17.10.2017.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.login.LoginActivity;
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

import java.util.Arrays;


public class Settings extends AppCompatActivity {
    public static final String TAG = "settings";
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;
    private AccessToken accessToken;
    private String googleToken;
    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private SeekBar seekBar;
    private TextView tv3;
    private EditText etEmail;
    private Switch switchFB;
    private Switch switchGoogle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sp = getSharedPreferences("bla", Context.MODE_PRIVATE);
        spe = sp.edit();
        init();//Aufruf der Initalisierungsmethode
    }


    @Override
    protected void onStart() {
        super.onStart();
        // TODO
    }


    public void init(){
        etEmail = (EditText)findViewById(R.id.etEmail);
        firebaseAuth = FirebaseAuth.getInstance();
        switchFB = (Switch) findViewById(R.id.switchFB);
        switchGoogle = (Switch) findViewById(R.id.switchGoogle);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv3 = (TextView) findViewById(R.id.textView3);
        seekBar.setMax(60);
        seekBar.setProgress(sp.getInt("interval", 30));
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

        // FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                accessToken = loginResult.getAccessToken();
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(Settings.this, "Login has been canceled.",
                        Toast.LENGTH_SHORT).show();
                switchFB.toggle();
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                switchFB.toggle();
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
                        Toast.makeText(Settings.this, "Google Play Services error.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void initOnClick() {
        switchFB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (switchFB.isChecked() && !isLinked(FacebookAuthProvider.PROVIDER_ID)) {
                    LoginManager.getInstance().logInWithReadPermissions(Settings.this,
                            Arrays.asList("public_profile", "email", "user_friends"));
                    linkProvider("facebook", (accessToken != null ? accessToken.getToken(): null));
                } else {
                    unlinkProvider(FacebookAuthProvider.PROVIDER_ID);
                }
            }
        });

        switchGoogle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (switchGoogle.isChecked() && !isLinked(GoogleAuthProvider.PROVIDER_ID)) {
                    linkProvider("google", googleToken);
                } else {

                    unlinkProvider(GoogleAuthProvider.PROVIDER_ID);
                }
            }
        });

        findViewById(R.id.tvDeleteAcc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAcc(firebaseAuth.getCurrentUser());
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign erfolgreich -> Authentifizierung mit Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                googleToken = account != null ? account.getIdToken() : null;
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * Ordnet den EmailAuth von firebase einem String zu fuer die spaetere Verwendung.
     * @param email enthaelt die E-Mail-Adresse des Users
     * @param password enthaelt das Passwort des Users
     */
    private void emailAccessData(String email, String password) {
        linkProvider("email", email, password);
    }


    /**
     * Prueft, ob der im Paramenter angegebene Provider mit dem Acc verbunden ist.
     * @param providerId enthaelt den zu pruefenden Provider.
     * @return false, wenn dies nicht der Fall ist und true, wenn es zutrifft
     */
    private boolean isLinked(String providerId) {
        for (UserInfo userInfo: firebaseAuth.getCurrentUser().getProviderData()) {
            if (userInfo.getProviderId().equals(providerId))
                return true;
        }
        return false;
    }


    /**
     * Prueft, ob ein token vorhanden ist und um welchen es sich handelt mithilfe des im String
     * angegebenen providers. Daraufhin wird der provider mit dem Acc verbunden.
     * @param provider uebergibt den aktuellen Provider an
     * @param token uebergibt den/die benoetigten token
     */
    private void linkProvider(String provider, String... token) {
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
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                //updateUI(user);
                            } else {
                                Log.w(TAG, "fehlgeschlagen", task.getException());
                                Toast.makeText(Settings.this, "Anmeldung fehltgeschlagen.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        }
    }


    /**
     * Trennt die Verbindung zum angegebenen Provider zum Acc.
     * @param providerId enthaelt die ProviderID, von dem getrennt werden soll
     */
    private void unlinkProvider(final String providerId) {
        firebaseAuth.getCurrentUser().unlink(providerId)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Settings.this, "Verbindung getrennt.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(task.getResult().getUser());
                        } else {
                            Log.w(TAG, "fehlgeschlagen", task.getException());
                            Toast.makeText(Settings.this, "Verbindung konnte nicht getrennt werden.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    /**
     * Loescht den Account des Users und leitet zum Loginscreen weiter.
     * @param user enthaelt den aktuellen User
     */
    private void deleteAcc(FirebaseUser user) {
        user.delete();
        startActivity(new Intent(Settings.this, LoginActivity.class));
    }
}
