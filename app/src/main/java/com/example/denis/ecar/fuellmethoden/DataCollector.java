package com.example.denis.ecar.fuellmethoden;

import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by denis on 19.05.17.
 */
//Diese Klasse sorgt für Log-Ausgaben
public class DataCollector
{
    public DataCollector()
    {

    }
    public boolean setLOG(MotionEvent event)
    {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("TAG", "touched down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("TAG", "moving: (" + x + ", " + y + ")");
                break;
            case MotionEvent.ACTION_UP:
                Log.i("TAG", "touched up");
                break;
        }
        return true;
    }
}
