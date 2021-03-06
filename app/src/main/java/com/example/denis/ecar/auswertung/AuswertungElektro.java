package com.example.denis.ecar.auswertung;

/**
 * Created by Denis on 18.10.2017.
 */

public class AuswertungElektro
{
    private double dStrecke, dStrompreis, dEmissionen;
    public AuswertungElektro(double dStrecke, double dStrompreis, double dEmissionen)
    {
        this.dStrecke = dStrecke;
        this.dStrompreis = dStrompreis;
        this.dEmissionen = dEmissionen;
    }

    public AuswertungElektro()
    {    }

    //Todo Größenordnungen? Km/m;Kwh
    public double getKosten(double dVerbrauchElektro)// strompreis aktuell ~29.16 cent quelle https://www.stromauskunft.de/strompreise/strompreise-2017/
    {//ausgabe in Euro (0.2916) 22.1kWh Tesla Model S
        return dVerbrauchElektro*getdStrompreis()*getdStrecke()/100;
    }

    public double getBatterieProzent(double dStrecke, double dVerbrauch, double dReichweite)
    {
        return (dStrecke*100)/dReichweite;
    }

    public double getKostenElektroProKm(double dVerbrauchElektro)
    {
        return getKosten(dVerbrauchElektro)/getdStrecke(); // € pro km
    }

    //Getter/Setter
    public double getdStrecke() {
        return dStrecke;
    }

    public void setdStrecke(double dStrecke) {
        this.dStrecke = dStrecke;
    }

    public double getdStrompreis() {
        return dStrompreis;
    }

    public void setdStrompreis(double dStrompreis) {
        this.dStrompreis = dStrompreis;
    }

    public double getdEmissionen() {
        return dEmissionen;
    }

    public void setdEmissionen(double dEmissionen) {
        this.dEmissionen = dEmissionen;
    }

}
