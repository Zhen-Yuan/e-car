package com.example.denis.ecar.StreckenView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.R;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSession;

import org.florescu.android.rangeseekbar.RangeSeekBar;


public class EditActivity extends AppCompatActivity {

    private EcarDataSource dataSource;
    private int sessionID, minV, maxV;
    private double[] args = new double[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        sessionID = intent.getExtras().getInt("sessionId");
        minV = intent.getExtras().getInt("min");
        maxV = intent.getExtras().getInt("max");
        init();
    }


    private void init() {

        String msg = "Min:  " + minV + "\n Max: " + maxV;

        TextView tvEdit = (TextView) findViewById(R.id.tvDescription);
        tvEdit.setText(msg);

        RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<>(this);
        // Set the range
        rangeSeekBar.setRangeValues(minV, maxV);
        rangeSeekBar.setSelectedMinValue(minV+10);
        rangeSeekBar.setSelectedMaxValue(maxV-10);
        rangeSeekBar.setTextAboveThumbsColorResource(android.R.color.holo_blue_light);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                args[0] = minValue;
                args[1] = maxValue;
            }
        });
        FrameLayout layout = (FrameLayout) findViewById(R.id.seekbar_placeholder);
        layout.addView(rangeSeekBar);

        Button bttnCancel = (Button) findViewById(R.id.bttn_cancel);
        bttnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditActivity.this, "pressed cancel", Toast.LENGTH_SHORT)
                        .show();
                backToPageFragment();
            }
        });

        Button bttnSubmit = (Button) findViewById(R.id.bttn_submit);
        bttnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditActivity.this, "pressed submit", Toast.LENGTH_SHORT)
                        .show();
                changeData();
            }
        });
    }

    private void changeData() {
        dataSource = new EcarDataSource(this);
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
        backToPageFragment();
    }


    private void backToPageFragment() {
        startActivity(new Intent(EditActivity.this, ViewStrecken.class));
        finish();
    }
}
