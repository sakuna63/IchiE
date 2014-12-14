package com.example.aktsk.ichie;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtil {
    private static final String KEY_ID = "key_id";

    public static void saveLogined(Context context) {
        getPref(context).edit().putBoolean(KEY_ID, true).apply();
    }

    public static boolean isLogined(Context context) {
        return getPref(context).getBoolean(KEY_ID, false);
    }

    private static SharedPreferences getPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
