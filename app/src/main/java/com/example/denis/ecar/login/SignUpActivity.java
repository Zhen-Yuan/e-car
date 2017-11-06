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
import com.example.denis.ecar.sharedPref.User;
import com.google.android.gms.tasks.OnCompleteListener;
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

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseListener;
    private User user;

    private EditText etUsername;
    private CheckBox chckBxShowPW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
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
                user.setId( firebaseUser.getUid() );
                user.saveDB( SignUpActivity.this );
            }
        };

        // View-Elemente
        etUsername = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);
        chckBxShowPW = (CheckBox) findViewById(R.id.chckBxShowPW);

        // leitet weiter zur Home-View, sofern die Anmeldung erfolgreich war
        findViewById(R.id.bttnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProgressBar();
                initUser();
                createAccount();
            }
        });

        // je nachdem, ob die Checkbox ausgewaehlt wurde, wird das Passwort angezeigt
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
    }


    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        firebaseAuth.signOut();

        showToast("Konto wurde erfolgreich erstellt!");
        closeProgressBar();
        finish();
    }


    /**
     * Erstellt einen User mit E-Mail-Adresse und dem Passwort.
     */
    private void createAccount() {
        if (!checkForm()) {
            return;
        }
        // User mit Email und Passwort erstellen
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

        // Benutzernamen pruefen
        String userName = etUsername.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            etUsername.setError("Gib deinen Benutzernamen ein.");
            valid = false;
        } else {
            etUsername.setError(null);
        }

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
