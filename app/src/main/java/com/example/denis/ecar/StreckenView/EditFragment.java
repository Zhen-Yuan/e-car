package com.example.denis.ecar.StreckenView;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.denis.ecar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends DialogFragment {

    private TextView tvMin, tvMax, tvResult;
    private SeekBar sb;
    private FloatingActionButton fabDelete; //, fabSave, fabCancel;
    private int sessionID, p, minValue, maxValue;

    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit, null);
        init(view);
        builder.setView(view);

        tvMin.setText(minValue);
        tvMax.setText(maxValue);
        tvResult.setText(minValue + (sb.getProgress()));
        sb.setMax(maxValue-minValue);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                p = minValue + progress;
                tvResult.setText(p);
            }
        });
        sb.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int dragEvent = event.getAction();
                //final View view = (View) event.getLocalState();
                if (dragEvent == DragEvent.ACTION_DROP) {
                    // TODO: Check above or beneath progress
                }
                return true;
            }
        });

        builder.setNegativeButton("Verwerfen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
        .setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO: Aenderungen speichern
            }
        });
        //int width = getResources().getDisplayMetrics().widthPixels;
        //int height = getResources().getDisplayMetrics().heightPixels;
        //dialog.getWindow().setLayout(width, height);
        dialog = builder.create();
        return dialog;
    }

    private void init(View view) {
        tvMin = (TextView) view.findViewById(R.id.tvMinTime);
        tvMax = (TextView) view.findViewById(R.id.tvMaxTime);
        tvResult = (TextView) view.findViewById(R.id.tvResult);

        fabDelete = (FloatingActionButton) view.findViewById(R.id.fabDelete);
        fabDelete.setOnLongClickListener(longClickListener);

        sb = (SeekBar) view.findViewById(R.id.sb_distance);
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


    protected void displayReceivedData(int sid, int min, int max) {
        minValue = min;
        maxValue = max;
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
