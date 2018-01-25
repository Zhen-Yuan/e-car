package com.example.denis.ecar.fragmente_Auto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.denis.ecar.MainActivity;
import com.example.denis.ecar.R;
import com.example.denis.ecar.datenbank.EcarCar;
import com.example.denis.ecar.datenbank.EcarDataSource;

import java.util.List;

/**
 * Created by denis on 29.07.2017.
 */

public class InfoFragment extends Fragment {
    private View v;
    private TextView tv_head, tv_verb, tv_reich, tv_bat, tv_besch;
    private EcarDataSource dS;
    private List<EcarCar> infolist;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.fragment_info,container,false);
        init();
        //TODO: Globale ID variable um passende Info für ein Bild auszugeben.
        tv_head.setText(infolist.get(MainActivity.selectedCarId.getivar()).getManufacturer()+" "+infolist.get(MainActivity.selectedCarId.getivar()).getName());
        tv_verb.setText("Verbrauch: "+infolist.get(MainActivity.selectedCarId.getivar()).getConsumption()+" KW/h");
        tv_reich.setText("Reichweite: "+infolist.get(MainActivity.selectedCarId.getivar()).getRange()+" KM");
        tv_bat.setText("Batterie: "+infolist.get(MainActivity.selectedCarId.getivar()).getPowerstore()+" KW");
        tv_besch.setText(infolist.get(MainActivity.selectedCarId.getivar()).getDescription());

        MainActivity.selectedCarId.setListener(new SelectedIdVariable.ChangeListener() {
            @Override
            public void onChange() {

                tv_head.setText(infolist.get(MainActivity.selectedCarId.getivar()).getManufacturer()+" "+infolist.get(MainActivity.selectedCarId.getivar()).getName());
                tv_verb.setText("Verbrauch: "+infolist.get(MainActivity.selectedCarId.getivar()).getConsumption()+" KW/h");
                tv_reich.setText("Reichweite: "+infolist.get(MainActivity.selectedCarId.getivar()).getRange()+" KM");
                tv_bat.setText("Batterie: "+infolist.get(MainActivity.selectedCarId.getivar()).getPowerstore()+" KW");
                tv_besch.setText(infolist.get(MainActivity.selectedCarId.getivar()).getDescription());
                Log.d("Testlog",""+MainActivity.selectedCarId.getivar());
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void init()
    {
        tv_head = (TextView)v.findViewById(R.id.tvh);
        tv_verb = (TextView)v.findViewById(R.id.tvVerbrauch);
        tv_reich = (TextView)v.findViewById(R.id.tvReichweite);
        tv_bat = (TextView)v.findViewById(R.id.tvBatt);
        tv_besch = (TextView)v.findViewById(R.id.tvdesc);
        tv_besch.setMovementMethod(new ScrollingMovementMethod());//Macht tv_info scrollable
        dS = new EcarDataSource(getActivity());
        dS.open();
        infolist = dS.getCarType(3);
        dS.close();

    }
}
