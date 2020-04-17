package com.google.android.libraries.gsa.launcherclient;

final class NotifyStatusRunnable implements Runnable {

    private final LauncherClient launcherClient;

    NotifyStatusRunnable(LauncherClient launcherClient) {
        this.launcherClient = launcherClient;
    }

    public final void run() {
        this.launcherClient.notifyStatusChanged(0);
    }
}
