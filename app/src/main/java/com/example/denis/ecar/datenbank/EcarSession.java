package com.example.denis.ecar.datenbank;

/**
 * Created by Raja on 02.07.2017..
 */

public class EcarSession {

    private boolean bAuswertung;
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

    public boolean getbAuswertung() { return  bAuswertung; }
    public void setbAuswertung(boolean asw) { this.bAuswertung = asw; }

    @Override
    public String toString() {
        String output = "Id: "+sesid+"\nUserId: "+uid+"\nName: "+name+"\nAusgewertet: "+bAuswertung+"\n";
        return output;
    }
}
