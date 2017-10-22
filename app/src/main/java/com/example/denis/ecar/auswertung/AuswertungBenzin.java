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

    public double getKostenBenzin(double dVerbrauchBenzin,double dBenzinpreis)//
    {//Ausgabe in Euro, Verbrauch BMW 750i xDrive 11.9l/100km
        return getdStrecke()/100*dVerbrauchBenzin*dBenzinpreis;
    }
    //TODO: Ausstoß-Klasse!
    public double getCO2CO2AusstossElektro(double dVerbrauchElektro) // Quellen: http://www.bmub.bund.de/fileadmin/Daten_BMU/Download_PDF/Verkehr/emob_klimabilanz_2015_bf.pdf, 2-> http://www.umweltbundesamt.de/themen/klima-energie/energieversorgung/strom-waermeversorgung-in-zahlen#Strommix
    {//Ausgabe in kg /// dEmissionen = 527g pro kWh für 2016
        return (getdStrecke()/100*dVerbrauchElektro*527)/1000; //CO2 Ausstoß für ein Elektroauto Strecke/100*Verbrauch()(Gramm pro kWh 2016->527) // Benzin (vorerst) -> https://legacy.bmw.com/com/de/newvehicles/7series/sedan/2012/showroom/efficiency/efficientdynamics.html
    }
    public double getCO2AusstossBenzin(double dEmissionen) // Quellen: http://www.bmub.bund.de/fileadmin/Daten_BMU/Download_PDF/Verkehr/emob_klimabilanz_2015_bf.pdf, 2-> http://www.umweltbundesamt.de/themen/klima-energie/energieversorgung/strom-waermeversorgung-in-zahlen#Strommix
    {//dEmissionen für BMW 750i xDrive (Vorerst) -> https://legacy.bmw.com/com/de/newvehicles/7series/sedan/2012/showroom/efficiency/efficientdynamics.html
        // 217g pro km
        return (getdStrecke()*dEmissionen)/1000; //CO2 Ausstoß für ein Benzinauto Strecke * CO2-Emissionswert! TODO: (Gramm pro kWh 2016->527) // Benzin (vorerst) -> https://legacy.bmw.com/com/de/newvehicles/7series/sedan/2012/showroom/efficiency/efficientdynamics.html
    }
    public double getCO2Einsparung(double dEmissionen, double dVerbrauchElektro)
    {//Ausgabe in kg
        return getCO2AusstossBenzin(dEmissionen)-getCO2CO2AusstossElektro(dVerbrauchElektro);
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
