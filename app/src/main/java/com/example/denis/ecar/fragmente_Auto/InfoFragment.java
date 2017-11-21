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
    private TextView tv_info;
    private EcarDataSource dS;
    private List<EcarCar> infolist;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.fragment_info,container,false);
        init();
        //TODO: Globale ID variable um passende Info für ein Bild auszugeben.

        tv_info.setText(infolist.get(MainActivity.selectedCarId.getivar()).toString());
        MainActivity.selectedCarId.setListener(new SelectedIdVariable.ChangeListener() {
            @Override
            public void onChange() {
                tv_info.setText(infolist.get(MainActivity.selectedCarId.getivar()).toString());
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
        tv_info = (TextView)v.findViewById(R.id.tv_Info);
        tv_info.setMovementMethod(new ScrollingMovementMethod());//Macht tv_info scrollable
        dS = new EcarDataSource(getActivity());
        dS.open();
        infolist = dS.getCarType(3);
        dS.close();

    }
}
