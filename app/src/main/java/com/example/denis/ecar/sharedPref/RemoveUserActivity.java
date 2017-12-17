package com.example.denis.ecar.sharedPref;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.denis.ecar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RemoveUserActivity extends AppCompatActivity
        implements ValueEventListener, DatabaseReference.CompletionListener {

    public static final String TAG = "RemoveUser";

    private User user;
    private FirebaseAuth firebaseAuth;
    private EditText etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_user);

        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }


    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        etPassword = (EditText) findViewById(R.id.etPassword);

        user = new User();
        user.setId(firebaseAuth.getCurrentUser().getUid());
        user.contextDataDB(this);


        findViewById(R.id.bttnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setPassword(etPassword.getText().toString());
                reauthenticate();
            }
        });
    }


    private void reauthenticate() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            return;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), user.getPassword());
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    deleteUser();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RemoveUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        user.removeDB(RemoveUserActivity.this);
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RemoveUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue(User.class);
        user.setEmail(u.getEmail());
    }


    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
            Toast.makeText(RemoveUserActivity.this, databaseError.toException().toString(),
                    Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(RemoveUserActivity.this, "Konto wurde erfolgreich gelöscht",
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}
