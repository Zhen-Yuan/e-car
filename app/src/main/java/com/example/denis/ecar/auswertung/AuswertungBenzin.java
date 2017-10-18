package com.example.denis.ecar.auswertung;

/**
 * Created by Denis on 18.10.2017.
 */

public class AuswertungBenzin {

    private double dStrecke, dSpritpreis;
    public AuswertungBenzin(double dStrecke, double dSpritpreis)
    {
        this.dStrecke = dStrecke;
        this.dSpritpreis = dSpritpreis;
    }

    public double getKostenGesamteStrecke()// Spritpreis als Übergabeparameter. ACHTUNG!!! Prüfen, ob es sich um Super, Super+, Diesel, E"X" handelt!
    {
        return getStrecke()/100*getdSpritpreis();
    }
    public double getKostenProKm() // ACHTUNG! Siehe getKostenGesamteStrecke()...
    {
        return getKostenGesamteStrecke()/getStrecke();
    }

    //Getter/Setter
    public double getStrecke() {
        return dStrecke;
    }

    public void setStrecke(double strecke) {
        dStrecke = strecke;
    }
    public double getdSpritpreis() {
        return dSpritpreis;
    }

    public void setdSpritpreis(double dSpritpreis) {
        this.dSpritpreis = dSpritpreis;
    }
}
