package com.example.denis.ecar.datenbank;

import android.graphics.Bitmap;

/**
 * Created by Raja on 04.10.2017.
 */

public class EcarUser {


    private String name;
    private String email;
    private int uid;
    private int sid;
    private Bitmap pic;

    public EcarUser(String name, String mail, int setid) {
        this.name = name;
        this.email = mail;
        this.sid = setid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getSid() {
        return sid;
    }
    public void setSid(int sid) {
        this.sid = sid;
    }

    public Bitmap getPic() {
        return pic;
    }
    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        String output = "Id: "+uid+"\nName: "+name+"\nEmail: "+email+"\nSettingID: "+sid+"\nBild: "+pic+"\n";
        return output;
    }
}
