package com.example.denis.ecar.StreckenView;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
        LinearLayout lmsplit = (LinearLayout) view.findViewById(R.id.splitlay);
        final LinearLayout lm = (LinearLayout) view.findViewById(R.id.linlay);
        lm.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        dataSource.open();
        Cursor cur = dataSource.getSessionDay(bund.getInt("day"));
        dataSource.close();

        long epoch = bund.getInt("day");
        String date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date (epoch*1000));
        textView.setTextSize(20);
        textView.setText("Strecken für den "+date.toString()+"\n");
        if(cur == null){
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
            int zeitspanne = max - min;
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);


            final Button btn = new Button(getContext());
            btn.setId(i+1);
            btn.setText("SessionID: "+sessionID+" - Min: "+min+" - Max: "+max);
            params.width = (getResources().getDisplayMetrics().widthPixels*1/3);
            double dispmax = getResources().getDisplayMetrics().heightPixels - 150;
            //double height = dispmax/100*(100/maxall*zeitspanne);
            params.height = (int)(300);

            btn.setLayoutParams(params);

            final int index = i;
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Button","Clicked Button Index: "+index);
                }
            });

            ll.addView(btn);
            lm.addView(ll);

            //TODO: Ausbauen für eine schönere Ansicht.
            cur.moveToNext();
            if(cur.isAfterLast()){return view;}
        }
        return view;
    }

}
