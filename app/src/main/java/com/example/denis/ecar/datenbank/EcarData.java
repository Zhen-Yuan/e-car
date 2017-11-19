package com.example.denis.ecar.datenbank;

/**
 * Created by Raja  on 25.06.2017.
 */

public class EcarData {

    private double data;
    private int did, vid, sesid;
    private int time;

    public EcarData(double data, int session, int value) {
        this.data = data;
        this.vid = value;
        this.sesid = session;
    }

    public int getDid() {
        return did;
    }
    public void setDid(int did) {
        this.did = did;
    }

    public double getData() {
        return data;
    }
    public void setData(double data) {
        this.data = data;
    }

    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }

    public int getVid() {
        return vid;
    }
    public void setVid(int vid) {
        this.vid = vid;
    }

    public int getSesid() {
        return sesid;
    }
    public void setSesid(int sesid) {
        this.sesid = sesid;
    }

    @Override
    public String toString() {
        String output = "Id: "+did+" Data: "+data+" Time: "+time+" Session: "+sesid+" Value: "+vid;
        return output;
    }
}

