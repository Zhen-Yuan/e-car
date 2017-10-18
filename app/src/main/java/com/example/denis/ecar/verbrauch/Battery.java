package com.example.denis.ecar.verbrauch;

/**
 * Created by Denis on 04.10.2017.
 */
//TODO: Ladezyklen (Verlauf der Ladezeit eines Akkus/Batterie), Kontext berücksichtigen (Reifen, Temp, Status der Batterie)
public class Battery
{
    private double dPhase;// Phase -> 1 || 3 Akzeptierte Werte für iPhase
    private double dStromstaerke; // 16A || 32A " für iStromstaerke
    private double dSpannung; // 230V || 400V " iSpannung
    private String strStatus;

    public Battery(int iPhase, int iStromstaerke, int iSpannung)
    {
        this.dPhase = iPhase;
        this.dStromstaerke = iStromstaerke;
        this.dSpannung = iSpannung;
    }

    public double getladeleistung()
    {
        //TODO: Stern: Ladeleistung := sqrt(3) * Spannung * Stromstärke
        //Ladeleistung Einphasenwechselstrom
        if(dPhase == 1 && dSpannung ==230 && dStromstaerke ==16)
        {
            setStrStatus("Ladeleistung, Einphasenwechselstrom");
            return dPhase*dSpannung*dStromstaerke; // in kW
        }else if(dPhase == 3 && dSpannung ==230 && dStromstaerke ==32) // Ladeleistung (Drehstrom, Dreiphasenwechselstrom)|Stern
        {
            setStrStatus("Ladeleistung, Einphasenwechselstrom");
            return dPhase*dSpannung*dStromstaerke; // in kW
        }else
        {
            setStrStatus("-1 -> Ladeleistung, Fall nicht vorhanden.");
            return -1.0; // Rückgabe, falls keiner dieser Fälle eintritt
        }

    }
    //Ergebnis der Ladezeit in (h)
    public double berechneLadezeit37e(double dKapazität, double dEnergieverbrauch, double dStrecke)
    {
			/*
			    Ladeleistung (Einphasenwechselstrom):
				Ladeleistung (3,7 kW) = Phasen (1) * Spannung (230 V) * Stromstärke (16 A)

				Ladeleistung (Drehstrom, Dreiphasenwechselstrom), Sternschaltung:
				Ladeleistung (22 kW) = Phasen (3) * Spannung (230 V) * Stromstärke (32 A)

				Alternativ: Ladeleistung (Drehstrom, Dreiphasenwechselstrom), Dreieckschaltung:
				Ladeleistung (22 kW) = Wurzel (3) * Spannung (400 V) * Stromstärke (32 A)
			 */
        return dKapazität/3.7; // Gesamte Ladezeit
    }
    public double berechneLadezeit37(double dKapazität, double dEnergieverbrauch, double dStrecke)
    {
			/*
			    Ladeleistung (Einphasenwechselstrom):
				Ladeleistung (3,7 kW) = Phasen (1) * Spannung (230 V) * Stromstärke (16 A)

				Ladeleistung (Drehstrom, Dreiphasenwechselstrom), Sternschaltung:
				Ladeleistung (22 kW) = Phasen (3) * Spannung (230 V) * Stromstärke (32 A)

				Alternativ: Ladeleistung (Drehstrom, Dreiphasenwechselstrom), Dreieckschaltung:
				Ladeleistung (22 kW) = Wurzel (3) * Spannung (400 V) * Stromstärke (32 A)
			 */
        return dKapazität/3.7 * getPausen(dStrecke, dKapazität); // Gesamte Ladezeit
    }
    public double berechneLadezeit22e(double dKapazität, double dEnergieverbrauch, double dStrecke)
    {
			/*
			    Ladeleistung (Einphasenwechselstrom):
				Ladeleistung (3,7 kW) = Phasen (1) * Spannung (230 V) * Stromstärke (16 A)

				Ladeleistung (Drehstrom, Dreiphasenwechselstrom), Sternschaltung:
				Ladeleistung (22 kW) = Phasen (3) * Spannung (230 V) * Stromstärke (32 A)

				Alternativ: Ladeleistung (Drehstrom, Dreiphasenwechselstrom), Dreieckschaltung:
				Ladeleistung (22 kW) = Wurzel (3) * Spannung (400 V) * Stromstärke (32 A)
			 */
        return dKapazität/22; //Ladezeit einmal aufladen
    }
    public double berechneLadezeit22(double dKapazität, double dEnergieverbrauch, double dStrecke)
    {
			/*
			    Ladeleistung (Einphasenwechselstrom):
				Ladeleistung (3,7 kW) = Phasen (1) * Spannung (230 V) * Stromstärke (16 A)

				Ladeleistung (Drehstrom, Dreiphasenwechselstrom), Sternschaltung:
				Ladeleistung (22 kW) = Phasen (3) * Spannung (230 V) * Stromstärke (32 A)

				Alternativ: Ladeleistung (Drehstrom, Dreiphasenwechselstrom), Dreieckschaltung:
				Ladeleistung (22 kW) = Wurzel (3) * Spannung (400 V) * Stromstärke (32 A)
			 */
        return dKapazität/22 *getPausen(dStrecke, dKapazität);
    }
    public double ladezeit(double dKapazitaet, double dLadeleistung) //!!!! Batteriekapaztät in kWh!!!!!!!
    {                      //!!!! Batteriekapaztät in kWh!!!!!!!
        return dKapazitaet/dLadeleistung;//!!!! Batteriekapaztät in kWh!!!!!!!
    }                      //!!!! Batteriekapaztät in kWh!!!!!!!

    public double reichweite(double dKapazitaet, double dEnergieverbrauch)//Reichweine in km (Rückgabe), Verbrauch = x kWh/100km. nur x angeben!
    {// NICHT GENAU !!!! Sehr ungenau TODO: Reichweite genauer rechnerisch ermitteln
        dEnergieverbrauch = dEnergieverbrauch/100;
        return dKapazitaet/dEnergieverbrauch*100;
    }

    private boolean plausibilitaetspruefung(double iPhase, double iStromstaerke, double iSpannung)
    {
        if(dPhase == 1 && dSpannung ==230 && dStromstaerke ==16)
        {
            return true;
        }else if(dPhase == 3 && dSpannung ==230 && dStromstaerke ==32) // Ladeleistung (Drehstrom, Dreiphasenwechselstrom)|Stern
        {
            return true;
        }else
        {
            setStrStatus("Plausibilitätsprüfung nicht bestanden.");
            return false;
        }
    }

    public String getStrStatus() {
        return strStatus;
    }

    private void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    public double getPausen(double dStrecke, double dReichweite) //
    {
        return dStrecke/dReichweite; // Gibt Anzahl der Ladepausen an
    }
}
