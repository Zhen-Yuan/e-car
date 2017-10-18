package com.example.denis.ecar.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.MainActivity;
import com.example.denis.ecar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Login oder Registrierung mit E-Mail-Adresse und Passwort.
 *
 * Created by Shinmei on 22.08.2017.
 */

public class EmailSignInActivity extends BaseActivity {

    private static final String TAG = "EmailPasswordLogin";
    private String email;
    private FirebaseAuth firebaseAuth;
    /** ----- GUI-Elemente ----- */
    private TextView tvTitle;
    private TextView tvPasswordHint;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private CheckBox chckBxShowPW;
    private Button bttnSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        init();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // UI wird angepasst, je nachdem, ob in der Login-View eine E-Mail-Adresse eingetragen wurde
        updateUI(email);
    }


    /**
     * Initialisiert den Firebase Authenticator und diverse View-Elemente.
     */
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        tvTitle= (TextView)findViewById(R.id.tvTitle);
        tvPasswordHint = (TextView)findViewById(R.id.tvPasswordHint);
        etUsername = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        chckBxShowPW = (CheckBox)findViewById(R.id.chckBxShowPW);
        bttnSignIn = (Button)findViewById(R.id.bttnSignIn);

        // versucht, das EXTRA, falls vorhanden, dem String email zu uebergeben
        Bundle inBundle = getIntent().getExtras();
        try {
            email = inBundle.get("email").toString();
        } catch(NullPointerException e) {
            email = null;
        }
        initOnClick();
    }


    /**
     * Initialisiert onClick Elemente und dessen Event.
     */
    private void initOnClick() {
        // leitet weiter zur Home-View, sofern die Anmeldung erfolgreich war
        findViewById(R.id.bttnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bttnSignIn.getText().equals("Anmelden")) {
                    signIn(etPassword.getText().toString());
                } else if (bttnSignIn.getText().equals("Registrieren")) {
                    createAccount(etEmail.getText().toString(), etPassword.getText().toString());
                }
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

        // verschickt eine E-Mail mit Passwort-Reset
        findViewById(R.id.tvResetPW).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordReset(email);
            }
        });
    }


    /**
     * Erstellt einen User mit E-Mail-Adresse und dem Passwort.
     * @param email enthaelt die eingegebene E-Mail-Adresse
     * @param password enthaelt das eingegebene Passwort
     */
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!checkForm()) {
            return;
        }
        showProgressDialog();
        // User mit Email und Passwort erstellen
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            sendEmailVerification(user);
                            nextActivity(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailSignInActivity.this, "Anmeldung fehlgeschlagen.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }


    /**
     * Meldet den User mit seiner E-Mailadresse von der Login-View zusammen mit dem eingegebenen
     * Passwort via firebase an.
     * @param password enthaelt das eingegebene Passwort
     */
    private void signIn(String password) {
        Log.d(TAG, "signIn:" + email);
        if (!checkPasswortForm()) {
            return;
        }
        showProgressDialog();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            nextActivity(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailSignInActivity.this, "Anmeldung fehlgeschlagen.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }


    /**
     * Verschickt eine Verfizierungs-E-Mail mithilfe von firebase.
     * @param user ist der aktuelle User
     */
    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EmailSignInActivity.this,
                                    "E-Mail-Verfikation gesendet.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(EmailSignInActivity.this,
                                    "Versenden fehlgeschlagen.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * Verschickt mithilfe von firebase eine E-Mail an den User um sein Passwort zu resetten.
     * @param email enthaelt die E-Mail-Adresse des Users
     */
    private void sendPasswordReset(String email) {
        firebaseAuth.sendPasswordResetEmail(email).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(EmailSignInActivity.this,
                                    "E-Mail gesendet.", Toast.LENGTH_SHORT).show();
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


    /**
     * Prueft nur, ob was in das Passwort EditText-Feld eingetragen wurde.
     * @return true, wenn was eingetragen wurde; false, wenn es leer gelassen wurde.
     */
    private boolean checkPasswortForm() {
        boolean valid = true;

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Gib dein Passwort ein.");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }


    /**
     * Aendert die UI von Registrieren-View zur Anmelden-View.
     * @param email ist die E-Mail-Adresse des Users, falls eine in der Login-View eingegeben wurde
     */
    private void updateUI(String email) {
        if (email != null) {
            tvTitle.setText(R.string.Anmelden);
            tvPasswordHint.setText(R.string.txtVwPasswort);
            bttnSignIn.setText(R.string.Anmelden);

            findViewById(R.id.etName).setVisibility(View.GONE);
            findViewById(R.id.etEmail).setVisibility(View.GONE);
            findViewById(R.id.chckBxShowPW).setVisibility(View.VISIBLE);
            findViewById(R.id.tvResetPW).setVisibility(View.VISIBLE);
        }
    }


    /**
     * ueberprueft, ob ein User eingeloggt ist, wenn ja, wird direkt zum Homescreen weitergeleitet.
     * @param user uebergibt den aktuellen User
     */
    private void nextActivity(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(EmailSignInActivity.this, MainActivity.class));
        }
    }
}
