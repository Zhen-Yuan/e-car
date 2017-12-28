package com.example.denis.ecar.StreckenView;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.denis.ecar.R;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarDbHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment {
    TextView textView;
    private EcarDataSource dataSource;

    public PageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataSource = new EcarDataSource(getContext());
        View view = inflater.inflate(R.layout.fragment_strecke_page_layout,container,false);
        textView = (TextView) view.findViewById(R.id.tvTest);
        Bundle bund = getArguments();
        String massage = Integer.toString(bund.getInt("day"));

        dataSource.open();
        Cursor cur = dataSource.getSessionDay(bund.getInt("day"));
        dataSource.close();
        long epoch = bund.getInt("day");
        String date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date (epoch*1000));
        textView.setText("Strecken für den "+date.toString()+"\n");
        if(cur == null){
            textView.append("Keine Strecken für den Tag gefunden");
            return view;
        };
        cur.moveToFirst();
        int idIndex = cur.getColumnIndex(EcarDbHelper.COLUMN_SESSION_ID);
        int idmin = cur.getColumnIndex("min");
        int idmax = cur.getColumnIndex("max");

        for(int i=0; i<cur.getCount();i++){
            int sessionID = cur.getInt(idIndex);
            int min = cur.getInt(idmin);
            int max = cur.getInt(idmax);
            double zeitspanne = max - min;
            double groeße = 100/86400*zeitspanne;
            //TODO: Ausbauen für eine schönere Ansicht.
            textView.append("SessionID: "+sessionID+" - Min: "+min+" - Max: "+max+"\n");
            cur.moveToNext();
            if(cur.isAfterLast()){return view;}
        }

        /*if (curse == null){
            return view;
        }*/

        /*List<EcarSession> esL = dataSource.getAllEcarSession();
        if(esL == null){
            textView.setText("error + "+massage);
            return view;
        }
        EcarSession esD1 = esL.get(esL.size());*/
        return view;
    }

}
