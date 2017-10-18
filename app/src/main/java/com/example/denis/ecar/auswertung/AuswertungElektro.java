package com.example.denis.ecar.auswertung;

/**
 * Created by Denis on 18.10.2017.
 */

public class AuswertungElektro
{
    private double dStrecke, dStrompreis;
    public AuswertungElektro(double dStrecke, double dStrompreis)
    {
        this.dStrecke = dStrecke;
        this.dStrompreis = dStrompreis;
    }

    public double getKostenGesamteStrecke()// Spritpreis als Übergabeparameter. ACHTUNG!!! Prüfen, ob es sich um Super, Super+, Diesel, E"X" handelt!
    {
        return getdStrecke()/100*getdStrompreis();
    }
    public double getKostenProKm() // ACHTUNG! Siehe getKostenGesamteStrecke()...
    {
        return getKostenGesamteStrecke()/getdStrecke();
    }

    public void getCO2Einsparung() // Quellen: http://www.bmub.bund.de/fileadmin/Daten_BMU/Download_PDF/Verkehr/emob_klimabilanz_2015_bf.pdf, 2-> http://www.oekoheizstrom.de/wieviel-co2-emissionen-pro-kwh-kilowattstunde-strom-2344/
    {
        //TODO: Muss noch implementiert werden
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
}
