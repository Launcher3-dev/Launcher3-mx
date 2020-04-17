package com.google.android.libraries.gsa.launcherclient;

final class ServiceStatusRunnable implements Runnable {

    private final AbsServiceStatusChecker.StatusCallback statusCallback;

    private final  AbsServiceStatusChecker f27b;

    ServiceStatusRunnable(AbsServiceStatusChecker absServiceStatusChecker, AbsServiceStatusChecker.StatusCallback statusCallback) {
        this.f27b = absServiceStatusChecker;
        this.statusCallback = statusCallback;
    }

    public final void run() {
        AbsServiceStatusChecker.assertMainThread();
        this.statusCallback.isRunning(false);
    }
}
