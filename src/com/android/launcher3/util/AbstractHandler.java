package com.android.launcher3.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class AbstractHandler<T> extends Handler {

    private WeakReference<T> weak;

    public AbstractHandler(T t) {
        this.weak = new WeakReference<T>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        if (null == weak || null == weak.get()) {
            return;
        }
        handleMessage(msg, weak.get());
        super.handleMessage(msg);
    }

    protected abstract void handleMessage(Message msg, T t);
}
