package com.example.denis.ecar;

/**
 * Created by Raja on 02.07.2017.
 */

public class EcarSession {

    private int sesid, uid;
    private String name;

    public EcarSession(int user, String name) {
        this.uid = user;
        this.name = name;
    }

    public int getSesid() {
        return sesid;
    }
    public void setSesid(int sesid) {
        this.sesid = sesid;
    }

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String output = "Id: "+sesid+" UserId: "+uid+" Name: "+name;
        return output;
    }
}
