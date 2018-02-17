package com.example.denis.ecar.auswertung;

/**
 * Created by Denis on 18.10.2017.
 */

public class AuswertungBenzin {

    private double dStrecke;
    private double dSpritpreis;
    private double dEmissionen;

    public AuswertungBenzin(double dStrecke, double dSpritpreis, double dEmissionen)
    {
        this.dStrecke = dStrecke;
        this.dSpritpreis = dSpritpreis;
        this.dEmissionen = dEmissionen;
    }

    public double getKosten(double dVerbrauchBenzin,double dBenzinpreis)//
    {//Ausgabe in Euro, Verbrauch BMW 750i xDrive 11.9l/100km
        return getdStrecke()/100*dVerbrauchBenzin*dBenzinpreis;
    }
    public double getKostenBenzinProKm(double dVerbrauchBenzin,double dBenzinpreis)
    {
        return getKosten(dVerbrauchBenzin,dBenzinpreis)/getdStrecke();
    }
    //Getter/Setter
    public double getdStrecke() {
        return dStrecke;
    }

    public void setdStrecke(double strecke) {
        dStrecke = strecke;
    }
    public double getdSpritpreis() {
        return dSpritpreis;
    }

    public void setdSpritpreis(double dSpritpreis) {
        this.dSpritpreis = dSpritpreis;
    }
    public double getdEmissionen() {
        return dEmissionen;
    }

    public void setdEmissionen(double dEmissionen) {
        this.dEmissionen = dEmissionen;
    }

}
