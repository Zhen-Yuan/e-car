package com.example.norbert.ecarapp;

/**
 * Created by Norbert on 08.06.2017.
 */

public class Team {
    public final int id;
    public final String name;

    public Team(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
