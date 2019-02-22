package com.android.mxlibrary.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public final class PermissionUtil {

    public static boolean requestStoragePermission(Activity activity) {
        if (isNeedRequestPermission()
                && (!hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                || !hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return true;
        } else {
            return true;
        }
    }


    public static boolean isNeedRequestPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private static boolean hasPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
    }

}
