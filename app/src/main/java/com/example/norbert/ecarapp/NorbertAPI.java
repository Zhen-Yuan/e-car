package com.example.norbert.ecarapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by Norbert on 08.06.2017.
 */

public interface NorbertAPI {
    @GET("team")
    Call<List<Team>> loadTeams(@Query("q") String status);



}
