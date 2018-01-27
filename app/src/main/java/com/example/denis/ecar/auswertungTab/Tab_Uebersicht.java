package com.example.denis.ecar.auswertungTab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.denis.ecar.R;


//Für Erklärung siehe Tab_Map
public class Tab_Uebersicht extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public TextView txt_batterie, txt_stromverbrauch, txt_stromkosten, txt_strecke, txt_co2einsparung, txt1, txt2, txt3, txt4, txt5;


    private String mParam1;
    private String mParam2;


    public Tab_Uebersicht() {
    }

    public static Tab_Uebersicht newInstance(String param1, String param2) {
        Tab_Uebersicht fragment = new Tab_Uebersicht();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup                  container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmenttab_uebersicht, container, false);

        txt1 = (TextView) rootView.findViewById(R.id.txt_1);
        txt2 = (TextView) rootView.findViewById(R.id.txt_2);
        txt3 = (TextView) rootView.findViewById(R.id.txt_3);
        txt4 = (TextView) rootView.findViewById(R.id.txt_4);
        txt5 = (TextView) rootView.findViewById(R.id.txt_5);
        txt_batterie = (TextView) rootView.findViewById(R.id.txt_batterie);
        txt_stromverbrauch = (TextView) rootView.findViewById(R.id.txt_stromverbrauch);
        txt_stromkosten = (TextView) rootView.findViewById(R.id.txt_stromkosten);
        txt_strecke = (TextView) rootView.findViewById(R.id.txt_strecke);
        txt_co2einsparung = (TextView) rootView.findViewById(R.id.txt_co2);

        return rootView;
    }



}
