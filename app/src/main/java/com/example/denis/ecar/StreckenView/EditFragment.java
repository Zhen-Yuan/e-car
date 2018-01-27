package com.example.denis.ecar.StreckenView;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.example.denis.ecar.R;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSession;

import org.florescu.android.rangeseekbar.RangeSeekBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends DialogFragment {

    private EcarDataSource dataSource;
    private RangeSeekBar<Integer> rsBar = new RangeSeekBar<>(getContext());
    private FloatingActionButton fabDelete; //, fabSave, fabCancel;
    private int sessionID, minV, maxV;
    private int[] args = new int[2];
    private Dialog dialogFragment;

    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogFragment = super.onCreateDialog(savedInstanceState);
        dialogFragment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit, null);
        init(view);
        builder.setView(view);

        rsBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                args[0] = minValue;
                args[1] = maxValue;
            }
        });
        rsBar.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int dragEvent = event.getAction();
                //final View view = (View) event.getLocalState();
                switch (dragEvent) {
                    case DragEvent.ACTION_DROP:
                        fabDelete.setClickable(false);
                        fabDelete.setBackgroundTintMode(PorterDuff.Mode.DARKEN);
                        break;
                }
                return true;
            }
        });

        fabDelete.setOnLongClickListener(longClickListener);
        builder.setNegativeButton("Verwerfen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
        .setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                changeData(dialogFragment);
            }
        });
        //int width = getResources().getDisplayMetrics().widthPixels;
        //int height = getResources().getDisplayMetrics().heightPixels;
        //dialog.getWindow().setLayout(width, height);
        dialogFragment = builder.create();
        return dialogFragment;
    }


    private void init(View view) {
        fabDelete = (FloatingActionButton) view.findViewById(R.id.fabDelete);
        fabDelete.setOnLongClickListener(longClickListener);

        // Add to layout
        rsBar = (RangeSeekBar) view.findViewById(R.id.rangeSeekBar);
        rsBar.setRangeValues(minV, maxV);
        rsBar.setSelectedMinValue(minV);
        rsBar.setSelectedMaxValue(maxV);
    }


    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData(v.getTag().toString(),mimeTypes, item);
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(fabDelete);
            fabDelete.setVisibility(View.INVISIBLE);
            v.startDrag(dragData,myShadow,null,0);
            return true;
        }
    };


    private void changeData(Dialog dialog) {
        dataSource = new EcarDataSource(getContext());
        dataSource.open();
        dataSource.getSessionByID(sessionID);
        EcarSession ecarSession = dataSource.getSessionByID(sessionID);

        if (minV == args[0] && maxV == args[1]) {
            // case 1: [min,max]
            // delete whole track
            dataSource.deleteEcarSession(ecarSession);
        } else if (minV == args[0] && maxV == args[1]) {
            // case 2: [min,x[
            // delete x-max
        } else if (minV == args[0] && maxV != args[1]) {
            // case 3: ]y,max]
            // delete y-min
        } else if (minV != args[0] && maxV == args[1]) {
            // case 4: [min,x[ u ]y,max]
            // update current session: [min,x[
            // add new session: ]y,max]
            String name = dataSource.getSessionByID(sessionID).getName();
        }
        dataSource.close();
        dialog.dismiss();
    }



    protected void displayReceivedData(int sid, int min, int max) {
        minV = min;
        maxV = max;
        sessionID = sid;
    }


    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }
}
