package com.example.denis.ecar.sharedPref;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.denis.ecar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class UpdateLoginActivity extends AppCompatActivity implements ValueEventListener {

    private FirebaseAuth firebaseAuth;
    private User user;
    private String passwort;

    private EditText mail;
    private Button bttnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_login);

        init();
    }


    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();

        user = new User();
        user.setId(firebaseAuth.getCurrentUser().getUid());
        user.contextDataDB(this);

        mail = (EditText) findViewById(R.id.etEmail);
        bttnSubmit = (Button) findViewById(R.id.bttnSubmit);

        bttnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }


    /**
     * Startet ein AlertDialog, in dem das Passwort eingegeben muss, um die Aenderung zu bestaetigen.
     */
    private void update() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View pwView = (LayoutInflater.from(this)).inflate(R.layout.input_pw, null);
        builder.setTitle(R.string.confirmChange).setView(pwView);
        final Dialog dialog = builder.create();
        final EditText pw = (EditText)pwView.findViewById(R.id.etPassword);

        pwView.findViewById(R.id.bttnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(pw.getText().toString()).isEmpty() && pw.getText().toString().length() > 5) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser == null) {
                        return;
                    }
                    passwort = pw.getText().toString();
                    user.setPassword(passwort);
                    reauthenticate();
                    dialog.dismiss();
                } else {
                    showToast("Bitte gib dein neues Passwort ein.");
                }
            }
        });
        pwView.findViewById(R.id.bttnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }


    /**
     * Aktualisiert den Login-Token mit der neuen E-Mail-Adresse.
     */
    private void reauthenticate(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if( firebaseUser == null ){
            return;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),
                user.getPassword());
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if( task.isSuccessful() ){
                            updateData();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });
    }

    /**
     * Aktualisiert die E-Mail-Adresse in der Firebase DB und Auth.
     */
    private void updateData(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if( firebaseUser == null ){
            return;
        }
        firebaseUser.updateEmail(mail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            user.setEmail(mail.getText().toString());
                            user.updateDB();
                            showToast("E-Mail-Adresse aktualisiert.");
                            finish();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });
    }


    private void showToast(String message) {
        Toast.makeText(UpdateLoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue( User.class );
        user.setEmail(u.getEmail());
    }

    @Override
    public void onCancelled(DatabaseError firebaseError) {
        Toast.makeText(this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}