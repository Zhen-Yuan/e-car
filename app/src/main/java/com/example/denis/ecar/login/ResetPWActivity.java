package com.example.denis.ecar.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.denis.ecar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPWActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etEmail;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pw);

        init();
    }


    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.PasswortReset);

        etEmail = (EditText) findViewById(R.id.etEmail);
        // verschickt eine E-Mail mit Passwort-Reset
        findViewById(R.id.bttn_Reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordReset();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }


    /**
     * Verschickt mithilfe von firebase eine E-Mail an den User um sein Passwort zu resetten.
     */
    private void sendPasswordReset() {
        firebaseAuth.sendPasswordResetEmail(etEmail.getText().toString()).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            etEmail.setText("");
                            Toast.makeText(
                                    ResetPWActivity.this, "E-Mail gesendet.", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(
                                    ResetPWActivity.this,"Gescheitert! Versuchen Sie es erneut",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
