package com.example.denis.ecar.auswertung;

/**
 * Created by Norbert on 07.01.2018.
 *
 * Wichtige Berechnungen zentral in einer Klasse gesammelt
 *
 */


public abstract class Kalkulationen {

    //Berechnung der Entfernung zwischen zwei Punkten
    public static double calcDist(double lat1, double lon1, double lat2, double lon2) {
        double dist = 0.0;
        double earth = 6371000;
        double lat = Math.toRadians(lat1 - lat2);
        double lng = Math.toRadians(lon1 - lon2);
        double a = Math.sin(lat/2) * Math.sin(lat/2) +
                Math.cos(Math.toRadians(lat2))
                        *Math.cos(Math.toRadians(lat1))
                        *Math.sin(lng/2)
                        *Math.sin(lng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        dist = earth * c;
        return (dist);
    }

    //Berechnung der Geschwindigkeit zwischen zwei Punkten
    public double calcVelocity(double lat1, double lon1, int time1, double lat2, double lon2, int time2){
        double dist = calcDist(lat1, lon1, lat2, lon2);
        double time_s = time1 - time2;
        time_s = Math.abs(time_s);
        double speed_mps = dist / time_s;
        double speed_kph = (speed_mps * 3600.0) / 1000.0;
        return speed_kph;
    }

    //Umrechnung Grad zu Bogenmaß
    public double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    } //Für die zukünftige Verwendung in Maps

    //Umrechnung Bogenmaß zu Grad
    public double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }//Siehe deg2rad


}
