package com.example.denis.ecar.datenbank;

import android.graphics.Bitmap;

/**
 * Created by Raja on 21.10.2017..
 */

public class EcarCar {

    private int cid;
    private String name;
    private String manufacturer;
    private String description;
    private double emission;
    private double consumption;
    private Bitmap carpic;
    private int fid;

    public EcarCar(String name, String hersteller, String beschreibung, double emissionen, double verbrauch, Bitmap bild, int treibstoff) {
        this.name = name;
        this.manufacturer = hersteller;
        this.description = beschreibung;
        this.emission = emissionen;
        this.consumption = verbrauch;
        this.carpic = bild;
        this.fid = treibstoff; // ausgelagert in FUEL. Bestimmt die Art von Wert, welcher in der Tabelle verbrauch(z.B kw/h).
    }

    public int getCid() {
        return cid;
    }
    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getEmission() {
        return emission;
    }
    public void setEmission(double emission) {
        this.emission = emission;
    }

    public double getConsumption() {
        return consumption;
    }
    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public Bitmap getCarpic() {
        return carpic;
    }
    public void setCarpic(Bitmap carpic) {
        this.carpic = carpic;
    }

    public int getFid() {
        return fid;
    }
    public void setFid(int fid) {
        this.fid = fid;
    }

    @Override
    public String toString() {
        String output = "Id: "+cid+
                "\nName: "+name+
                "\nHersteller: "+manufacturer+
                "\nBeschreibung:\n"+description+
                "\nEmissionen: "+emission+
                "\nVerbrauch: "+consumption+
                "\nTreibstoffID: "+fid;
        return output;
    }
}
