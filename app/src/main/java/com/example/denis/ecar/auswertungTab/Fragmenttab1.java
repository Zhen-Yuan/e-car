package com.example.denis.ecar.auswertungTab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.denis.ecar.R;

public class Fragmenttab1 extends Fragment {
    // Initparameter
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Parameter
    private String mParam1;
    private String mParam2;


    public Fragmenttab1() {

    }

    /**
     * Mit dieser "Factory"-Methode werden neue Instanzen erstellt!!!!!!
     * (Mit den Übergebenen Parametern)
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return Neue Instanz Fragmenttab1.
     */
    public static Fragmenttab1 newInstance(String param1, String param2) {
        Fragmenttab1 fragment = new Fragmenttab1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup                  container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fragmenttab1, container, false);
        return rootView;
    }
}
