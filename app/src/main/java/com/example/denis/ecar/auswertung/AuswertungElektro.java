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

    //TODO: Ausstoß-Klasse!
    public double getCO2CO2AusstossElektro(double dVerbrauchElektro) // Quellen: http://www.bmub.bund.de/fileadmin/Daten_BMU/Download_PDF/Verkehr/emob_klimabilanz_2015_bf.pdf, 2-> http://www.umweltbundesamt.de/themen/klima-energie/energieversorgung/strom-waermeversorgung-in-zahlen#Strommix
    {//Ausgabe in kg /// dEmissionen = 527g pro kWh für 2016
        return (getdStrecke()/100*dVerbrauchElektro*527)/1000; //CO2 Ausstoß für ein Elektroauto strecke/100*verbrauch()(Gramm pro kWh 2016->527) // Benzin (vorerst) -> https://legacy.bmw.com/com/de/newvehicles/7series/sedan/2012/showroom/efficiency/efficientdynamics.html
    }
    public double getCO2AusstossBenzin() // Quellen: http://www.bmub.bund.de/fileadmin/Daten_BMU/Download_PDF/Verkehr/emob_klimabilanz_2015_bf.pdf, 2-> http://www.umweltbundesamt.de/themen/klima-energie/energieversorgung/strom-waermeversorgung-in-zahlen#Strommix
    {//dEmissionen für BMW 750i xDrive (Vorerst) -> https://legacy.bmw.com/com/de/newvehicles/7series/sedan/2012/showroom/efficiency/efficientdynamics.html
        // 217g pro km
        return (getdStrecke()*getdEmissionen())/1000; //CO2 Ausstoß für ein Benzinauto Strecke * CO2-Emissionswert! TODO: (Gramm pro kWh 2016->527) // Benzin (vorerst) -> https://legacy.bmw.com/com/de/newvehicles/7series/sedan/2012/showroom/efficiency/efficientdynamics.html
    }
    public double getCO2Einsparung(double dEmissionen, double dVerbrauchElektro)
    {//Ausgabe in kg
        return getCO2AusstossBenzin()-getCO2CO2AusstossElektro(dVerbrauchElektro);
    }

    public double getKostenElektro(double dVerbrauchElektro)// strompreis aktuell ~29.16 cent quelle https://www.stromauskunft.de/strompreise/strompreise-2017/
    {//ausgabe in Euro (0.2916) 22.1kWh Tesla Model S
        return getdStrecke()/100*dVerbrauchElektro*getdStrompreis();
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
