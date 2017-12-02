package com.example.denis.ecar.datenbank;

/**
 * Created by Raja on 04.10.2017..
 */

public class EcarUser {


    private String firebaseid;
    private int uid;
    private int sid;


    public EcarUser(String fbase, int setid) {
        this.firebaseid = fbase;
        this.sid = setid;
    }

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirebaseid() {
        return firebaseid;
    }
    public void setFirebaseid(String firebaseid) {
        this.firebaseid = firebaseid;
    }

    public int getSid() {
        return sid;
    }
    public void setSid(int sid) {
        this.sid = sid;
    }

    @Override
    public String toString() {
        String output = "Id: "+uid+"\nFirebaseId: "+firebaseid+"\nSettingID: "+sid;
        return output;
    }
}
