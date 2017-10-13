package com.example.denis.ecar.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth firebaseAuth;
    private EditText etEmail;
    private Button bttnFBSignIn;
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
        etEmail = (EditText)findViewById(R.id.etEmail);

        // FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        bttnFBSignIn = (Button) findViewById(R.id.bttnFBSignIn);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
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
                        // Ausgabe, falls Google-Login fehlschlaegt
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
        bttnFBSignIn.setOnClickListener(new View.OnClickListener() {
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
                googleSignIn();
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
     * uebergibt das access token des Users an firebase fuer die Authentifizierung
     * @param token ist das access token, das fuer die Anmeldung in FB via firebase benoetigt wird
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Anmeldung erfolgreich -> zur naechsten Activity
                            Log.d(TAG, "signInWithCredential:success");
                            nextActivity(firebaseAuth.getCurrentUser());
                        } else {
                            // Anmeldung fehlgeschlagen -> Nachricht anzeigen
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Activity result wird der Facebook SDK uebergeben
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // gibt das Ergebnis vom Starten des Intents von GoogleSignInApi.getSignInIntent(...); wieder
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign erfolgreich -> Authentifizierung mit Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }


    /**
     * Anmeldung via Google ueber firebase
     * @param acc ist der Google acc des Users
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acc) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acc.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            nextActivity(firebaseAuth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        hideProgressDialog();
                    }
                });
    }


    /**
     * Ermoeglicht Google-Anmeldung
     */
       private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /**
     * ueberprueft, ob ein User eingeloggt ist, wenn ja, wird direkt zum Homescreen weitergeleitet
     * @param user uebergibt den aktuellen User
     */
    private void nextActivity(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }
}
