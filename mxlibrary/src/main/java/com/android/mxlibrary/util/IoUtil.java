package com.android.mxlibrary.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by yuchuan
 * DATE 2020/4/28
 * TIME 22:25
 */
public final class IoUtil {

    public static void closeSafely(Closeable closeable){
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
