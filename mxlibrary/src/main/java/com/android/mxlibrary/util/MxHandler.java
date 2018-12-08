package com.android.mxlibrary.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 防止内存泄漏的Handler
 *
 * @param <T>
 */
public abstract class MxHandler<T> extends Handler {

    private WeakReference<T> weakReference;

    public MxHandler(T t) {
        weakReference = new WeakReference<T>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        if (weakReference == null || weakReference.get() == null) {
            return;
        }
        handleMessage(weakReference.get(), msg);
        super.handleMessage(msg);
    }

    protected abstract void handleMessage(T t, Message msg);
}
