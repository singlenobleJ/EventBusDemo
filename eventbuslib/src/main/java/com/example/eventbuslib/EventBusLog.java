package com.example.eventbuslib;


import android.util.Log;

/**
 * @author: lilinjie
 * @date: 2019-05-25 12:48
 * @description:
 */
public class EventBusLog {
    private static final String TAG = EventBusLog.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static void log(String message) {
        if (DEBUG) {
            Log.d(TAG, message);
        }
    }
}
