package com.example.denis.ecar.login;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * Created by Shinmei on 22.08.2017.
 */

abstract public class BaseActivity extends AppCompatActivity {

    protected EditText etEmail;
    protected EditText etPassword;
    protected ProgressBar progressBar;


    protected void showSnackbar(String message ){
        Snackbar.make(progressBar, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }


    protected void showToast( String message ){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }


    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }


    abstract protected void initUser();
}