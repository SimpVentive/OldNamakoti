package com.unitol.namakoti.util;

import android.util.Log;

public final class Debug {
    public static final boolean DEBUG_FLAG = com.unitol.namakoti.BuildConfig.DEBUG_FLAG;

    public static void e(String tag, String msg) {
        if (DEBUG_FLAG) {
            Log.e(tag, msg);
        }
    }

}
