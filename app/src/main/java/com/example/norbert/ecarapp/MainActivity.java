package com.example.norbert.ecarapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



import org.json.JSONArray;

import java.util.concurrent.ExecutionException;

import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public class MainActivity extends AppCompatActivity {



    Button bttnTestConnection;
    EditText textIn;
    TextView txt1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttnTestConnection = (Button) findViewById(R.id.button);
        txt1 = (TextView) findViewById(R.id.textView2);
        textIn = (EditText) findViewById(R.id.editText);



        bttnTestConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    try {
                        DBConnectionHandler dbcon = new DBConnectionHandler();
                        String result = (String)dbcon.execute(textIn.getText().toString()).get();
                        String res = result.toString();


                        Retrofit r = new Retrofit.Builder().baseUrl("http://pi-bo.dd-dns.de:8080/ContextAware/api/v1/").build();

                        //String res = "";

                        txt1.setText(res);
                    }catch(ExecutionException e){}
                }catch(InterruptedException e){}

            }});

    }
}
