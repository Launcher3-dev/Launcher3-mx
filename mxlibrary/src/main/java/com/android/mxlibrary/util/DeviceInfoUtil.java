package com.android.mxlibrary.util;

import android.os.Build;

public final class DeviceInfoUtil {

    public static String getPhoneModel() {
        return Build.MODEL;
    }

}
