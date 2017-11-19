package com.example.denis.ecar.auswertung;

/**
 * Created by Denis on 26.10.2017.
 */

public class AuswertungCO2
{

    /*
    Als Übergabeparameter entweder AuswertungBenzin, AuswertungElektro-Objekte oder
    double dEmissionenBenzin, double dEmissionenElektro, double dStrecke

    Falls die Methoden nicht selbsterklärend sind, bitte anschreiben!
    Werde dann die Kommentare dann hinzufügen.
     */

    private AuswertungElektro auswertungElektro;
    private AuswertungBenzin auswertungBenzin;
    private double dEmissionenBenzin, dEmissionenElektro, dStrecke;

    public AuswertungCO2(AuswertungElektro auswertungElektro, AuswertungBenzin auswertungBenzin)
    {
        this.auswertungElektro = auswertungElektro;
        this.auswertungBenzin = auswertungBenzin;
    }
    public AuswertungCO2(double dEmissionenBenzin, double dEmissionenElektro, double dStrecke)
    {
    this.dEmissionenBenzin = dEmissionenBenzin;
    this.dEmissionenElektro = dEmissionenElektro;
    this.dStrecke = dStrecke;
    }

    public AuswertungCO2()
    {
    }

    public double getCO2CO2AusstossElektro(double dVerbrauchElektro) // Quellen: http://www.bmub.bund.de/fileadmin/Daten_BMU/Download_PDF/Verkehr/emob_klimabilanz_2015_bf.pdf, 2-> http://www.umweltbundesamt.de/themen/klima-energie/energieversorgung/strom-waermeversorgung-in-zahlen#Strommix
    {//Ausgabe in kg /// dEmissionen = 527g pro kWh für 2016
        if (auswertungElektro == null)
        {
            return (getdStrecke() / 100 * dVerbrauchElektro * 527) / 1000; //CO2 Ausstoß für ein Elektroauto strecke/100*verbrauch()(Gramm pro kWh 2016->527) // Benzin (vorerst) -> https://legacy.bmw.com/com/de/newvehicles/7series/sedan/2012/showroom/efficiency/efficientdynamics.html
        }else
        {
            return (auswertungElektro.getdStrecke() / 100 * dVerbrauchElektro * 527) / 1000;
        }
    }
    public double getCO2AusstossBenzin() // Quellen: http://www.bmub.bund.de/fileadmin/Daten_BMU/Download_PDF/Verkehr/emob_klimabilanz_2015_bf.pdf, 2-> http://www.umweltbundesamt.de/themen/klima-energie/energieversorgung/strom-waermeversorgung-in-zahlen#Strommix
    {//dEmissionen für BMW 750i xDrive (Vorerst) -> https://legacy.bmw.com/com/de/newvehicles/7series/sedan/2012/showroom/efficiency/efficientdynamics.html
        // 217g pro km
        if (auswertungBenzin == null) {
            return (getdStrecke() * getdEmissionenBenzin()) / 1000; //CO2 Ausstoß für ein Benzinauto Strecke * CO2-Emissionswert! TODO: (Gramm pro kWh 2016->527) // Benzin (vorerst) -> https://legacy.bmw.com/com/de/newvehicles/7series/sedan/2012/showroom/efficiency/efficientdynamics.html
        }else
        {
            return (auswertungBenzin.getdStrecke() * getdEmissionenBenzin()) / 1000;
        }
    }
    public double getCO2Einsparung(double dVerbrauchElektro)
    {//Ausgabe in kg
        return getCO2AusstossBenzin()-getCO2CO2AusstossElektro(dVerbrauchElektro);
    }

    //Getter Setter
    public double getdEmissionenBenzin() {
        return dEmissionenBenzin;
    }

    public void setdEmissionenBenzin(double dEmissionenBenzin) {
        this.dEmissionenBenzin = dEmissionenBenzin;
    }

    public double getdEmissionenElektro() {
        return dEmissionenElektro;
    }

    public void setdEmissionenElektro(double dEmissionenElektro) {
        this.dEmissionenElektro = dEmissionenElektro;
    }

    public double getdStrecke() {
        return dStrecke;
    }

    public void setdStrecke(double dStrecke) {
        this.dStrecke = dStrecke;
    }
}
