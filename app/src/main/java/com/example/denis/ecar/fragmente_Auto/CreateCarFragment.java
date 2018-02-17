package com.example.denis.ecar.fragmente_Auto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.R;
import com.example.denis.ecar.datenbank.EcarCar;
import com.example.denis.ecar.datenbank.EcarDataSource;

/**
 * Created by Rajen on 04.11.2017.
 */

public class CreateCarFragment extends Fragment {
    private View v;
    private TextView tvVerbrauch;
    private TextView tvTank;
    private EditText eTName, eTHersteller, eTBeschreibung;
    private SeekBar sbVerbrauch;
    private SeekBar tank;
    private RadioGroup rgTreibstoff;
    private RadioButton rbB, rbD, rbE;
    private Button Bsave;
    private EcarDataSource dS;
    private Bitmap BmNewcar;
    private String Verbrauchsart;
    private String akkutank;
    private int FuelId;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_create_car,container,false);
        init();

        rgTreibstoff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(i);
                if (checkedRadioButton == rbE){
                    Verbrauchsart = " KWh/100km";
                    akkutank = " KWh Akkukapazität";
                    sbVerbrauch.setMax(300);
                    sbVerbrauch.setProgress(150);
                    tank.setProgress(50);
                    tvVerbrauch.setText(sbVerbrauch.getProgress()+""+Verbrauchsart);
                    tvTank.setText(tank.getProgress()+""+akkutank);
                    FuelId = 3;
                }else{
                    Verbrauchsart = " L/100KM";
                    akkutank = " Liter Tankvolumen";
                    tank.setProgress(50);
                    tvTank.setText(tank.getProgress()+""+akkutank);
                    if(sbVerbrauch.getProgress() > 15){
                        sbVerbrauch.setProgress(8);
                    }
                    sbVerbrauch.setMax(15);
                    tank.setMax(200);
                    if (checkedRadioButton == rbD){
                        FuelId = 2;
                    }else{
                        FuelId = 1;
                    }
                }
            }
        });

        sbVerbrauch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvVerbrauch.setText(i+" "+Verbrauchsart);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tank.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvTank.setText(i+" "+akkutank);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String output = ""+eTName.getText();
                if(output.isEmpty()){
                    Toast toast = Toast.makeText(getActivity(), "Bitte einen Namen für das Auto angeben!", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    try {
                        dS.open();
                        EcarCar car = dS.createEcarCar(
                                output,
                                eTHersteller.getText() + "",
                                eTBeschreibung.getText() + "",
                                0, sbVerbrauch.getProgress(),
                                100/sbVerbrauch.getProgress()*tank.getProgress(),                    //ToDo oder auch nicht
                                tank.getProgress(),
                                BmNewcar,
                                FuelId);
                        dS.close();
                        Toast toast = Toast.makeText(getActivity(), "Das Auto '"+output+"' wurde angelegt!", Toast.LENGTH_LONG);
                        toast.show();


                    }catch (Exception ex){
                        Toast toast = Toast.makeText(getActivity(), "Fehler!", Toast.LENGTH_LONG);
                        toast.show();
                        Log.d("fuck", ""+ex);
                    }
                }

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
        BmNewcar = BitmapFactory.decodeResource(getResources(), R.drawable.cover2);
        tvVerbrauch = (TextView)v.findViewById(R.id.tv_consumption);
        tvTank = (TextView)v.findViewById(R.id.tv_tank);
        eTName = (EditText) v.findViewById(R.id.eT_name);
        eTHersteller = (EditText) v.findViewById(R.id.eT_manu);
        eTBeschreibung = (EditText) v.findViewById(R.id.eT_desc);
        rgTreibstoff = (RadioGroup) v.findViewById(R.id.Fuel_Group);
        rbB = (RadioButton) v.findViewById(R.id.Fuel_B);
        rbD = (RadioButton) v.findViewById(R.id.Fuel_D);
        rbE = (RadioButton) v.findViewById(R.id.Fuel_E);
        sbVerbrauch = (SeekBar) v.findViewById(R.id.Consumption_Seeker);
        tank = (SeekBar) v.findViewById(R.id.consumption_Seeker);
        Bsave = (Button) v.findViewById(R.id.bttn_save);
        dS = new EcarDataSource(getActivity());

        rbB.setText("Benzin");
        rbD.setText("Diesel");
        rbE.setText("Elektro");

        Verbrauchsart = " L/100KM";
        akkutank = " Liter Tankvolumen";
        tank.setMax(200);
        tank.setProgress(50);
        sbVerbrauch.setProgress(50);
        sbVerbrauch.setMax(15);
        sbVerbrauch.setProgress(8);
        FuelId = 1;
        tvVerbrauch.setText(sbVerbrauch.getProgress()+" "+Verbrauchsart);
        tvTank.setText(tank.getProgress()+" "+akkutank);
    }
}
