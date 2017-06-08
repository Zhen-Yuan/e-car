package com.example.norbert.ecarapp;

/**
 * Created by Norbert on 08.06.2017.
 */

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        return getTeams2();
    }

    static final String BASE_URL = "http://pi-bo.dd-dns.de:8080/ContextAware/api/v1/"; //https://git.eclipse.org/r/";

    public List<Team> teams = null;





    public void getTeams() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        NorbertAPI norbertAPI = retrofit.create(NorbertAPI.class);

        Call<List<Team>> call = norbertAPI.loadTeams("status:open");
        call.enqueue(new Callback<List<Team>>() {

            @Override
            public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {
                if(response.isSuccessful()) {
                    //List<String> str = response.body();
                    List<Team> teamsList = response.body();
                    for(int i=0;i< teamsList.size();i++) {
                        System.out.println("ID: "+ teamsList.get(i).id + " - Name: " + teamsList.get(i).name);
                    }
                } else {
                    System.out.println("Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Team>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



    public static List<Team> getTeams2() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        NorbertAPI norbertAPI = retrofit.create(NorbertAPI.class);

        Call<List<Team>> call = norbertAPI.loadTeams("status:open");

        try{
            Response res = call.execute();
            if (res.isSuccessful()) {
                //List<String> str = response.body();
                List<Team> teamsList = (List<Team>) res.body();
                for(int i=0;i< teamsList.size();i++) {
                    System.out.println("ID: "+ teamsList.get(i).id + " - Name: " + teamsList.get(i).name);
                }
                return teamsList;

            } else {
                System.out.println("Error: " + res.errorBody());
            }

        }catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

}