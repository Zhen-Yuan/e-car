package com.example.denis.ecar.sharedPref;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import static com.facebook.FacebookSdk.getApplicationContext;


public class LinkAccountsFragment extends Fragment implements ValueEventListener,
        GoogleApiClient.OnConnectionFailedListener, DatabaseReference.CompletionListener {

    public static final String TAG = "settings_link_account";

    private View view;
    private TextView tvName, tvEmail;
    private EditText etName, etEmail, etPassword;
    private Button bttnSubmit;

    private User user;
    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN_GOOGLE = 9001;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_link_accounts, container, false);

        init();
        initOnClick();
        return view;
    }

    public void init(){
        // FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookAccessData(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() { }
            @Override
            public void onError(FacebookException error) {
                makeToast(error.getMessage());
            }
        });

        // GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        firebaseAuth = FirebaseAuth.getInstance();
        user = new User();
        try {
            user.setId(firebaseAuth.getCurrentUser().getUid());
            DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(user.getId());
            firebaseDB.addListenerForSingleValueEvent(this);
        }catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        tvName = (TextView)view.findViewById(R.id.tvName);
        tvEmail = (TextView)view.findViewById(R.id.tvEmail);
        etName = (EditText)view.findViewById(R.id.etName);
        etEmail = (EditText)view.findViewById(R.id.etEmail);
        etPassword = (EditText)view.findViewById(R.id.etPassword);
        bttnSubmit = (Button)view.findViewById(R.id.bttnSubmit);
        updateUI();
        changeBttnLabels();
    }


    protected void initUser() {
        user.setName(etName.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.setPassword(etPassword.getText().toString());
    }


    protected void initOnClick() {
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName();
            }
        });

        tvEmail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateMail();
            }
        });

        bttnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bttnSubmit.getText().equals("Registrieren")) {
                    initUser();
                    createAccount(user.getEmail(), user.getPassword());
                }
                updatePW();
            }
        });

        (view.findViewById(R.id.bttnFB)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLinked(FacebookAuthProvider.PROVIDER_ID)) {
                    unlinkProvider(FacebookAuthProvider.PROVIDER_ID);
                    return;
                }
                LoginManager.getInstance().logInWithReadPermissions(getActivity(),
                        Arrays.asList("public_profile", "email", "user_friends"));
            }
        });

        (view.findViewById(R.id.bttnGoogle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLinked(GoogleAuthProvider.PROVIDER_ID)) {
                    unlinkProvider(GoogleAuthProvider.PROVIDER_ID);
                    return;
                }
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
            }
        });
    }

    private void changeBttnLabels() {
        setButtonLabel(R.id.bttnFB, FacebookAuthProvider.PROVIDER_ID,
                R.string.linkFB, R.string.unlinkFB);

        setButtonLabel(R.id.bttnGoogle, GoogleAuthProvider.PROVIDER_ID,
                R.string.linkGoogle, R.string.unlinkGoogle);
    }

    private void setButtonLabel(int bttnId, String providerId, int linkId, int unlinkId) {
        if (isLinked(providerId)) {
            ((Button)view.findViewById(bttnId)).setText(unlinkId);
        } else {
            ((Button)view.findViewById(bttnId)).setText(linkId);
        }
    }

    private void showHideFields(boolean status, int... ids) {
        for (int id: ids) {
            view.findViewById(id).setVisibility(status ? View.VISIBLE : View.GONE);
        }
    }

    private void updateUI() {
        if (!isLinked(EmailAuthProvider.PROVIDER_ID)) {
            showHideFields(false, R.id.tvName, R.id.tvEmail);
            showHideFields(true, R.id.etName, R.id.etEmail, R.id.etPassword);
            bttnSubmit.setText(R.string.Registrieren);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN_GOOGLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();
            if(account == null){
                makeToast("Die Anmeldung ist fehlgeschlagen. Bitte versuchen Sie es erneut.");
                return;
            }
            googleAccessData(account.getIdToken());
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void updateName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View nameView = (LayoutInflater.from(getContext())).inflate(R.layout.input_name, null);
        builder.setTitle(R.string.changeName).setView(nameView);
        final Dialog dialog = builder.create();
        final EditText name = (EditText)nameView.findViewById(R.id.etName);
        nameView.findViewById(R.id.bttnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(name.getText().toString()).isEmpty() && name.getText().toString().length() > 1) {
                    user.setName(name.getText().toString());
                    user.updateDB(LinkAccountsFragment.this);
                    tvName.setText(user.getName());
                    makeToast("Benutzername erfolgreich geändert.");
                    dialog.dismiss();
                } else {
                    makeToast("Bitte gib deinen neuen Namen ein.");
                }
            }
        });
        nameView.findViewById(R.id.bttnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }


    private void updateMail() {
        startActivity(new Intent(getContext(), UpdateLoginActivity.class));
    }

    private void updatePW() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View pwView = (LayoutInflater.from(getContext())).inflate(R.layout.input_pw, null);
        builder.setTitle(R.string.changePassword).setView(pwView);
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
                    firebaseUser.updatePassword(pw.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                        makeToast("Passwort aktualisiert.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    makeToast(e.getMessage());
                                }
                            });
                    dialog.dismiss();
                } else {
                    makeToast("Bitte gib dein neues Passwort ein.");
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


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ link provider methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void createAccount(String email, String pw) {
        linkProvider("email", email, pw);
    }

    private void facebookAccessData(AccessToken accessToken) {
        linkProvider("facebook", (accessToken != null ? accessToken.getToken() : null));
    }

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
            credential = provider.equalsIgnoreCase("email") ?
                    EmailAuthProvider.getCredential(token[0], token[1] ) : credential;

            firebaseAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            changeBttnLabels();
                            makeToast("Konto erfolgreich mit " + provider + " verknüpft");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    makeToast(e.getMessage());
                }
            });
            if (provider.equalsIgnoreCase("email")) {
                user.setId(firebaseAuth.getCurrentUser().getUid());
                user.updateDB(LinkAccountsFragment.this);
            }
        } else {
            firebaseAuth.signOut();
        }
    }

    /**
     * Trennt die Verbindung zum angegebenen Provider zum Account.
     * @param providerId enthaelt die ProviderID, von dem getrennt werden soll
     */
    private void unlinkProvider(final String providerId) {
        firebaseAuth.getCurrentUser().unlink(providerId)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            makeToast("Verbindung getrennt.");
                            changeBttnLabels();
                            if (isLastProvider(providerId)){
                                user.setId(firebaseAuth.getCurrentUser().getUid());
                                user.removeDB(LinkAccountsFragment.this);
                            }
                        } else {
                            Log.w(TAG, "fehlgeschlagen", task.getException());
                            makeToast("Fehlgeschlagen.");
                            return;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Error: " + e);
            }
        });
    }

    /**
     * Prueft, ob der im Paramenter angegebene Provider mit dem Acc verbunden ist.
     * @param providerId enthaelt den zu pruefenden Provider.
     * @return false, wenn dies nicht der Fall ist und true, wenn es zutrifft
     */
    private boolean isLinked(String providerId) {
        //if(providerId != null) {
        for (UserInfo userInfo : firebaseAuth.getCurrentUser().getProviderData()) {
            if (userInfo.getProviderId().equals(providerId)) {
                return true;
            }
        }
        //}
        return false;
    }

    /**
     * Ueberprueft, ob der letzte Provider EmailAuth ist
     * @param providerId beinhaltet die Provider-ID
     * @return true, wenn der Fall eintrifft
     */
    private boolean isLastProvider(String providerId) {
        int size = firebaseAuth.getCurrentUser().getProviders().size();
        return (size == 0 || (size == 1 && providerId.equals(EmailAuthProvider.PROVIDER_ID)));
    }


    private void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }


// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ database methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
            Log.e(TAG, databaseError.getMessage());
        }
        firebaseAuth.getCurrentUser().delete();
        firebaseAuth.signOut();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        makeToast(connectionResult.getErrorMessage());
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue(User.class);
        tvName.setText(u.getName());
        tvEmail.setText(u.getEmail());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) { }
}