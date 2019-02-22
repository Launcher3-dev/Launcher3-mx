package com.android.mxlibrary.util;

import android.util.Log;

public class XLog {

    public static final String TAG_GU = "TAG_GU --- ";
    public static final String TAG_GU_STATE = "TAG_GU_STATE --- ";

    private static final boolean sEnablePrint = true;

    public static String getTag() {
        if (!sEnablePrint) {
            return "";
        }
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
        return "[" + traceElement.getFileName() + " | "
                + traceElement.getLineNumber() + " | "
                + traceElement.getMethodName() + "]";
    }

    public static void i(String tag, String log) {
        if (sEnablePrint) {
            Log.i(tag, log);
        }
    }

    public static void e(String tag, String log) {
        if (sEnablePrint) {
            Log.e(tag, log);
        }
    }

    public static void d(String tag, String log) {
        if (sEnablePrint) {
            Log.d(tag, log);
        }
    }

    public static void w(String tag, String log) {
        if (sEnablePrint) {
            Log.w(tag, log);
        }
    }

    public static void v(String tag, String log) {
        if (sEnablePrint) {
            Log.v(tag, log);
        }
    }

}
