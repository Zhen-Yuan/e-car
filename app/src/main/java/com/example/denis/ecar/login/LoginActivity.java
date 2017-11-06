package com.example.denis.ecar.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.denis.ecar.MainActivity;
import com.example.denis.ecar.R;
import com.example.denis.ecar.sharedPref.User;
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

    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseListener;
    private User user;
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        initUser();
    }


    @Override
    protected void onStart() {
        super.onStart();
        verifyLogged();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseListener);
        }
    }


    private void init() {
        // FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookAccessData(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {
                showSnackbar(error.getMessage());
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
                        showSnackbar(connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if( firebaseUser == null ){
                    return;
                }
                if(user.getId() == null && isNameOk(user, firebaseUser)){
                    user.setId(firebaseUser.getUid());
                    user.setNameIfNull(firebaseUser.getDisplayName());
                    user.setEmailIfNull(firebaseUser.getEmail());
                    user.saveDB();
                }
                nextActivity();
            }
        };

        // View-Elemente
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        // leitet weiter zur Passwort-Eingabe-View
        findViewById(R.id.bttnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkForm()) {
                    return;
                }
                openProgressBar();
                initUser();
                signIn();
            }
        });

        // verschickt eine E-Mail mit Passwort-Reset
        findViewById(R.id.tvResetPW).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPWActivity.class));
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

        // leitet weiter zur Registrieren-View
        findViewById(R.id.tvSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

    }


    @Override
    protected void initUser() {
        user = new User();
        user.setEmail(etEmail.getText().toString());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();

            if (account == null) {
                showSnackbar("Google-Anmeldung fehlgeschlagen, bitte versuchen Sie es erneut.");
                return;
            }
            googleAccessData(account.getIdToken());
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
                                showSnackbar("Social Network Anmeldung fehltgeschlagen.");
                            }
                        }
                    });
        } else {
            firebaseAuth.signOut();
        }
    }


    private boolean isNameOk( User user, FirebaseUser firebaseUser ){
        return(user.getName() != null || firebaseUser.getDisplayName() != null);
    }


    /**
     * Meldet den User mit seiner E-Mailadresse von der Login-View zusammen mit dem eingegebenen
     * Passwort via firebase an.
     */
    private void signIn() {
        user.saveProviderSP(LoginActivity.this, "");
        firebaseAuth.signInWithEmailAndPassword(user.getEmail(), etPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            showSnackbar("Login fehlgeschlagen.");
                            return;
                        }
                    }
                });
    }


    private void verifyLogged(){
        if( firebaseAuth.getCurrentUser() != null ){
            nextActivity();
        } else {
            firebaseAuth.addAuthStateListener(firebaseListener);
        }
    }


    /**
     * Wechselt zur MainActivity.
     */
    private void nextActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    /**
     * ueberprueft, ob die Form der Emailadresse stimmt(beinhaltet ein '@'und .Suffix<3 oder
     * Suffix>4)
     * @return true, wenn die Form korrekt ist und false, wenn die Form nicht korrekt ist
     */
    private boolean checkForm() {
        boolean valid = true;

        // E-Mail-Adresse ueberpruefen
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

        // Passwort pruefen
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Gib dein Passwort ein.");
            valid = false;
        } else if (password.length() < 6) {
            etPassword.setError("Passwort ist zu kurz.");
        } else {
            etPassword.setError(null);
        }
        return valid;
    }

}
