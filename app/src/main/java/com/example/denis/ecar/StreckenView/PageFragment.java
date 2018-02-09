package com.example.denis.ecar.StreckenView;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.denis.ecar.R;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarDbHelper;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment {
    TextView textView;
    double gesTime = 0;
    private EcarDataSource dataSource;

    //private FloatingActionButton fab;
    private CircleImageView imgEdit;
    private PassData passData;
    private static final String IMAGEVIEW_TAG = "icon cut";


    public PageFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
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

        dataSource.open();
        Cursor cur = dataSource.getSessionDay(bund.getInt("day"));
        dataSource.close();

        long epoch = bund.getInt("day");
        String date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date (epoch*1000));
        textView.setTextSize(20);
        textView.setText("Strecken für den "+date.toString()+"\n");
        if(cur == null){
            return view;
        }
        cur.moveToFirst();
        int idIndex = cur.getColumnIndex(EcarDbHelper.COLUMN_SESSION_ID);
        int idmin = cur.getColumnIndex("min");
        int idmax = cur.getColumnIndex("max");
        gesTime = 0;
        for(int i=0; i<cur.getCount();i++) {
            double min = cur.getInt(idmin);
            double max = cur.getInt(idmax);
            double zeitspanne = max - min;
            gesTime = gesTime + zeitspanne;
            cur.moveToNext();
        }
        cur.moveToFirst();
        for(int i2=0; i2<cur.getCount();i2++){
            final int sessionID = cur.getInt(idIndex);
            final int min = cur.getInt(idmin);
            final int max = cur.getInt(idmax);
            int zeitspanne = max - min;
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);


            final Button btn = new Button(getContext());
            btn.setId(i2+1);
            btn.setBackgroundColor(getResources().getColor(R.color.colorGoogle));
            btn.setText("SessionID: "+sessionID+" - Min: "+min+" - Max: "+max);

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.width = (getResources().getDisplayMetrics().widthPixels*1/3);
            double dispmax = getResources().getDisplayMetrics().heightPixels - 150;
            Log.d("zahl",sessionID+" ID --------------------");
            Log.d("zahl", dispmax+" dispmax");
            Log.d("zahl", gesTime+" gesTime -- zeitstpanne "+zeitspanne);
            double perc = 100/gesTime*zeitspanne;
            final double height = dispmax/100*perc;
            Log.d("zahl", height+" height - %"+ perc);
            params.height = (int)(height);
            Log.d("zahl",params.height+" params hight");

            btn.setLayoutParams(params);
            final int index = i2;
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Button","Clicked Button Index: "+index);
                }
            });
            btn.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    int dragEvent = event.getAction();
                    //final View view = (View) event.getLocalState();

                    switch (dragEvent) {
                        case DragEvent.ACTION_DRAG_ENTERED:
                            // change color of the button
                            btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            // return to usual color
                            btn.setBackgroundColor(getResources().getColor(R.color.colorGoogle));
                            break;
                        case DragEvent.ACTION_DROP:
                            return true;
                        case DragEvent.ACTION_DRAG_ENDED:
                            passData.sendData(sessionID, min, max);
                            showDialog();
                            return true;
                    }
                    return true;
                }
            });
            ll.addView(btn);
            lm.addView(ll);

            //TODO: Ausbauen für eine schönere Ansicht.
            cur.moveToNext();
            if(cur.isAfterLast()){return view;}
        }

        imgEdit = (CircleImageView) view.findViewById(R.id.ivEdit);
        imgEdit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        imgEdit.setTag(IMAGEVIEW_TAG);
        imgEdit.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("","");
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(imgEdit);
                v.startDrag(data, myShadow, null, 0);
                return false;
            }
    });



            //fab = (FloatingActionButton) view.findViewById(R.id.fab);
        //fab.performClick();

                /*.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me) {
                if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(v.getWidth(),  v.getHeight());
                    //set the margins. Not sure why but multiplying the height by 1.5 seems to keep my finger centered on the button while it's moving
                    params.setMargins((int)me.getRawX() - v.getWidth()/2, (int)(me.getRawY() - v.getHeight()*1.5), (int)me.getRawX() - v.getWidth()/2, (int)(me.getRawY() - v.getHeight()*1.5));
                    v.setLayoutParams(params);
                }
                return true;
            }
        });
                /*.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(),mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(fab);
                fab.setVisibility(View.INVISIBLE);
                v.startDrag(dragData,myShadow,null,0);
                return true;
            }
        }); */
        return view;
    }


    public void showDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        EditFragment editDialog = new EditFragment();
        editDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        editDialog.show(fragmentManager, "editFragement");
    /*  FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, editDialog).addToBackStack(null).commit();    */
    }


    interface PassData {
        void sendData(int sid, int min, int max);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // to prevent NullPointerException
        try {
            passData = (PassData) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Fehler beim Abrufen der Daten. Bitte versuchen Sie es erneut.");
        }
    }
}
