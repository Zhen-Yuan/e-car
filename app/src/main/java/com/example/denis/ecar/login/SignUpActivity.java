package com.example.denis.ecar.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.denis.ecar.MainActivity;
import com.example.denis.ecar.R;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSettings;
import com.example.denis.ecar.sharedPref.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Login oder Registrierung mit E-Mail-Adresse und Passwort.
 *
 * Created by Shinmei on 22.08.2017.
 */

public class SignUpActivity extends BaseActivity implements DatabaseReference.CompletionListener {

    private String defaultImage = "https://firebasestorage.googleapis.com/v0/b/softwareprojekt" +
            "-4c899.appspot.com/o/images%2Fdefault_profile.jpg?alt=media&token=24855b04-2aba" +
            "-43a3-8379-de33c5a151b4";

    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseListener;

//    private EcarDataSource dataSource;
    private EditText etUsername;
    private CheckBox chckBxShowPW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
    }


    private void init() {
        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if( firebaseUser == null || user.getId() != null ){
                    return;
                }
                user.setId(firebaseUser.getUid());
                user.saveDB(SignUpActivity.this);
                //saveUserData(firebaseUser.getUid());
            }
        };

        // SQLLite-Database
        //       dataSource = new EcarDataSource(this);

        // View-Elemente
        etUsername = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);
        chckBxShowPW = (CheckBox) findViewById(R.id.chckBxShowPW);

        // Weiterleitung zur Home-View bei erfolgreicher Anmeldung
        findViewById(R.id.bttnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProgressBar();
                initUser();
                createAccount();
            }
        });

        // anzeigen/ausblenden des Passwortes
        findViewById(R.id.chckBxShowPW).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chckBxShowPW.isChecked()) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }


    @Override
    protected void initUser() {
        user = new User();
        user.setName(etUsername.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setImageUrl(defaultImage);
    }


    /**
     * Erstellt einen Account mit E-Mail-Adresse und dem Passwort.
     */
    private void createAccount() {
        if (!checkForm()) {
            return;
        }
        // Account erstellen
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), etPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        } else {
                            closeProgressBar();
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar(e.getMessage());
            }
        });
    }


    /**
     * 1. Ueberprueft, ob ein Benutzername eingetragen wurde.
     * 2. ueberprueft, ob die Form der Emailadresse stimmt(beinhaltet ein '@'und .Suffix<3 oder
     *    Suffix>4).
     * 3. Ueberprueft, ob was in das Passwort EditText-Feld eingetragen wurde.
     * @return true, wenn alle Pruefungen bestanden wurden.
     */
    private boolean checkForm() {
        boolean valid = true;

        // Benutzernamen
        String userName = etUsername.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            etUsername.setError("Gib deinen Benutzernamen ein.");
            valid = false;
        } else {
            etUsername.setError(null);
        }

        // E-Mail-Adresse
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

        // Passwort
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


    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        firebaseAuth.signOut();

        showToast("Konto wurde erfolgreich erstellt!");
        closeProgressBar();
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseListener);
        }
    }
}