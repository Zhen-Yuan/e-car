package com.example.denis.ecar.fuellmethoden;

import android.provider.ContactsContract;

/**
 * Created by denis on 19.05.17.
 */

public class DataGenerator
{
    public DataGenerator()
    {
        //Konstruktor
    }
    public String fillStr(int n)
    {//Gibt String aus, zum Testen von beispielsweise n.setText()-Operationen
        String str1 = "\tFill ";
        String str2 = "Fill ";
        String str3 = "Fill ";
        String str4 = "\n";
        String output = null;
        for (int i = 0;i<=n;i++)
        {
            if(i == 0)
            {
                output = str1 + str2 + str3 + str1 + str2 + str3 + str4;
            }else {
                output = output + str1 + str2 + str3 + str1 + str2 + str3 + str4;
            }
        }
        return output;
    }
    public String teslaModelSinfo()//TODO: Texte online, aus Textdateien, oder aus der lok. Datenbank beziehen.
    {
        return "Energieverbrauch\n" +
                "Model S 75: 185 Wh/km\n" +
                "Model S 75D: 186 Wh/km\n" +
                "Model S 90D: 189 Wh/km\n" +
                "Model S P90D: 200 Wh/km\n" +
                "Model S 100D: 189 Wh/km\n" +
                "Model S P100D: 200 Wh/km\n\n\nAufladen\n" +
                "Universaler Mobile Connector mit rotem 11 kW-Industriestrom-Adapter (400V, 16A) und 3 kW \"Schuko\"-Steckdosenadapter (230V, 13A)\n" +
                "Zugang zum wachsenden Tesla Supercharger-Netzwerk\n\n"+"Innenansicht\n" +
                "17-Zoll-Touchscreen\n" +
                "Bordkarten und Navigation mit Gratis-Updates für 7 Jahre\n" +
                "Schlüsselloser Zugang\n" +
                "WiFi- und Mobilfunk-Konnektivität\n" +
                "Fernbedienung über Mobile-App für Smartphones\n" +
                "Türgriffe mit automatischem Einzug\n" +
                "Elektrische Fensterheber mit Tastendruck-Automatik\n" +
                "HD-Rückfahrkamera\n" +
                "Bluetooth-Freisprechsystem\n" +
                "Sprachgesteuerte Funktionen\n" +
                "AM-, FM-, DAB+ und Internet-Radio\n" +
                "Spiegel mit Abblendautomatik\n" +
                "LED-Ambienteleuchten im Innenraum\n" +
                "Beleuchtete Türgriffe\n" +
                "Elektrisch einklappbare, beheizbare Seitenspiegel mit Positionsspeicher\n" +
                "Zwei USB-Anschlüsse für Mediengeräte und Nebenverbraucher\n" +
                "12 V-Netzbuchse\n" +
                "Beheizbare Vordersitze mit 12 elektrischen Verstellfunktionen, Memoryfunktion und Fahrerprofilspeicher\n" +
                "Frontstauraum (statt sperrigem Motor!), Gepäckraum hinten und 60/40 umklappbare Rücksitze - 894 Liter Stauraum";
    }
}
