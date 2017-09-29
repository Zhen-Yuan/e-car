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

import com.example.denis.ecar.HomeActivity;
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
    private FirebaseAuth firebaseAuth;
    private String email;

    /** ----- GUI-Elemente ----- */
    private TextView txtVwTitel;
    private TextView txtVwPasswortHinweis;
    private EditText txtUserName;
    private EditText txtEMail;
    private EditText txtPasswort;
    private CheckBox chckBxPasswortAnzeigen;
    private Button bttnAnmelden;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        init();
        initOnClick();
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
    @SuppressWarnings("ConstantConditions")
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        txtVwTitel = (TextView)findViewById(R.id.txtVwTitel);
        txtVwPasswortHinweis = (TextView)findViewById(R.id.txtVwPasswortHinweis);
        txtUserName = (EditText)findViewById(R.id.etName);
        txtEMail = (EditText)findViewById(R.id.etMail);
        txtPasswort = (EditText)findViewById(R.id.etPasswort);
        chckBxPasswortAnzeigen = (CheckBox)findViewById(R.id.chckBxPwAnzeigen);
        bttnAnmelden = (Button)findViewById(R.id.bttnAnmelden);

        // versucht, das EXTRA, falls vorhanden, dem String email zu uebergeben
        Bundle inBundle = getIntent().getExtras();
        try {
            email = inBundle.get("email").toString();
        } catch(NullPointerException e) {
            email = null;
        }
    }


    /**
     * Initialisiert onClick Elemente und dessen Event.
     */
    private void initOnClick() {
        // leitet weiter zur Home-View, sofern die Anmeldung erfolgreich war
        findViewById(R.id.bttnAnmelden).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bttnAnmelden.getText().equals("Anmelden")) {
                    signIn(txtPasswort.getText().toString());
                } else if (bttnAnmelden.getText().equals("Registrieren")) {
                    createAccount(txtEMail.getText().toString(), txtPasswort.getText().toString());
                }
            }
        });

        // je nachdem, ob die Checkbox ausgewaehlt wurde, wird das Passwort angezeigt
        findViewById(R.id.chckBxPwAnzeigen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chckBxPasswortAnzeigen.isChecked()) {
                    txtPasswort.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    txtPasswort.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        // verschickt eine E-Mail mit Passwort-Reset
        findViewById(R.id.txtVwPasswortVergessen).setOnClickListener(new View.OnClickListener() {
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
        String userName = txtUserName.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            txtUserName.setError("Gib deinen Benutzernamen ein.");
            valid = false;
        } else {
            txtUserName.setError(null);
        }

        // E-Mail-Adresse ueberpruefen
        String email = txtEMail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtEMail.setError("Gib deine E-Mail-Adresse ein.");
            valid = false;
        } else if(!email.contains("@")) {
            txtEMail.setError("E-Mail-Adresse ist nicht korrekt.");
            valid = false;
        } else {
            int i = email.indexOf('@');
            String domain = email.substring(i);
            i = domain.indexOf('.');
            String suffix = domain.substring(i);
            if (suffix.length()<3 || suffix.length()>4) {
                txtEMail.setError("E-Mail-Adresse ist nicht korrekt.");
                valid = false;
            } else {
                txtEMail.setError(null);
            }
        }

        // Passwort pruefen
        String password = txtPasswort.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPasswort.setError("Gib dein Passwort ein.");
            valid = false;
        } else if (password.length() < 6) {
            txtPasswort.setError("Passwort ist zu kurz.");
        } else {
            txtPasswort.setError(null);
        }
        return valid;
    }


    /**
     * Prueft nur, ob was in das Passwort EditText-Feld eingetragen wurde.
     * @return true, wenn was eingetragen wurde; false, wenn es leer gelassen wurde.
     */
    private boolean checkPasswortForm() {
        boolean valid = true;

        String password = txtPasswort.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPasswort.setError("Gib dein Passwort ein.");
            valid = false;
        } else {
            txtPasswort.setError(null);
        }
        return valid;
    }


    /**
     * Aendert die UI von Registrieren-View zur Anmelden-View.
     * @param email ist die E-Mail-Adresse des Users, falls eine in der Login-View eingegeben wurde
     */
    private void updateUI(String email) {
        if (email != null) {
            txtVwTitel.setText(R.string.Anmelden);
            txtVwPasswortHinweis.setText(R.string.txtVwPasswort);
            bttnAnmelden.setText(R.string.Anmelden);

            findViewById(R.id.etName).setVisibility(View.GONE);
            findViewById(R.id.etMail).setVisibility(View.GONE);
            findViewById(R.id.chckBxPwAnzeigen).setVisibility(View.VISIBLE);
            findViewById(R.id.txtVwPasswortVergessen).setVisibility(View.VISIBLE);
        }
    }


    /**
     * ueberprueft, ob ein User eingeloggt ist, wenn ja, wird direkt zum Homescreen weitergeleitet.
     * @param user uebergibt den aktuellen User
     */
    private void nextActivity(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(EmailSignInActivity.this, HomeActivity.class));
        }
    }
}
