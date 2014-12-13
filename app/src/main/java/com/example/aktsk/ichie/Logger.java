package com.example.aktsk.ichie;

import android.util.Log;

public class Logger {

    public static <E> void log(E msg) {
        Log.d("ICHIE", String.valueOf(msg));
    }
}
