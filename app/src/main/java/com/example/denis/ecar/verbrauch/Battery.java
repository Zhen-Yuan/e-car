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

    public double ladeleistung()
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
    public double ladezeit(double dKapazitaet, double dLadeleistung) //!!!! Batteriekapaztät in kWh!!!!!!!
    {                      //!!!! Batteriekapaztät in kWh!!!!!!!
        return dKapazitaet/dLadeleistung;//!!!! Batteriekapaztät in kWh!!!!!!!
    }                      //!!!! Batteriekapaztät in kWh!!!!!!!

    public double reichweite(double dKapazitaet, double dEnergieverbrauch)//Reichweine in km (Rückgabe), Verbrauch = x kWh/100km. nur x angeben!
    {
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

}
