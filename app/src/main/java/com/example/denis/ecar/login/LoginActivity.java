package com.example.denis.ecar.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.denis.ecar.MainActivity;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;


/**
 * Login-View mit Email-, Facebook- und Google-Login sowie einer Weiterleitung zur Registrierung
 * und zu den AGB und Datenschutzrichtlinien.
 *
 * Created by Shinmei on 22.08.2017.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private FirebaseAuth firebaseAuth;
    private User user;
    private EditText etEmail;
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        initOnClick();
    }


    @Override
    protected void onStart() {
        super.onStart();
        nextActivity(firebaseAuth.getCurrentUser());
    }


    @Override
    protected void onResume() {
        super.onResume();
        nextActivity(firebaseAuth.getCurrentUser());
    }


    /**
     * Initialisiert die Authenticator und EditText fuer Eingabe der E-Mail-Adresse
     */
    private void init() {
        // Auth fuer E-Mail/Passwort-Anmeldung initialisieren
        firebaseAuth = FirebaseAuth.getInstance();
        user = new User();
        etEmail = (EditText)findViewById(R.id.etEmail);

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
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(LoginActivity.this, "Anmeldung abgebrochen.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(LoginActivity.this, "Anmeldung fehlgeschlagen.", Toast.LENGTH_SHORT).show();
            }
        });

        // GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed:" + connectionResult);
                        Toast.makeText(LoginActivity.this, "Google Play Services error.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    /**
     * Initialisiert onClick Elemente und dessen Event
     */
    private void initOnClick() {
        // leitet weiter zur Passwort-Eingabe-View
        findViewById(R.id.bttnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkForm()) {
                    return;
                }
                Intent next = new Intent(LoginActivity.this, EmailSignInActivity.class);
                next.putExtra("email", etEmail.getText().toString());
                startActivity(next);
            }
        });

        // leitet weiter zur Registrieren-View
        findViewById(R.id.tvRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, EmailSignInActivity.class));
            }
        });

        // Facebook-Login
        findViewById(R.id.bttnFBSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "email", "user_friends"));
            }
        });

        // Google-Login
        findViewById(R.id.bttnGoogleSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
            }
        });

        // leitet zu der AGB- und Datenschutz-View weiter
        findViewById(R.id.tvAGB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                if (account == null) {
                    Toast.makeText(LoginActivity.this, "GoogleAccount nicht gefunden.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                googleAccessData(account.getIdToken());
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * Ordnet dem Facebook accessToken einen String zu fuer die spaetere Verwendung.
     * @param accessToken enthaelt den Facebook AcessToken
     */
    private void facebookAccessData(AccessToken accessToken) {
        linkProvider("facebook", (accessToken != null ? accessToken.getToken() : null));
    }


    /**
     * Ordnet dem Google accessToken einen String zu fuer die spaetere Verwendung.
     * @param accessToken enthaelt den Google AccessToken
     */
    private void googleAccessData(String accessToken) {
        linkProvider("google", accessToken);
    }


    /**
     * Prueft, ob ein token vorhanden ist und um welchen es sich handelt mithilfe des im String
     * angegebenen providers. Daraufhin wird der provider mit dem Acc verbunden.
     * @param provider uebergibt den aktuellen Provider an
     * @param token uebergibt den/die benoetigten token
     */
    private void linkProvider(final String provider, String... token) {
        if(token != null && token.length > 0 && token[0] != null) {
            AuthCredential credential = FacebookAuthProvider.getCredential(token[0]);
            credential = provider.equalsIgnoreCase("google") ?
                    GoogleAuthProvider.getCredential(token[0], null) : credential;

            user.saveProviderSP(LoginActivity.this, provider);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "fehlgeschlagen", task.getException());
                                Toast.makeText(LoginActivity.this, "Anmeldung fehltgeschlagen.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            firebaseAuth.signOut();
        }
    }


    /**
     * ueberprueft, ob die Form der Emailadresse stimmt(beinhaltet ein '@'und .Suffix<3 oder
     * Suffix>4)
     * @return true, wenn die Form korrekt ist und false, wenn die Form nicht korrekt ist
     */
    private boolean checkForm() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Gib deine E-Mail-Adresse ein.");
            valid = false;
        } else if(!email.contains("@")) {
            etEmail.setError("E-Mail-Adresse ist nicht korrekt.");
            valid = false;
        } else {
            int i = email.indexOf('@');
            String domain = email.substring(i);
            i = domain.indexOf('.');
            String suffix = domain.substring(i);

            if (suffix.length()<3 || suffix.length()>4) {
                etEmail.setError("E-Mail-Adresse ist nicht korrekt.");
                valid = false;
            } else {
                etEmail.setError(null);
            }
        }
        return valid;
    }


    /**
     * Wechselt zur MainActivity.
     */
    private void nextActivity(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
